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

import static com.flipkart.gojira.core.GlobalConstants.HTTP_TEST_DATA_TYPE;

import java.util.Objects;

public class HttpConfig extends ExternalConfig {

  private String hostNamePort;
  private int maxConnections;
  private int connectionTimeout;
  private int operationTimeout;

  public HttpConfig() {
    super(HTTP_TEST_DATA_TYPE);
  }

  public String getHostNamePort() {
    return hostNamePort;
  }

  public void setHostNamePort(String hostNamePort) {
    this.hostNamePort = hostNamePort;
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public int getOperationTimeout() {
    return operationTimeout;
  }

  public void setOperationTimeout(int operationTimeout) {
    this.operationTimeout = operationTimeout;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HttpConfig that = (HttpConfig) o;
    return maxConnections == that.maxConnections
        && connectionTimeout == that.connectionTimeout
        && operationTimeout == that.operationTimeout
        && Objects.equals(hostNamePort, that.hostNamePort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hostNamePort, maxConnections, connectionTimeout, operationTimeout);
  }
}
