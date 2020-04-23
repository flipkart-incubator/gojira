package com.flipkart.gojira.external.rmq;

import com.rabbitmq.client.AMQP;

public interface IRMQHelper {

  /**
   * @param clientId, id of client which is under test
   * @param exchangeName, exchange on which msg needs to be published
   * @param routingKey, routing key being used at exchange to determine queue
   * @param data, message data being published
   * @param properties, headers, clientId, reply-to etc properties of msg
   * @param mandatory
   * @throws RMQPublishException, if unable to publish to exchange
   */
  void publish(
      String clientId,
      String exchangeName,
      byte[] routingKey,
      byte[] data,
      AMQP.BasicProperties properties,
      boolean mandatory)
      throws RMQPublishException;
}
