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

package com.flipkart.gojira.execute.rmq;

import com.flipkart.gojira.core.GlobalConstants;
import com.flipkart.gojira.core.Mode;
import com.flipkart.gojira.execute.TestExecutor;
import com.flipkart.gojira.external.rmq.IRmqHelper;
import com.flipkart.gojira.external.rmq.RmqPublishException;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.rmq.RmqTestDataType;
import com.flipkart.gojira.models.rmq.RmqTestRequestData;
import com.flipkart.gojira.models.rmq.RmqTestResponseData;
import com.google.inject.Inject;
import com.rabbitmq.client.AMQP;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RMQ Executor to publish messages i.e replay RMQ messages which were profiled.
 */
public class DefaultRmqTestExecutor
    implements TestExecutor<TestData<RmqTestRequestData, RmqTestResponseData, RmqTestDataType>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRmqTestExecutor.class);
  private final IRmqHelper rmqHelper;

  @Inject
  public DefaultRmqTestExecutor(final IRmqHelper rmqHelper) {
    this.rmqHelper = rmqHelper;
  }

  /**
   * When executing adds testId to http headers.
   *
   * @param testData testData which is used for invoking execution
   * @param clientId identifier to indicate which system hit
   * @throws RmqPublishException when unable to publish messages to RMQ
   */
  @Override
  public void execute(
      TestData<RmqTestRequestData, RmqTestResponseData, RmqTestDataType> testData, String clientId)
      throws RmqPublishException {
    String testId = testData.getId();
    RmqTestRequestData requestData = testData.getRequestData();
    AMQP.BasicProperties basicProperties = requestData.getProperties();
    Map<String, Object> headers = basicProperties.getHeaders();
    if (headers == null) {
      headers = new HashMap<>();
    }
    headers.put(GlobalConstants.TEST_HEADER, testId);
    headers.put(GlobalConstants.MODE_HEADER, Mode.TEST.name());
    AMQP.BasicProperties alteredProperties = basicProperties.builder().headers(headers).build();
    try {
      rmqHelper.publish(
          clientId,
          requestData.getExchangeName(),
          requestData.getRoutingKey(),
          requestData.getData(),
          alteredProperties,
          requestData.isMandatory());
    } catch (RmqPublishException e) {
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
        "published data to exchange: "
            + exchangeName
            + " with routingKey: "
            + new String(routingKey)
            + " with data "
            + new String(data)
            + " with clientId: "
            + clientId
            + " for testId: "
            + testId
            + ". ");
  }
}
