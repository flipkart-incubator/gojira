package com.flipkart.gojira.execute.rmq;

import com.flipkart.gojira.core.FilterConstants;
import com.flipkart.gojira.execute.TestExecutionException;
import com.flipkart.gojira.execute.TestExecutor;
import com.flipkart.gojira.external.rmq.IRMQHelper;
import com.flipkart.gojira.external.rmq.RMQPublishException;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.rmq.RMQTestDataType;
import com.flipkart.gojira.models.rmq.RMQTestRequestData;
import com.flipkart.gojira.models.rmq.RMQTestResponseData;
import com.google.inject.Inject;
import com.rabbitmq.client.AMQP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/** RMQ Executor to publish messages i.e replay RMQ messages which were profiled.*/
public class DefaultRMQTestExecutor
    implements TestExecutor<TestData<RMQTestRequestData, RMQTestResponseData, RMQTestDataType>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRMQTestExecutor.class);
  private final IRMQHelper rmqHelper;

  @Inject
  public DefaultRMQTestExecutor(final IRMQHelper rmqHelper) {
    this.rmqHelper = rmqHelper;
  }

  @Override
  public void execute(TestData<RMQTestRequestData, RMQTestResponseData, RMQTestDataType> testData)
      throws TestExecutionException {
    execute(testData, "DEFAULT");
  }

  /**
   * @param testData testData which is used for invoking execution
   * @param clientId identifier to indicate which system hit
   * @throws RMQPublishException, thrown when unable to publish messages to RMQ
   */
  @Override
  public void execute(
      TestData<RMQTestRequestData, RMQTestResponseData, RMQTestDataType> testData, String clientId)
      throws RMQPublishException {
    String testId = testData.getId();
    RMQTestRequestData requestData = testData.getRequestData();
    AMQP.BasicProperties basicProperties = requestData.getProperties();
    Map<String, Object> headers = basicProperties.getHeaders();
    if (headers == null) {
      headers = new HashMap<>();
    }
    headers.put(FilterConstants.TEST_HEADER, testId);
    AMQP.BasicProperties alteredProperties = basicProperties.builder().headers(headers).build();
    try {
      rmqHelper.publish(
          clientId,
          requestData.getExchangeName(),
          requestData.getRoutingKey(),
          requestData.getData(),
          alteredProperties,
          requestData.isMandatory());
    } catch (RMQPublishException e) {
      LOGGER.error(
          "Unable to publish through RMQ with clientId :{} and testID:{}", clientId, testId, e);
      throw e;
    }
    logRecordPublish(
        requestData.getExchangeName(),
        requestData.getRoutingKey(),
        requestData.getData(),
        clientId,
        testId);
  }

  private void logRecordPublish(
      String exchangeName, byte[] routingKey, byte[] data, String clientId, String testId) {
    LOGGER.info(
        String.format(
            "published data to exchange: %s with routingKey: %s with data %s with clientId: %s for testId: %s. ",
            exchangeName, new String(routingKey), new String(data), clientId, testId));
  }
}
