package com.flipkart.gojira.external.rmq;

import com.rabbitmq.client.Channel;

import java.util.HashMap;
import java.util.Map;

public interface IRMQManager {

  Map<String, Channel> clientVsChannelMap = new HashMap<>();

  /**
   * @param client, the id of the client under test
   * @return, channel on which publish will happen to exchange.
   */
  Channel getChannelByClient(String client);
}
