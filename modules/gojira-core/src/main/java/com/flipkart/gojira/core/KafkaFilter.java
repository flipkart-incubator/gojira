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

import com.flipkart.gojira.models.TestResponseData;
import com.flipkart.gojira.models.kafka.KafkaTestResponseData;
import com.flipkart.gojira.requestsampling.RequestSamplingRepository;
import com.google.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter implementation to capture kafka request and response data. Also responsible for starting
 * and ending the recording of data per request-response capture lifecycle.
 */
public class KafkaFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaFilter.class);

  /**
   * Initializes a map of {@link Mode} specific filter handlers.
   */
  // TODO: converted static field to non-static
  private final Map<Mode, KafkaFilterHandler> filterHashMap;

  private final RequestSamplingRepository requestSamplingRepository;

  /**
   * Constructs KafkaFilter object by initializing filterHashMap with sampling configuration.
   *
   * @param requestSamplingRepository Sampling configuration
   */
  @Inject
  public KafkaFilter(RequestSamplingRepository requestSamplingRepository) {
    this.requestSamplingRepository = requestSamplingRepository;
    filterHashMap =
        Collections.unmodifiableMap(
            new HashMap<Mode, KafkaFilterHandler>() {
              {
                put(Mode.NONE, new NoneKafkaFilterHandler(requestSamplingRepository));
                put(Mode.PROFILE, new ProfileKafkaFilterHandler(requestSamplingRepository));
                put(Mode.TEST, new TestKafkaFilterHandler(requestSamplingRepository));
                put(Mode.SERIALIZE, new SerializeKafkaFilterHandler(requestSamplingRepository));
              }
            });
  }

  /**
   * Helper method to get headers.
   *
   * @param headers kafka headers
   * @return map of headers as with key as string and value as byte[]
   */
  private static Map<String, byte[]> getMapForRequestHeaders(Headers headers) {
    Map<String, byte[]> headersMap = new HashMap<>();
    if (headers == null) {
      return headersMap;
    }
    headers.forEach(
        header -> {
          headersMap.put(header.key(), header.value());
        });
    return headersMap;
  }

  /**
   * Integrating application is required to call this method during the start of request-response
   * capture life-cycle. Failure to do so may result in not capturing the request-response data.
   *
   * <p>This method invokes the {@link Mode} specific handler to process the incoming request.
   *
   * @param topicName kafka topic name
   * @param key key used for producing message to the topic
   * @param value body used for producing message to the topic
   * @param recordHeaders headers used for producing message to the topic
   */
  public void start(String topicName, byte[] key, byte[] value, Headers recordHeaders) {
    Map<String, byte[]> headersMap = getMapForRequestHeaders(recordHeaders);
    filterHashMap
        .getOrDefault(
            ProfileRepository.getMode(), new NoneKafkaFilterHandler(requestSamplingRepository))
        .handle(topicName, key, value, headersMap);
  }

  /**
   * Integrating application is required to call this method during the end of request-response
   * capture life-cycle. Failure to do so may result in not capturing the request-response data and
   * also potential memory leak.
   *
   * <p>This method calls {@link DefaultProfileOrTestHandler#end(TestResponseData)}.
   *
   * @param bytes response data if any. In most cases this may be null
   */
  public void end(byte[] bytes) {
    KafkaTestResponseData kafkaTestResponseData =
        KafkaTestResponseData.builder().setRespondData(bytes).build();
    DefaultProfileOrTestHandler.end(kafkaTestResponseData);
  }
}
