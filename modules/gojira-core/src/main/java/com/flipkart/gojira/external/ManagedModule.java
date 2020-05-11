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

import com.flipkart.gojira.external.http.HttpHelper;
import com.flipkart.gojira.external.http.HttpManager;
import com.flipkart.gojira.external.http.IHttpHelper;
import com.flipkart.gojira.external.http.IHttpManager;
import com.flipkart.gojira.external.kafka.IKafkaHelper;
import com.flipkart.gojira.external.kafka.IKafkaManager;
import com.flipkart.gojira.external.kafka.KafkaHelper;
import com.flipkart.gojira.external.kafka.KafkaManager;
import com.flipkart.gojira.external.rmq.IRmqHelper;
import com.flipkart.gojira.external.rmq.IRmqManager;
import com.flipkart.gojira.external.rmq.RmqHelper;
import com.flipkart.gojira.external.rmq.RmqManager;
import com.google.inject.AbstractModule;

/**
 * Guice module which is responsible for binding interfaces to setup and shutdown external
 * connections as well as binding interfaces to get instances of resources to make rpc calls.
 */
public class ManagedModule extends AbstractModule {

  @Override
  protected void configure() {
    ManagedImpl managed = new ManagedImpl();
    bind(Managed.class).toInstance(managed);
    bind(IHttpManager.class).toInstance(HttpManager.HTTP_MANAGER);
    bind(IHttpHelper.class).toInstance(new HttpHelper());
    bind(IKafkaManager.class).toInstance(KafkaManager.KAFKA_MANAGER);
    bind(IKafkaHelper.class).toInstance(new KafkaHelper());
    bind(IRmqManager.class).toInstance(RmqManager.RMQ_MANAGER);
    bind(IRmqHelper.class).toInstance(new RmqHelper());
  }
}
