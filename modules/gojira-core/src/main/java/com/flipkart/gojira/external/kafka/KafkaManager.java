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

package com.flipkart.gojira.external.kafka;

import com.flipkart.gojira.core.injectors.TestExecutionInjector;
import com.flipkart.gojira.external.ExternalConfigRepository;
import com.flipkart.gojira.external.Managed;
import com.flipkart.gojira.external.SetupException;
import com.flipkart.gojira.external.ShutdownException;
import com.flipkart.gojira.external.config.ExternalConfig;
import com.flipkart.gojira.external.config.KafkaConfig;
import com.flipkart.gojira.models.kafka.KafkaTestDataType;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for {@link IKafkaManager} and {@link Managed}.
 */
public enum KafkaManager implements IKafkaManager, Managed {
  KAFKA_MANAGER;
  public static final Logger LOGGER = LoggerFactory.getLogger(KafkaManager.class);

  /**
   * Sets up kafka connections by using properties in {@link ExternalConfig}.
   *
   * @throws SetupException if we are not able to setup kafka connection.
   */
  @Override
  public void setup() throws SetupException {
    try {
      Map<String, ExternalConfig> externalConfigMap =
          TestExecutionInjector.getInjector()
              .getInstance(ExternalConfigRepository.class)
              .getExternalConfigByType(KafkaTestDataType.class);
      for (Map.Entry<String, ExternalConfig> entry : externalConfigMap.entrySet()) {
        if (entry.getValue() != null) {
          KafkaConfig kafkaConfig = (KafkaConfig) entry.getValue();
          Properties props = new Properties();
          props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getHostNamePort());
          props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
          props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
          clientMap.put(entry.getKey(), new KafkaProducer<>(props));
        }
      }
    } catch (Exception e) {
      LOGGER.error("error setting up kafka producers.", e);
      throw new SetupException("error setting up kafka producers.", e);
    }
  }

  /**
   * Closes kafka connections by calling {@link KafkaProducer#close()}.
   *
   * @throws ShutdownException if we are not able to shutdown kafka connection.
   */
  @Override
  public void shutdown() throws ShutdownException {
    try {
      for (Map.Entry<String, Producer<byte[], byte[]>> entry : clientMap.entrySet()) {
        entry.getValue().close();
      }
    } catch (Exception e) {
      LOGGER.error("error closing kafka producers.", e);
      throw new ShutdownException("error closing kafka producers.", e);
    }
  }

  @Override
  public Producer<byte[], byte[]> getProducer(String client) {
    return clientMap.get(client);
  }
}
