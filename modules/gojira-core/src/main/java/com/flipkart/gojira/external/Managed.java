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

package com.flipkart.gojira.external;

import com.flipkart.gojira.external.config.ExternalConfig;

/**
 * Interface whose methods are expected to be invoked as part of lifecycle during setup and shutdown
 * by client applications who need to make external connections.
 */
public interface Managed {

  /**
   * Method to setup external connections to enable rpc calls.
   *
   * @throws SetupException exception thrown if we are not able to setup connection.
   */
  void setup() throws SetupException;

  /**
   * Method to update external connections to enable rpc calls.
   *
   * @throws UpdateException exception thrown if we are not able to setup connection.
   */
  default void update(String clientId, ExternalConfig externalConfig) throws UpdateException {
    throw new UnsupportedOperationException("Operation not allowed");
  }

  /**
   * Method to shutdown external connections.
   *
   * @throws ShutdownException if we are not able to shutdown connection.
   */
  void shutdown() throws ShutdownException;
}
