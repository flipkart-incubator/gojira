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

package com.flipkart.gojira.external.config;

import static com.flipkart.gojira.core.GlobalConstants.RMQ_TEST_DATA_TYPE;

import java.util.List;

public class RmqConfig extends ExternalConfig {
  /** port at which RMQ is hosted. */
  private int port;

  /** username of RMQ cluster. */
  private String username;

  /** password of RMQ cluster. */
  private String password;
  /**
   * The virtual host path in which the exchange exists. Default value is "/". Exchange with same
   * name can exist on the same host on different virtual host path.
   */
  private String virtualHost;

  private boolean automaticRecoveryEnabled;

  /** List of end-points for RMQ test machines. */
  private List<String> endpoints;

  public RmqConfig() {
    super(RMQ_TEST_DATA_TYPE);
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getVirtualHost() {
    return virtualHost;
  }

  public void setVirtualHost(String virtualHost) {
    this.virtualHost = virtualHost;
  }

  public List<String> getEndpoints() {
    return endpoints;
  }

  public void setEndpoints(List<String> endpoints) {
    this.endpoints = endpoints;
  }

  public boolean isAutomaticRecoveryEnabled() {
    return automaticRecoveryEnabled;
  }

  public void setAutomaticRecoveryEnabled(boolean automaticRecoveryEnabled) {
    this.automaticRecoveryEnabled = automaticRecoveryEnabled;
  }
}
