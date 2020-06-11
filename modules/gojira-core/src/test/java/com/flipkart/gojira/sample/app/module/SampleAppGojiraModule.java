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

public class SampleAppGojiraModule extends AbstractModule {
  private static final String BIQ_QUEUE_PATH = "/tmp/gojira/";

  public SampleAppGojiraModule() {}

  @Override
  protected void configure() {
    RequestSamplingConfig requestSamplingConfig =
        RequestSamplingConfig.builder()
            .setSamplingPercentage(100)
            .setWhitelist(Arrays.asList("GET /github/(.*)", "POST /httpbin/post"))
            .build();

    SerdeConfig serdeConfig =
        SerdeConfig.builder().setDefaultSerdeHandler(new JsonDefaultTestSerdeHandler()).build();

    GojiraComparisonConfig gojiraComparisonConfig =
        GojiraComparisonConfig.builder()
            .setDiffIgnoreMap(
                Collections.unmodifiableMap(
                    new HashMap<String, List<String>>() {
                      {
                        put("ADD", new ArrayList<>());
                        put("MOVE", new ArrayList<>());
                        put("REMOVE", new ArrayList<>());
                        put("MODIFY", Arrays.asList("/, headers, Date, /"));
                      }
                    }))
            .setDefaultCompareHandler(new JsonTestCompareHandler())
            .setResponseDataCompareHandler(new JsonTestCompareHandler())
            .build();

    DataStoreConfig dataStoreConfig =
        DataStoreConfig.builder()
            .setDataStoreHandler(new FileBasedDataStoreHandler("/tmp/datastore"))
            .build();

    TestQueuedSenderConfig testQueuedSenderConfig =
        TestQueuedSenderConfig.builder()
            .setPath(BIQ_QUEUE_PATH)
            .setQueueSize(100L)
            .setQueuePurgeIntervalInSeconds(1)
            .build();

    install(
        new SetupModule(
            Mode.DYNAMIC,
            requestSamplingConfig,
            serdeConfig,
            gojiraComparisonConfig,
            dataStoreConfig,
            testQueuedSenderConfig));

    install(new BindingsModule());
  }
}
