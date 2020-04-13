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

package com.flipkart.gojira.execute.kafka;

import com.flipkart.gojira.execute.TestExecutor;
import com.flipkart.gojira.external.kafka.IKafkaHelper;
import com.flipkart.gojira.external.kafka.KafkaProducerException;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.kafka.KafkaTestDataType;
import com.flipkart.gojira.models.kafka.KafkaTestRequestData;
import com.flipkart.gojira.models.kafka.KafkaTestResponseData;
import com.google.inject.Inject;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultKafkaTestExecutor
    implements TestExecutor<
        TestData<KafkaTestRequestData, KafkaTestResponseData, KafkaTestDataType>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultKafkaTestExecutor.class);
  private static final String GOJIRA_TEST_HEADER = "X-GOJIRA-ID";
  public final IKafkaHelper kafkaHelper;

  @Inject
  public DefaultKafkaTestExecutor(final IKafkaHelper kafkaHelper) {
    this.kafkaHelper = kafkaHelper;
  }

  /**
   * When executing adds testId to kafka headers.
   *
   * @param testData testData which is used for invoking execution
   * @param clientId identifier to indicate which system hit
   * @throws KafkaProducerException if RecordProduction fails.
   */
  @Override
  public void execute(
      TestData<KafkaTestRequestData, KafkaTestResponseData, KafkaTestDataType> testData,
      String clientId)
      throws KafkaProducerException {
    String testId = testData.getId();
    KafkaTestRequestData requestData = testData.getRequestData();
    RecordHeaders recordHeaders = new RecordHeaders();
    requestData.getHeaders().forEach((k, v) -> recordHeaders.add(new RecordHeader(k, v)));
    recordHeaders.add(new RecordHeader(GOJIRA_TEST_HEADER, testId.getBytes()));
    ProducerRecord<byte[], byte[]> producerRecord =
        new ProducerRecord<>(
            requestData.getTopicName(),
            null,
            requestData.getKey(),
            requestData.getValue(),
            recordHeaders);
    LOGGER.info("gojira test ID " + testId);
    RecordMetadata recordMetadata = kafkaHelper.produce(clientId, producerRecord);
    logRecordProduction(recordMetadata, clientId, testId);
  }

  private void logRecordProduction(RecordMetadata recordMetadata, String clientId, String testId) {
    LOGGER.info(
        String.format(
            "produced record to topic: %s with clientId: %s for testId: %s. ",
            recordMetadata.topic(), clientId, testId));
  }
}
