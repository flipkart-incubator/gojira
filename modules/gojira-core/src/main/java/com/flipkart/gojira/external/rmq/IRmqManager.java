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

package com.flipkart.gojira.external.rmq;

import com.rabbitmq.client.Channel;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface which helps retrieve {@link Channel}.
 */
public interface IRmqManager {

  Map<String, Channel> clientVsChannelMap = new HashMap<>();

  /**
   * Given a clientId as key, return {@link Channel}.
   *
   * @param client the id of the client under test
   * @return channel on which publish will happen to exchange.
   */
  Channel getChannelByClient(String client);
}
