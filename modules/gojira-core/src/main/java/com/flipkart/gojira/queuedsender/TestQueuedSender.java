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

package com.flipkart.gojira.queuedsender;

import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.TestDataType;
import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.models.TestResponseData;
import com.flipkart.gojira.queuedsender.config.TestQueuedSenderConfig;
import com.flipkart.gojira.sinkstore.handlers.SinkHandler;

/**
 * TestQueuedSender is the interface which needs to be implemented for setting up, shutting down and
 * sending messages to a temporary {@link com.leansoft.bigqueue.IBigQueue} based store till it is
 * flushed to data store.
 */
public abstract class TestQueuedSender {

  protected TestQueuedSenderConfig testQueuedSenderConfig;

  /**
   * This method sets up the in-memory queue to store and write asynchronously to {@link
   * SinkHandler}.
   *
   * @throws Exception if error while setting up the queue.
   */
  public abstract void setup() throws Exception;

  /**
   * This method shuts down the in-memory queue set up in {@link #setup()}.
   *
   * @throws Exception if error while shutting down the queue.
   */
  public abstract void shutdown() throws Exception;

  /**
   * This method writes to the in-memory queue.
   *
   * @param testData is the data to be enqueued.
   * @throws Exception if error while writing the data to the in-memory queue.
   */
  public abstract <T extends TestDataType> void send(
      TestData<TestRequestData<T>, TestResponseData<T>, T> testData) throws Exception;

  /**
   * This method sets configuration for the in-memory queue.
   *
   * @param testQueuedSenderConfig is the configuration.
   */
  public void setTestQueuedSenderConfig(TestQueuedSenderConfig testQueuedSenderConfig) {
    this.testQueuedSenderConfig = testQueuedSenderConfig;
  }
}
