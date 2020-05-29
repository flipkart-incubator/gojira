/*
 * Copyright 2020 Flipkart Internet, pvt ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.gojira.core;

import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.models.ExecutionData;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.TestDataType;
import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.models.TestResponseData;
import com.flipkart.gojira.queuedsender.TestQueuedSender;
import com.flipkart.gojira.requestsampling.RequestSamplingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link StartEndTestHandler} for mode {@link Mode#PROFILE}.
 *
 * @param <T> type of test-data
 */
public class ProfileStartEndTestHandler<T extends TestDataType> implements StartEndTestHandler<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProfileStartEndTestHandler.class);

  /**
   * Checks if the request falls in the sampling bucket. If yes, it calls {@link
   * ProfileRepository#begin(String)} to start profiling, adds the {@link TestRequestData} to
   * {@link TestData} by calling {@link ProfileRepository#setRequestData(TestRequestData)}
   * and adds the {@link Mode} to {@link ExecutionData#executionMode} by
   * calling the {@link ProfileRepository#setRequestMode(Mode)}
   * In case of any failure, marks the {@link ExecutionData#profileState} as
   * {@link ProfileState#FAILED} to avoid further recording of data.
   *
   * @param id this is the id, which will be used for synchronizing testing across multiple threads
   *     within a single request-response scope.
   * @param requestData this is the request-data with which test is initiated
   * @param requestMode this is the mode of execution of gojira at a request level
   */
  @Override
  public void start(String id, TestRequestData<T> requestData, Mode requestMode) {
    try {
      if (fallsInSamplingBucket()) {
        // add request data to thread-local.
        ProfileRepository.begin(id);
        ProfileRepository.setRequestData(requestData);
        ProfileRepository.setRequestMode(requestMode);
        LOGGER.info("Profiling initiated for id: " + ProfileRepository.getTestData().getId());
      } else {
        LOGGER.info("doesn't fall into this sampling bucket, ignoring profiling for this request");
      }
    } catch (Exception e) {
      ProfileRepository.setProfileState(ProfileState.FAILED);
      LOGGER.warn("error starting test profile data." + " global_per_request_Id: " + id, e);
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
          LOGGER.info("Profiling complete for id : " + ProfileRepository.getTestData().getId());
          ProfileRepository.setResponseData(responseData);
          TestData<TestRequestData<T>, TestResponseData<T>, T> testData =
              ProfileRepository.getTestData();
          if (testData != null) {
            LOGGER.info(
                "Profiling complete for id : "
                    + ProfileRepository.getTestData().getId()
                    + " sending to queuedSender.");
            testQueuedSender.send(testData);
          }
        } catch (Exception e) {
          LOGGER.warn(
              "error writing test profile data to datastore"
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
