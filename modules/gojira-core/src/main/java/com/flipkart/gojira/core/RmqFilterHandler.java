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

import static com.flipkart.gojira.core.GlobalConstants.TEST_HEADER;

import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.requestsampling.RequestSamplingRepository;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.LongString;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is expected to provide an interface for implementing different logic for different
 * {@link Mode} during request capture.
 */
public abstract class RmqFilterHandler {

  protected static final Logger LOGGER = LoggerFactory.getLogger(RmqFilterHandler.class);

  /**
   * Implementation of this is expected to call {@link DefaultProfileOrTestHandler#start(String,
   * TestRequestData, Mode)} as per {@link Mode} specific needs.
   *
   * @param exchangeName rmq exchange name
   * @param key key used for producing message to the exchange
   * @param value body used for producing message to the exchange
   * @param basicProperties contains headers, transactional reply-to id and meta data for successful
   *     RMQ operation
   * @param mandatory mandatory flag tells RabbitMq that the message must be routable.
   */
  protected abstract void handle(
      String exchangeName,
      byte[] key,
      byte[] value,
      AMQP.BasicProperties basicProperties,
      boolean mandatory);

  /**
   * Helper method which tells whether the exchange being published to is whitelisted.
   *
   * @param exchangeName rmq exchange for publishing
   * @return true if whitelisted, else false.
   */
  protected boolean isExchangeWhitelisted(String exchangeName) {
    List<Pattern> whitelistedExchanges =
        GuiceInjector.getInjector().getInstance(RequestSamplingRepository.class).getWhitelist();
    for (Pattern whitelistedExchange : whitelistedExchanges) {
      if (whitelistedExchange.matcher(exchangeName).matches()) {
        return true;
      }
    }
    LOGGER.trace(
        String.format(
            "exchange: %s is not whitelisted for Gojira... Hence ignoring!", exchangeName));
    return false;
  }

  /**
   * Extracts the test-id for gojira.
   *
   * @param basicProperties rmq basic properties which contains headers
   * @return test id
   */
  protected final String getTestId(AMQP.BasicProperties basicProperties) {

    if (basicProperties == null
        || basicProperties.getHeaders() == null
        || basicProperties.getHeaders().isEmpty()) {
      LOGGER.error("Headers not present for RMQ");
      return null;
    }
    Map<String, Object> headersMap = basicProperties.getHeaders();
    try {
      for (Map.Entry<String, Object> header : headersMap.entrySet()) {
        if (TEST_HEADER.equals(header.getKey())) {
          if (header.getValue() instanceof LongString) {
            byte[] correlationIdAsByteArray = ((LongString) header.getValue()).getBytes();
            return new String(correlationIdAsByteArray, "UTF-8");
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unable to encode test headers", e);
    }
    return null;
  }
}
