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

package com.flipkart.gojira.external;

import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.external.config.ExternalConfig;
import com.flipkart.gojira.external.http.HttpManager;
import com.flipkart.gojira.external.kafka.KafkaManager;
import com.flipkart.gojira.external.rmq.RmqManager;
import com.flipkart.gojira.queuedsender.TestQueuedSender;

/**
 * Implementation of {@link Managed}.
 */
public class ManagedImpl implements Managed {

  /**
   * Sets up connections for HTTP and Kafka.
   *
   * @throws SetupException if we are not able to setup connection.
   */
  @Override
  public void setup() throws SetupException {
    HttpManager.HTTP_MANAGER.setup();
    KafkaManager.KAFKA_MANAGER.setup();
    RmqManager.RMQ_MANAGER.setup();
  }

  @Override
  public void update(String clientId, ExternalConfig externalConfig) throws UpdateException {
    switch (externalConfig.getType()) {
      case "HTTP" :
        HttpManager.HTTP_MANAGER.update(clientId, externalConfig);
        break;
      case "KAFKA" :
        KafkaManager.KAFKA_MANAGER.update(clientId, externalConfig);
        break;
      case "RMQ" :
        RmqManager.RMQ_MANAGER.update(clientId, externalConfig);
        break;
      default:
        break;
    }
  }

  /**
   * Shuts down connections for HTTP and Kafka.
   *
   * @throws ShutdownException if we are not able to shutdown connection.
   */
  @Override
  public void shutdown() throws ShutdownException {
    HttpManager.HTTP_MANAGER.shutdown();
    KafkaManager.KAFKA_MANAGER.shutdown();
    RmqManager.RMQ_MANAGER.shutdown();

    try {
      GuiceInjector.getInjector().getInstance(TestQueuedSender.class).shutdown();
    } catch (Exception e) {
      throw new ShutdownException("error during test-queued-sender shutdown", e);
    }
  }
}
