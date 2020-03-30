package com.flipkart.gojira.core;

import com.flipkart.gojira.models.rmq.RMQTestRequestData;
import com.rabbitmq.client.AMQP;

public class ProfileRMQFilterHandler extends RMQFilterHandler {

  /**
   * @param exchangeName rmq exchange name
   * @param key key used for producing message to the exchange
   * @param value body used for producing message to the exchange
   * @param basicProperties contains headers, transactional reply-to id and meta data for successful
   *     RMQ operation
   * @param mandatory mandatory flag tells RabbitMq that the message must be routable.
   *     <p>Implementation of this is expected to call {@link
   *     DefaultProfileOrTestHandler#start(String, com.flipkart.gojira.models.TestRequestData)} as
   *     per {@link Mode} specific needs.
   */
  @Override
  protected void handle(
      String exchangeName,
      byte[] key,
      byte[] value,
      AMQP.BasicProperties basicProperties,
      boolean mandatory) {
    String id = getTestId(basicProperties);
    if (id != null) {
      LOGGER.error(
          "Header with name: "
              + FilterConstants.TEST_HEADER
              + " present. But service is not running in  mode. : "
              + ProfileRepository.getMode());
      throw new RuntimeException(
          "Header with name: "
              + FilterConstants.TEST_HEADER
              + " present. But service is not running in  mode. : "
              + ProfileRepository.getMode());
    }
    if (!isExchangeWhitelisted(exchangeName)) {
      LOGGER.error("Exchange is not whitelisted. Exchange name :{}", exchangeName);
      return;
    }
    id = String.valueOf(System.nanoTime()) + Thread.currentThread().getId();
    RMQTestRequestData rmqTestRequestData =
        RMQTestRequestData.builder()
            .setExchangeName(exchangeName)
            .setRoutingKey(key)
            .setData(value)
            .setProperties(basicProperties)
            .setMandatory(mandatory)
            .build();
    try {
      DefaultProfileOrTestHandler.start(id, rmqTestRequestData);
    } catch (Exception e) {
      LOGGER.error("Exception trying to construct RMQTestRequest. ", e);
    }
  }
}
