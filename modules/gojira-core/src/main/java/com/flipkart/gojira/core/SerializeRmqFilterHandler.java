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

import com.rabbitmq.client.AMQP;

/**
 * Implementation of {@link RmqFilterHandler} in mode {@link Mode#SERIALIZE}.
 */
public class SerializeRmqFilterHandler extends RmqFilterHandler {

    public SerializeRmqFilterHandler() {super(requestSamplingRepository);}

    @Override
  protected void handle(
      String exchangeName,
      byte[] key,
      byte[] value,
      AMQP.BasicProperties basicProperties,
      boolean mandatory) {
    String id = getTestId(basicProperties);
    if (id == null) {
      throw new RuntimeException("X-GOJIRA-ID header not present");
    }
    DefaultProfileOrTestHandler.start(id, null);
  }
}
