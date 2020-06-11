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

import com.flipkart.gojira.core.injectors.TestExecutionInjector;
import com.flipkart.gojira.external.ExternalConfigModule;
import com.flipkart.gojira.external.ManagedModule;
import com.flipkart.gojira.external.config.ExternalConfig;
import com.flipkart.gojira.serde.SerdeModule;
import com.flipkart.gojira.serde.config.SerdeConfig;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This guice module contains all the configurations required for initiating tests by making calls
 * to the JVM which is running in {@link Mode#TEST} or {@link Mode#SERIALIZE} mode.
 */
public class TestExecutionModule extends AbstractModule {

  public static final Logger LOGGER = LoggerFactory.getLogger(TestExecutionModule.class);

  private final Map<String, List<ExternalConfig>> clientToListConfigMap;

  public TestExecutionModule(Map<String, List<ExternalConfig>> clientToListConfigMap) {
    this.clientToListConfigMap = clientToListConfigMap;
  }

  @Override
  protected void configure() {
    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        install(new ExternalConfigModule(clientToListConfigMap));
        install(new ManagedModule());
        install(new SerdeModule(SerdeConfig.builder().build()));
      }
    });

    TestExecutionInjector.assignInjector(injector);
  }
}
