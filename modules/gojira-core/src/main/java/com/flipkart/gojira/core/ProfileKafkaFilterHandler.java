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

package com.flipkart.gojira.core;

import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.models.kafka.KafkaTestRequestData;
import java.util.Map;

/**
 * Implementation of {@link KafkaFilterHandler} for mode {@link Mode#PROFILE}.
 */
public class ProfileKafkaFilterHandler extends KafkaFilterHandler {

  /**
   * Gets test-id from headers for validation. If not null throws {@link RuntimeException}
   *
   * <p>Checks if whitelisted. If not return else capture the parameters required for making a kafka
   * request in other {@link Mode}.
   *
   * <p>Then begins recording session by calling {@link DefaultProfileOrTestHandler#start(String,
   * TestRequestData, Mode)}
   *
   * <p>Implementation of this is expected to call {@link DefaultProfileOrTestHandler#start(String,
   * TestRequestData, Mode)}
   *
   * @param topicName kafka topic name
   * @param key key used for producing message to the topic
   * @param value body used for producing message to the topic
   * @param headersMap headers used for producing message to the topic with key as string and value
   *     as map
   */
  @Override
  protected void handle(
      String topicName,
      byte[] key,
      byte[] value,
      Map<String, byte[]> headersMap) {
    String id = getTestId(headersMap);
    if (id != null) {
      LOGGER.error(
          "Header with name: "
              + GojiraConstants.TEST_HEADER
              + " present. But service is not running in TEST mode. : "
              + Mode.PROFILE);
      throw new RuntimeException(
          "Header with name: "
              + GojiraConstants.TEST_HEADER
              + " present. But service is not running in TEST mode. : "
              + Mode.PROFILE);
    }
    if (!isWhitelistedTopic(topicName)) {
      return;
    }
    id = String.valueOf(System.nanoTime()) + Thread.currentThread().getId();
    KafkaTestRequestData kafkaTestRequestData =
        KafkaTestRequestData.builder()
            .setTopicName(topicName)
            .setKey(key)
            .setValue(value)
            .setHeaders(headersMap)
            .build();
    try {
      DefaultProfileOrTestHandler.start(id, kafkaTestRequestData, Mode.PROFILE);
    } catch (Exception e) {
      LOGGER.error("Exception trying to construct KafkaTestRequest. ", e);
    }
  }
}
