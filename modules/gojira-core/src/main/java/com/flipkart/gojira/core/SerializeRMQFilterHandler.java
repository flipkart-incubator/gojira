package com.flipkart.gojira.core;

import com.rabbitmq.client.AMQP;

public class SerializeRMQFilterHandler extends RMQFilterHandler {

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
    if (id == null) {
      throw new RuntimeException("X-GOJIRA-ID header not present");
    }
    DefaultProfileOrTestHandler.start(id, null);
  }
}
