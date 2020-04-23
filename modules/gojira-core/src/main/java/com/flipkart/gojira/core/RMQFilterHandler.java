package com.flipkart.gojira.core;

import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.requestsampling.RequestSamplingRepository;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.LongString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.flipkart.gojira.core.GojiraConstants.TEST_HEADER;

/**
 * This class is expected to provide an interface for implementing different logic for different
 * {@link Mode} during request capture.
 */
public abstract class RMQFilterHandler {

  protected static final Logger LOGGER = LoggerFactory.getLogger(RMQFilterHandler.class);

  /**
   * @param exchangeName rmq exchange name
   * @param key key used for producing message to the exchange
   * @param value body used for producing message to the exchange
   * @param basicProperties contains headers, transactional reply-to id and meta data for successful RMQ operation
   * @param mandatory  mandatory flag tells RabbitMq that the message must be routable.
   *     <p>Implementation of this is expected to call {@link
   *     DefaultProfileOrTestHandler#start(String, TestRequestData)} (String, TestRequestData)} as
   *     per {@link Mode} specific needs.
   */
  protected abstract void handle(
      String exchangeName,
      byte[] key,
      byte[] value,
      AMQP.BasicProperties basicProperties,
      boolean mandatory);

  /**
   * Helper method which tells whether the exchange being published to is whitelisted?
   *
   * @param exchangeName rmq exchange for publishing
   * @return boolean true if whitelisted, else false.
   */
  protected boolean isExchangeWhitelisted(String exchangeName) {
    List<Pattern> whitelistedExchanges =
        GuiceInjector.getInjector().getInstance(RequestSamplingRepository.class).getWhitelist();
    for (Pattern whitelistedExchange : whitelistedExchanges) {
      if (whitelistedExchange.matcher(exchangeName).matches()) {
        return true;
      }
    }
    LOGGER.info(
        String.format(
            "exchange: %s is not whitelisted for Gojira... Hence ignoring!", exchangeName));
    return false;
  }

  /**
   * Helper method which given
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
