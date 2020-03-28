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
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link IKafkaHelper}
 */
public class KafkaHelper implements IKafkaHelper {

  public static final Logger LOGGER = LoggerFactory.getLogger(KafkaHelper.class);

  /**
   * Produces kafka message by calling {@link org.apache.kafka.clients.producer.KafkaProducer#send(ProducerRecord)}
   * and then returns {@link RecordMetadata} by calling {@link Future#get()}
   * <p>
   * On error, throws {@link KafkaProducerException}
   *
   * @param client         clientId
   * @param producerRecord record to be produced to kafka.
   * @return metadata of produced record.
   * @throws KafkaProducerException exception if we are not able to produce message.
   */
  @Override
  public RecordMetadata produce(String client, ProducerRecord<byte[], byte[]> producerRecord)
      throws KafkaProducerException {
    Producer<byte[], byte[]> producer = TestExecutionInjector.getInjector()
        .getInstance(IKafkaManager.class).getProducer(client);
    try {
      Future<RecordMetadata> recordMetadataFuture = producer.send(producerRecord);
      LOGGER.info("Producing record. ");
      return recordMetadataFuture.get();
    } catch (Exception e) {
      LOGGER.info("Record not produced.");
      throw new KafkaProducerException("Record not produced.", e);
    }
  }
}
