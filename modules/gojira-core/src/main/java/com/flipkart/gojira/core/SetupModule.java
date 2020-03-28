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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gojira.compare.GojiraComparisonModule;
import com.flipkart.gojira.compare.config.GojiraComparisonConfig;
import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.queuedsender.TestQueuedSenderModule;
import com.flipkart.gojira.queuedsender.config.TestQueuedSenderConfig;
import com.flipkart.gojira.requestsampling.RequestSamplingModule;
import com.flipkart.gojira.requestsampling.config.RequestSamplingConfig;
import com.flipkart.gojira.serde.SerdeModule;
import com.flipkart.gojira.serde.config.SerdeConfig;
import com.flipkart.gojira.sinkstore.config.DataStoreConfig;
import com.flipkart.gojira.sinkstore.config.DataStoreModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This guice module is used for setting up all configuration related to execution in various {@link
 * Mode}.
 */
public class SetupModule extends AbstractModule {

  public static final Logger LOGGER = LoggerFactory.getLogger(SetupModule.class);

  private Mode mode;

  private RequestSamplingConfig requestSamplingConfig;

  private SerdeConfig serdeConfig;

  private GojiraComparisonConfig gojiraComparisonConfig;

  private DataStoreConfig dataStoreConfig;

  private TestQueuedSenderConfig testQueuedSenderConfig;

  /**
   * @param mode                   Specifies the mode in which server needs to run. Can be {@link
   *                               Mode#PROFILE} (when requests or annotated methods need to be
   *                               recorded) {@link Mode#TEST} (when responses and annotated methods
   *                               need to be compared against recorded data) {@link Mode#NONE}
   *                               (when the server needs to run without recording or replaying
   *                               mode) {@link Mode#SERIALIZE} (when the server needs to just test
   *                               deserialization of recorded data)
   * @param requestSamplingConfig  The requestSamplingConfig contains the sampling percentage and
   *                               the whiteList to filter request profiling.
   * @param serdeConfig            Contains serde Handler and config for the serialization-deserialization
   *                               of the data.
   * @param gojiraComparisonConfig Contains the default and response compare handlers to be used in
   *                               {@link Mode#TEST} mode and diffIgnoreMap for the same.
   * @param dataStoreConfig        Implementation for writing/reading test data.
   * @param testQueuedSenderConfig In {@link Mode#PROFILE} mode, requests that get profiled are
   *                               written to a BigQueue implementation. This specifies the
   *                               configuration for the same.
   */
  public SetupModule(Mode mode,
      RequestSamplingConfig requestSamplingConfig,
      SerdeConfig serdeConfig,
      GojiraComparisonConfig gojiraComparisonConfig,
      DataStoreConfig dataStoreConfig,
      TestQueuedSenderConfig testQueuedSenderConfig) {
    this.mode = mode;
    this.requestSamplingConfig = requestSamplingConfig;
    this.serdeConfig = serdeConfig;
    this.gojiraComparisonConfig = gojiraComparisonConfig;
    this.dataStoreConfig = dataStoreConfig;
    this.testQueuedSenderConfig = testQueuedSenderConfig;
  }

  protected void configure() {
    // TODO: In TEST mode, enable host:port level validation here so that we are sure we are not connecting
    //       to any box other than what we need when running gojira. This means the data required for running
    //       gojira should be stored in a place other than production data
    ProfileRepository.setMode(mode);
    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(ObjectMapper.class).asEagerSingleton();
        install(new RequestSamplingModule(requestSamplingConfig));
        install(new SerdeModule(serdeConfig));
        install(new GojiraComparisonModule(gojiraComparisonConfig));
        install(new DataStoreModule(dataStoreConfig));
        install(new TestQueuedSenderModule(testQueuedSenderConfig));
      }
    });

    GuiceInjector.assignInjector(injector);
  }
}
