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

import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.requestsampling.RequestSamplingRepository;
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
  //TODO: Make this common.
  protected final String TEST_HEADER = "X-GOJIRA-ID";

  /**
   * @param topicName  kafka topic name
   * @param key        key used for producing message to the topic
   * @param value      body used for producing message to the topic
   * @param headersMap headers used for producing message to the topic with key as string and value
   *                   as map
   *                   <p>
   *                   Implementation of this is expected to call {@link DefaultProfileOrTestHandler#start(String,
   *                   TestRequestData)} as per {@link Mode} specific needs.
   */
  protected abstract void handle(String topicName, byte[] key, byte[] value,
      Map<String, byte[]> headersMap);

  /**
   * Helper method which given
   *
   * @param topic kafka topic
   * @return boolean true if whitelisted, else false.
   */
  protected final boolean isWhitelistedTopic(String topic) {
    List<Pattern> whitelistedTopics = GuiceInjector.getInjector()
        .getInstance(RequestSamplingRepository.class).getWhitelist();
    for (Pattern whitelistedTopic : whitelistedTopics) {
      if (whitelistedTopic.matcher(topic).matches()) {
        return true;
      }
    }
    LOGGER.info(String.format("topic: %s is not whitelisted for Gojira... Hence ignoring!", topic));
    return false;
  }

  /**
   * Helper method which given
   *
   * @param headersMap kafka headers map with key as string and value as map
   * @return test id
   */
  protected final String getTestId(Map<String, byte[]> headersMap) {
    byte[] id = headersMap.getOrDefault(TEST_HEADER, null);
    return id != null ? new String(id) : null;
  }


}
