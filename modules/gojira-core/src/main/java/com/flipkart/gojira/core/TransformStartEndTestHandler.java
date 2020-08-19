package com.flipkart.gojira.core;

import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.models.ExecutionData;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.TestDataType;
import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.models.TestResponseData;
import com.flipkart.gojira.queuedsender.TestQueuedSender;
import com.flipkart.gojira.requestsampling.RequestSamplingRepository;
import com.flipkart.gojira.serde.SerdeHandlerRepository;
import com.flipkart.gojira.sinkstore.SinkException;
import com.flipkart.gojira.sinkstore.handlers.SinkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformStartEndTestHandler<T extends TestDataType>
    implements StartEndTestHandler<T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransformStartEndTestHandler.class);
  private SerdeHandlerRepository serdeHandlerRepository =
      GuiceInjector.getInjector().getInstance(SerdeHandlerRepository.class);
  /**
   * sinkHandler for reading {@link TestData} and storing results of test execution.
   */
  private SinkHandler sinkHandler = GuiceInjector.getInjector().getInstance(SinkHandler.class);

  /**
   * If id is null or empty, throws a {@link RuntimeException}
   *
   * <p>Reads the test data using {@link SinkHandler#read(String)} and deserializes using {@link
   * SerdeHandlerRepository#getTestDataSerdeHandler()} instance.
   *
   * <p>If testData is null, throws a {@link RuntimeException}
   *
   * <p>Begins execution by calling {@link ProfileRepository#begin(String)} and adds {@link
   * TestData} for execution by calling {@link ProfileRepository#setTestData(TestData)} to make
   * method intercepted and response data recorded in {@link Mode#PROFILE} mode available to
   * transform.
   *
   * <p>It also adds the {@link Mode} to {@link ExecutionData#executionMode} by calling the {@link
   * ProfileRepository#setRequestMode(Mode)}
   *
   * <p>In case of any failure, marks the {@link ExecutionData#profileState} as {@link
   * ProfileState#FAILED} to avoid further recording of data.
   *
   * @param id this is the id, which will be used for synchronizing testing across multiple threads
   *     within a single request-response scope.
   * @param requestData this is the request-data with which test is initiated
   */
  @Override
  public void start(String id, TestRequestData<T> requestData) {
    if (id == null || id.isEmpty()) {
      throw new RuntimeException("invalid test id.");
    }
    try {
      TestData<TestRequestData<T>, TestResponseData<T>, T> testData =
          serdeHandlerRepository
              .getTestDataSerdeHandler()
              .deserialize(sinkHandler.read(id), TestData.class);
      if (testData == null) {
        throw new RuntimeException("no data available against mentioned test id: " + id);
      }

      // add request data to thread-local.
      ProfileRepository.begin(id);
      ProfileRepository.setTestData(testData);
      ProfileRepository.setRequestData(requestData);
      ProfileRepository.setRequestMode(Mode.TRANSFORM);
      LOGGER.info("Profiling initiated for id: " + ProfileRepository.getTestData().getId());

    } catch (Exception e) {
      ProfileRepository.setProfileState(ProfileState.FAILED);
      LOGGER.warn("error starting test transform data." + " global_per_request_Id: " + id, e);
    }
  }

  /**
   * If {@link ExecutionData#profileState} is {@link ProfileState#INITIATED}, and we get an instance
   * of {@link TestQueuedSender}, we add {@link TestResponseData} to {@link TestData} by calling
   * {@link ProfileRepository#setResponseData(TestResponseData)} and sends it to {@link
   * TestQueuedSender}.
   *
   * <p>In case of any exception, we just log.
   *
   * <p>In the finally block, {@link ProfileRepository#end()} is called.
   *
   * @param responseData this is the response-data after the request is processed by the client
   *     application.
   */
  @Override
  public void end(TestResponseData<T> responseData) {
    try {
      TestQueuedSender testQueuedSender =
          GuiceInjector.getInjector().getInstance(TestQueuedSender.class);
      if (ProfileState.INITIATED.equals(ProfileRepository.getProfileState())
          && testQueuedSender != null) {
        try {
          LOGGER.info("Transform complete for id : " + ProfileRepository.getTestData().getId());
          ProfileRepository.setResponseData(responseData);
          ProfileRepository.setTag("testTag");
          TestData<TestRequestData<T>, TestResponseData<T>, T> testData =
              ProfileRepository.getTestData();
          if (testData != null) {
            LOGGER.info(
                "Transforming complete for id : "
                    + ProfileRepository.getTestData().getId()
                    + " sending to queuedSender.");
            testQueuedSender.send(testData);
          }
        } catch (Exception e) {
          LOGGER.warn(
              "error writing test transformed data to datastore"
                  + " global_request_per_id: "
                  + ProfileRepository.getGlobalPerRequestID(),
              e);
        }
      }
    } finally {
      ProfileRepository.end();
    }
  }

  /**
   * Helper method to do time-based sampling.
   *
   * <p>TODO: Use an interface to implement sampling functionality.
   *
   * @return true if request can be sampled.
   */
  private boolean fallsInSamplingBucket() {
    return ((System.nanoTime() % 10000)
        < (GuiceInjector.getInjector()
                .getInstance(RequestSamplingRepository.class)
                .getSamplingPercentage()
            * 100));
  }
}
