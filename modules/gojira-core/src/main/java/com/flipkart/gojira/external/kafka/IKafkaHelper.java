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

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * Interface to produce kafka messages.
 */
public interface IKafkaHelper {

  /**
   * Produces the record provided as input.
   *
   * @param client clientId
   * @param producerRecord record to be produced to kafka.
   * @return metadata of produced record.
   * @throws KafkaProducerException exception if we are not able to produce message.
   */
  RecordMetadata produce(String client, ProducerRecord<byte[], byte[]> producerRecord)
      throws KafkaProducerException;
}
