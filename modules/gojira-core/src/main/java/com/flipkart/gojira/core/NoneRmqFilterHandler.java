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
 * Implementation for {@link RmqFilterHandler} for mode {@link Mode#NONE}.
 */
public class NoneRmqFilterHandler extends RmqFilterHandler {

  /**
   * Gets the test-id and throws an exception if test-header is present.
   *
   * <p>{@inheritDoc}
   */
  @Override
  protected void handle(
      String exchangeName,
      byte[] key,
      byte[] value,
      AMQP.BasicProperties basicProperties,
      boolean mandatory) {
    String id = getTestId(basicProperties);
    if (id != null) {
      LOGGER.error(
          "Header with name: "
              + GojiraConstants.TEST_HEADER
              + " present. But service is not running in mode. : "
              + Mode.NONE);
      throw new RuntimeException(
          "Header with name: "
              + GojiraConstants.TEST_HEADER
              + " present. But service is not running in mode. : "
              + Mode.NONE);
    }
  }
}
