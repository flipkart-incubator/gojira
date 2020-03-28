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

package com.flipkart.gojira.queuedsender.config;

import com.flipkart.gojira.core.Mode;
import com.flipkart.gojira.models.TestData;

/**
 * This class holds information regarding various config parameters for queueing. This needs to be
 * provided by the client application.
 */
public class TestQueuedSenderConfig {

  /**
   * In {@link Mode#PROFILE} mode, requests that get profiled are written to a BigQueue
   * implementation. This specifies the queue length.
   */
  private Long queueSize = 1000L;

  /**
   * In {@link Mode#PROFILE} mode, this is the path that needs to be provided where {@link TestData}
   * is temporarily written to disk from BigQueue.
   */
  private String path = "/var/log/gojira/big_queue_data";

  /**
   * In {@link Mode#PROFILE} mode, BigQueue data will be off loaded to DataStore after
   * queuePurgeInterval in seconds.
   */
  private int queuePurgeInterval = 30;

  private TestQueuedSenderConfig() {

  }

  public static Builder builder() {
    return new Builder();
  }

  public Long getQueueSize() {
    return queueSize;
  }

  public String getPath() {
    return path;
  }

  public int getQueuePurgeInterval() {
    return queuePurgeInterval;
  }

  public static class Builder {

    private TestQueuedSenderConfig testQueuedSenderConfigToBuild;

    private Builder() {
      this.testQueuedSenderConfigToBuild = new TestQueuedSenderConfig();
    }

    public TestQueuedSenderConfig build() {
      return this.testQueuedSenderConfigToBuild;
    }

    public Builder setQueueSize(Long queueSize) {
      this.testQueuedSenderConfigToBuild.queueSize = queueSize;
      return this;
    }

    public Builder setPath(String path) {
      this.testQueuedSenderConfigToBuild.path = path;
      return this;
    }

    public Builder setQueuePurgeIntervalInSeconds(int queuePurgeInterval) {
      this.testQueuedSenderConfigToBuild.queuePurgeInterval = queuePurgeInterval;
      return this;
    }
  }
}
