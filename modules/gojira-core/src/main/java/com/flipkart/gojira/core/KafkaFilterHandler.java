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

import static com.flipkart.gojira.core.GojiraConstants.TEST_HEADER;

import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.requestsampling.RequestSamplingRepository;
import com.google.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is expected to provide an interface for implementing different logic for different
 * {@link Mode} during request capture.
 */
public abstract class KafkaFilterHandler {

  protected static final Logger LOGGER = LoggerFactory.getLogger(KafkaFilterHandler.class);

  private RequestSamplingRepository requestSamplingRepository;

  protected KafkaFilterHandler(RequestSamplingRepository requestSamplingRepository) {
    this.requestSamplingRepository = requestSamplingRepository;
  }

  /**
   * Implementation of this is expected to call {@link DefaultProfileOrTestHandler#start(String,
   * TestRequestData, Mode)} as per {@link Mode} specific needs.
   *
   * @param topicName kafka topic name
   * @param key key used for producing message to the topic
   * @param value body used for producing message to the topic
   * @param headersMap headers used for producing message to the topic with key as string and value
   *     as map
   */
  protected abstract void handle(
      String topicName, byte[] key, byte[] value, Map<String, byte[]> headersMap);

  /**
   * Uses the sampling configuration to determine if the TOPIC is whitelisted or not for running in
   * various {@link Mode}.
   *
   * @param topic kafka topic
   * @return true if whitelisted, else false.
   */
  protected final boolean isWhitelistedTopic(String topic) {
    List<Pattern> whitelistedTopics = requestSamplingRepository.getWhitelist();
    for (Pattern whitelistedTopic : whitelistedTopics) {
      if (whitelistedTopic.matcher(topic).matches()) {
        return true;
      }
    }
    LOGGER.info(String.format("topic: %s is not whitelisted for Gojira... Hence ignoring!", topic));
    return false;
  }

  /**
   * Extracts the test-id for gojira.
   *
   * @param headersMap kafka headers map with key as string and value as map
   * @return test id
   */
  protected final String getTestId(Map<String, byte[]> headersMap) {
    byte[] id = headersMap.getOrDefault(TEST_HEADER, null);
    return id != null ? new String(id) : null;
  }
}
