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
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqHelper implements IRmqHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(RmqHelper.class);

  @Override
  public void publish(
      String clientId,
      String exchangeName,
      byte[] routingKey,
      byte[] data,
      AMQP.BasicProperties properties,
      boolean mandatory)
      throws RmqPublishException {
    Channel channel =
        TestExecutionInjector.getInjector()
            .getInstance(IRmqManager.class)
            .getChannelByClient(clientId);
    if (channel == null) {
      LOGGER.trace("Unable to publish as cannot instantiate channel");
      throw new RmqPublishException("Unable to publish as cannot instantiate channel");
    }
    try {
      String routingParam = new String(routingKey);
      channel.basicPublish(exchangeName, routingParam, mandatory, properties, data);
    } catch (Exception e) {
      LOGGER.error("Unable to publish to RMq channel: " + e.getMessage());
      throw new RmqPublishException("Unable to publish to RMq channel", e);
    }
  }
}
