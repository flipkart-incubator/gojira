package com.flipkart.gojira.external.rmq;

import com.flipkart.gojira.core.injectors.TestExecutionInjector;
import com.flipkart.gojira.external.ExternalConfigRepository;
import com.flipkart.gojira.external.Managed;
import com.flipkart.gojira.external.SetupException;
import com.flipkart.gojira.external.ShutdownException;
import com.flipkart.gojira.external.config.ExternalConfig;
import com.flipkart.gojira.external.config.RMQConfig;
import com.flipkart.gojira.models.rmq.RMQTestDataType;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public enum RMQManager implements IRMQManager, Managed {
  RMQ_MANAGER;

  private static final Logger logger = LoggerFactory.getLogger(RMQManager.class);

  /** @throws SetupException , raised whn unable to setup a connection to RMQ */
  @Override
  public void setup() throws SetupException {
    try {

      RMQTestDataType rmqTestDataType = new RMQTestDataType();
      Map<String, ExternalConfig> externalConfigMap =
          TestExecutionInjector.getInjector()
              .getInstance(ExternalConfigRepository.class)
              .getExternalConfigByType(rmqTestDataType);
      if (!externalConfigMap.isEmpty()) {
        for (Map.Entry<String, ExternalConfig> entry : externalConfigMap.entrySet()) {
          ExternalConfig externalConfig = entry.getValue();
          if (externalConfig != null) {
            RMQConfig rmqConfig = (RMQConfig) externalConfig;
            clientVsChannelMap.put(entry.getKey(), createChannel(rmqConfig));
          }
        }
      }
    } catch (Exception e) {
      logger.error("error setting up rmq connections.", e);
      throw new SetupException("error setting up rmq connections.", e);
    }
  }

  /** @throws ShutdownException , raised when unable to perform shutdown */
  @Override
  public void shutdown() throws ShutdownException {
    try {
      for (Map.Entry<String, Channel> entry : clientVsChannelMap.entrySet()) {
        stopConsumer(entry.getValue());
      }
    } catch (Exception e) {
      logger.error("error closing http connections.", e);
      throw new ShutdownException("error closing http connections.", e);
    }
  }

  /**
   * @param rmqConfig
   * @return a Channel for app to exchange publish
   * @throws SetupException
   */
  private Channel createChannel(RMQConfig rmqConfig) throws SetupException {
    Channel rmqChannel;
    ConnectionFactory factory = new ConnectionFactory();
    Connection connection;
    factory.setUsername(rmqConfig.getUsername());
    factory.setPassword(rmqConfig.getPassword());
    factory.setVirtualHost(rmqConfig.getVirtualHost());
    factory.setAutomaticRecoveryEnabled(rmqConfig.isAutomaticRecoveryEnabled());
    List<Address> addressList = new LinkedList<Address>();
    for (String endpoint : rmqConfig.getEndpoints()) {
      addressList.add(new Address(endpoint, rmqConfig.getPort()));
      logger.info(
          "Adding the RMQ endpoint ["
              + endpoint
              + ":"
              + rmqConfig.getPort()
              + "] in connection factory");
    }
    Address[] addrArr = addressList.toArray(new Address[0]);
    logger.info("Number of nodes connected to in RMQ:" + addrArr.length);
    try {
      connection = factory.newConnection(addrArr);
      rmqChannel = connection.createChannel();
      logger.info(
          "Connection to RMQ established.."
              + rmqChannel.getConnection().getAddress().getHostName());
      return rmqChannel;
    } catch (IOException | TimeoutException e) {
      String errorMsg = "Connection to RMQ could not be established..";
      logger.error(errorMsg);
      throw new SetupException(errorMsg);
    }
  }

  /** @param channel, that needs to be disconnected */
  private void stopConsumer(Channel channel) {
    try {
      logger.info("Stopping Consumer Service");
      if (channel.getConnection().isOpen()) {
        logger.info("closing RabbitMQ channel...");
        channel.getConnection().close();
      }
      if (channel.isOpen()) channel.close();
    } catch (Exception e) {
      logger.error("Exception while closing channel", e);
    }
  }

  @Override
  public Channel getChannelByClient(String client) {
    return clientVsChannelMap.get(client);
  }
}
