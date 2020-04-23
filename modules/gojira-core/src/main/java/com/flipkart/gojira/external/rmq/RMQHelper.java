package com.flipkart.gojira.external.rmq;

import com.flipkart.gojira.core.injectors.TestExecutionInjector;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RMQHelper implements IRMQHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(RMQHelper.class);

  /**
   * @param clientId, id of client which is under test
   * @param exchangeName, exchange on which msg needs to be published
   * @param routingKey, routing key being used at exchange to determine queue
   * @param data, message data being published
   * @param properties, headers, clientId, reply-to etc properties of msg
   * @param mandatory
   * @throws RMQPublishException, if unable to publish to exchange
   */
  @Override
  public void publish(
      String clientId,
      String exchangeName,
      byte[] routingKey,
      byte[] data,
      AMQP.BasicProperties properties,
      boolean mandatory)
      throws RMQPublishException {
    Channel channel =
        TestExecutionInjector.getInjector()
            .getInstance(IRMQManager.class)
            .getChannelByClient(clientId);
    if (channel == null) {
      LOGGER.error("Unable to publish as cannot instantiate channel");
      throw new RMQPublishException("Unable to publish as cannot instantiate channel");
    }
    try {
      String routingParam = new String(routingKey);
      channel.basicPublish(exchangeName, routingParam, mandatory, properties, data);
    } catch (Exception e) {
      LOGGER.error("Unable to publish to RMq channel", e);
      throw new RMQPublishException("Unable to publish to RMq channel", e);
    }
  }
}
