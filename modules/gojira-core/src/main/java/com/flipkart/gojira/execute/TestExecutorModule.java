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

package com.flipkart.gojira.execute;

import static com.flipkart.gojira.core.GlobalConstants.HTTP_TEST_DATA_TYPE;
import static com.flipkart.gojira.core.GlobalConstants.KAFKA_TEST_DATA_TYPE;
import static com.flipkart.gojira.core.GlobalConstants.RMQ_TEST_DATA_TYPE;

import com.flipkart.gojira.execute.http.DefaultHttpTestExecutor;
import com.flipkart.gojira.execute.kafka.DefaultKafkaTestExecutor;
import com.flipkart.gojira.execute.rmq.DefaultRmqTestExecutor;
import com.flipkart.gojira.models.TestData;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Guice module which contains bindings for different {@link TestExecutor} for different {@link
 * TestData}.
 */
public class TestExecutorModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(TestExecutor.class)
        .annotatedWith(Names.named(HTTP_TEST_DATA_TYPE))
        .to(DefaultHttpTestExecutor.class);
    bind(TestExecutor.class)
        .annotatedWith(Names.named(KAFKA_TEST_DATA_TYPE))
        .to(DefaultKafkaTestExecutor.class);
    bind(TestExecutor.class)
        .annotatedWith(Names.named(RMQ_TEST_DATA_TYPE))
        .to(DefaultRmqTestExecutor.class);
  }
}
