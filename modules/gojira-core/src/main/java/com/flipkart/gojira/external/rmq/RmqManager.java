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

package com.flipkart.gojira.external.rmq;

import com.flipkart.gojira.core.injectors.TestExecutionInjector;
import com.flipkart.gojira.external.ExternalConfigModule;
import com.flipkart.gojira.external.ExternalConfigRepository;
import com.flipkart.gojira.external.Managed;
import com.flipkart.gojira.external.SetupException;
import com.flipkart.gojira.external.ShutdownException;
import com.flipkart.gojira.external.UpdateException;
import com.flipkart.gojira.external.config.ExternalConfig;
import com.flipkart.gojira.external.config.RmqConfig;
import com.flipkart.gojira.models.rmq.RmqTestDataType;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum RmqManager implements IRmqManager, Managed {
  RMQ_MANAGER;

  private static final Logger LOGGER = LoggerFactory.getLogger(RmqManager.class);

  @Override
  public void setup() throws SetupException {
    try {
      Map<String, ExternalConfig> externalConfigMap =
          TestExecutionInjector.getInjector()
              .getInstance(ExternalConfigRepository.class)
              .getExternalConfigByType(RmqTestDataType.class);

      if (!externalConfigMap.isEmpty()) {
        for (Map.Entry<String, ExternalConfig> entry : externalConfigMap.entrySet()) {
          ExternalConfig externalConfig = entry.getValue();
          if (externalConfig != null) {
            RmqConfig rmqConfig = (RmqConfig) externalConfig;
            clientVsChannelMap.put(entry.getKey(), createChannel(rmqConfig));
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("error setting up rmq connections.", e);
      throw new SetupException("error setting up rmq connections.", e);
    }
  }

  @Override
  public void update(String clientId, ExternalConfig externalConfig) throws UpdateException {
    try {
      clientVsChannelMap.get(clientId).close();
      RmqConfig rmqConfig = (RmqConfig) externalConfig;
      clientVsChannelMap.put(clientId, createChannel(rmqConfig));
    } catch (Exception e) {
      LOGGER.error("error updating rmq connections.", e);
      throw new UpdateException("error updating rmq connections.", e);
    }
  }

  @Override
  public void shutdown() throws ShutdownException {
    try {
      for (Map.Entry<String, Channel> entry : clientVsChannelMap.entrySet()) {
        stopConsumer(entry.getValue());
      }
    } catch (Exception e) {
      LOGGER.error("error closing http connections.", e);
      throw new ShutdownException("error closing http connections.", e);
    }
  }

  /**
   * Creates {@link Channel} to publish Rmq messages.
   *
   * @param rmqConfig Provided in the {@link ExternalConfigModule} for a
   *     given client
   * @return a Channel for app to exchange publish
   * @throws SetupException if we are not able to setup connection.
   */
  private Channel createChannel(RmqConfig rmqConfig) throws SetupException {
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
      LOGGER.info(
          "Adding the RMQ endpoint ["
              + endpoint
              + ":"
              + rmqConfig.getPort()
              + "] in connection factory");
    }
    Address[] addrArr = addressList.toArray(new Address[0]);
    LOGGER.info("Number of nodes connected to in RMQ:" + addrArr.length);
    try {
      connection = factory.newConnection(addrArr);
      rmqChannel = connection.createChannel();
      LOGGER.info(
          "Connection to RMQ established.."
              + rmqChannel.getConnection().getAddress().getHostName());
      return rmqChannel;
    } catch (IOException | TimeoutException e) {
      String errorMsg = "Connection to RMQ could not be established..";
      LOGGER.error(errorMsg);
      throw new SetupException(errorMsg);
    }
  }

  /**
   * Closes the {@link Channel}.
   *
   * @param channel that needs to be disconnected
   */
  private void stopConsumer(Channel channel) {
    try {
      LOGGER.info("Stopping Consumer Service");
      if (channel.getConnection().isOpen()) {
        LOGGER.info("closing RabbitMQ channel...");
        channel.getConnection().close();
      }
      if (channel.isOpen()) {
        channel.close();
      }
    } catch (Exception e) {
      LOGGER.error("Exception while closing channel", e);
    }
  }

  @Override
  public Channel getChannelByClient(String client) {
    return clientVsChannelMap.get(client);
  }
}
