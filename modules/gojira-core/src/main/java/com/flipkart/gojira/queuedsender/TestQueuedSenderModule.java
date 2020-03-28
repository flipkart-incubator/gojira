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

import com.flipkart.gojira.queuedsender.config.TestQueuedSenderConfig;
import com.google.inject.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Guice module to setup {@link TestQueuedSender} related config.
 */
public class TestQueuedSenderModule extends AbstractModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestQueuedSenderModule.class);
  private final TestQueuedSenderConfig testQueuedSenderConfig;

  public TestQueuedSenderModule(TestQueuedSenderConfig testQueuedSenderConfig) {
    this.testQueuedSenderConfig = testQueuedSenderConfig;
  }

  @Override
  protected void configure() {
    TestQueuedSender testQueuedSender = new TestQueuedSenderImpl();
    testQueuedSender.setTestQueuedSenderConfig(testQueuedSenderConfig);
    bind(TestQueuedSender.class).toInstance(testQueuedSender);
    try {
      testQueuedSender.setup();
    } catch (Exception e) {
      LOGGER.error("error initializing queued sender", e);
    }
  }
}
