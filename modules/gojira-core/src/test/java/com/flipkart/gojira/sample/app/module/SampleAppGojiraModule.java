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

package com.flipkart.gojira.sample.app.module;

import com.flipkart.compare.diff.DiffType;
import com.flipkart.compare.handlers.json.JsonTestCompareHandler;
import com.flipkart.gojira.compare.config.GojiraComparisonConfig;
import com.flipkart.gojira.core.BindingsModule;
import com.flipkart.gojira.core.Mode;
import com.flipkart.gojira.core.SetupModule;
import com.flipkart.gojira.queuedsender.config.TestQueuedSenderConfig;
import com.flipkart.gojira.requestsampling.config.RequestSamplingConfig;
import com.flipkart.gojira.serde.config.SerdeConfig;
import com.flipkart.gojira.serde.handlers.json.JsonDefaultTestSerdeHandler;
import com.flipkart.gojira.sinkstore.config.DataStoreConfig;
import com.flipkart.gojira.sinkstore.file.FileBasedDataStoreHandler;
import com.google.inject.AbstractModule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Class that has all configurations for Gojira testing to happen that needs to be installed.
 */
public class SampleAppGojiraModule extends AbstractModule {
  // BigQueue config.
  private static final String BIQ_QUEUE_PATH = "/tmp/gojira-messages/";
  private static final int BIQ_QUEUE_PURGE_INTERVAL = 1;
  private static final long BIQ_QUEUE_SIZE = 100L;

  // Sampling Config.
  private static final String GET_GITHUB_URI = "GET /github/(.*)";
  private static final String POST_HTTPBIN_DATA = "POST /httpbin/post";
  private static final int    SAMPLING_CONFIG = 100;

  // patterns to be ignored
  private static final String HEADERS_DATE = "/, headers, Date, /";

  // data-store config
  private static final String DATASTORE_FILE = "/tmp/datastore";


  public SampleAppGojiraModule() {}

  /**
   * All required configs to be installed including the following:
   * 1. {@link Mode} - specified as {@link Mode#DYNAMIC}, to be able to switch b/w modes, without
   * having to restart JVM.
   * 2. {@link com.flipkart.gojira.requestsampling.RequestSamplingModule}
   * 3. {@link com.flipkart.gojira.serde.SerdeModule}
   * 4. {@link com.flipkart.gojira.compare.GojiraComparisonModule}
   * 5. {@link com.flipkart.gojira.sinkstore.config.DataStoreModule}
   * 6. {@link com.flipkart.gojira.queuedsender.TestQueuedSenderModule}
   * 7. {@link BindingsModule}
   */
  @Override
  protected void configure() {
    // build request sampling config.
    RequestSamplingConfig requestSamplingConfig =
        RequestSamplingConfig.builder()
            .setSamplingPercentage(SAMPLING_CONFIG)
            .setWhitelist(Arrays.asList(GET_GITHUB_URI, POST_HTTPBIN_DATA))
            .build();

    // build serialization config.
    SerdeConfig serdeConfig =
        SerdeConfig.builder().setDefaultSerdeHandler(new JsonDefaultTestSerdeHandler()).build();

    // build comparison config.
    GojiraComparisonConfig gojiraComparisonConfig =
        GojiraComparisonConfig.builder()
            .setDiffIgnoreMap(
                Collections.unmodifiableMap(
                    new HashMap<String, List<String>>() {
                      {
                        put(DiffType.ADD.name(), new ArrayList<>());
                        put(DiffType.MOVE.name(), new ArrayList<>());
                        put(DiffType.REMOVE.name(), new ArrayList<>());
                        put(DiffType.MODIFY.name(), Arrays.asList(HEADERS_DATE));
                      }
                    }))
            .setDefaultCompareHandler(new JsonTestCompareHandler())
            .setResponseDataCompareHandler(new JsonTestCompareHandler())
            .build();

    // build data-store config for writing test-data in PROFILE mode and test-results in TEST mode.
    DataStoreConfig dataStoreConfig =
        DataStoreConfig.builder()
            .setDataStoreHandler(new FileBasedDataStoreHandler(DATASTORE_FILE))
            .build();

    // Bigqueue config for storing data before flushing to data-store.
    TestQueuedSenderConfig testQueuedSenderConfig =
        TestQueuedSenderConfig.builder()
            .setPath(BIQ_QUEUE_PATH)
            .setQueueSize(BIQ_QUEUE_SIZE)
            .setQueuePurgeIntervalInSeconds(BIQ_QUEUE_PURGE_INTERVAL)
            .build();

    // install all configs
    install(
        new SetupModule(
            Mode.DYNAMIC,
            requestSamplingConfig,
            serdeConfig,
            gojiraComparisonConfig,
            dataStoreConfig,
            testQueuedSenderConfig));

    // install this for method interception to work.
    install(new BindingsModule());
  }
}
