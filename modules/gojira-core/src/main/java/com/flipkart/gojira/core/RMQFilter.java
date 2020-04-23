package com.flipkart.gojira.core;

import com.flipkart.gojira.models.rmq.RMQTestResponseData;
import com.rabbitmq.client.AMQP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter implementation to capture RMQ request and response data. Also responsible for starting and
 * ending the recording of data per request-response capture lifecycle.
 */
public class RMQFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(RMQFilter.class);

  public RMQFilter() {}

  /** Initializes a map of {@link Mode} specific filter handlers for RMQ. */
  private static final Map<Mode, RMQFilterHandler> filterHashMap =
      Collections.unmodifiableMap(
          new HashMap<Mode, RMQFilterHandler>() {
            {
              put(Mode.NONE, new NoneRMQFilterHandler());
              put(Mode.PROFILE, new ProfileRMQFilterHandler());
              put(Mode.TEST, new TestRMQFilterHandler());
              put(Mode.SERIALIZE, new SerializeRMQFilterHandler());
            }
          });

  public void start(
      String exchangeName,
      byte[] routingKey,
      byte[] data,
      AMQP.BasicProperties basicProperties,
      boolean mandatory) {

    filterHashMap
        .getOrDefault(ProfileRepository.getMode(), new NoneRMQFilterHandler())
        .handle(exchangeName, routingKey, data, basicProperties, mandatory);
  }

  public void end(byte[] bytes) {
    RMQTestResponseData rmqTestResponseData =
        RMQTestResponseData.builder().setRespondData(bytes).build();
    DefaultProfileOrTestHandler.end(rmqTestResponseData);
  }
}
