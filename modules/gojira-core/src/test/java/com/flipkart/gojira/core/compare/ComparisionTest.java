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

package com.flipkart.gojira.core.compare;

import com.flipkart.compare.TestCompareException;
import com.flipkart.compare.handlers.TestCompareHandler;
import com.flipkart.compare.handlers.json.JsonTestCompareHandler;
import com.flipkart.gojira.compare.config.GojiraComparisonConfig;
import com.flipkart.gojira.core.DI;
import com.flipkart.gojira.core.Mode;
import com.flipkart.gojira.core.SetupModule;
import com.flipkart.gojira.core.serde.DeserializeTest;
import com.flipkart.gojira.queuedsender.config.TestQueuedSenderConfig;
import com.flipkart.gojira.requestsampling.config.RequestSamplingConfig;
import com.flipkart.gojira.serde.TestSerdeException;
import com.flipkart.gojira.serde.config.SerdeConfig;
import com.flipkart.gojira.serde.handlers.TestSerdeHandler;
import com.flipkart.gojira.serde.handlers.json.JsonMapListSerdeHandler;
import com.flipkart.gojira.sinkstore.config.DataStoreConfig;
import com.flipkart.gojira.sinkstore.file.FileBasedDataStoreHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class ComparisionTest {

  @Test(expected = TestCompareException.class)
  public void checkCompare() throws TestSerdeException, TestCompareException {
    Map<String, String> map1 = new HashMap<>();
    map1.put("hi", "hello");
    map1.put("alpha", "beta");
    map1.put("jingle", "bells");
    DeserializeTest.TestClass testClass1 = new DeserializeTest.TestClass();
    testClass1.setMap(map1);

    Map<String, String> map2 = new HashMap<>();
    map2.put("hi", "hello");
    map2.put("jingle", "bells");
    map2.put("alpha", "gamma");
    DeserializeTest.TestClass testClass2 = new DeserializeTest.TestClass();
    testClass2.setMap(map2);

    RequestSamplingConfig requestSamplingConfig =
        RequestSamplingConfig.builder()
            .setSamplingPercentage(100)
            .setWhitelist(new ArrayList<>())
            .build();
    SerdeConfig serdeConfig =
        SerdeConfig.builder().setDefaultSerdeHandler(new JsonMapListSerdeHandler()).build();
    GojiraComparisonConfig comparisonConfig =
        GojiraComparisonConfig.builder()
            .setDiffIgnoreMap(null)
            .setDefaultCompareHandler(new JsonTestCompareHandler())
            .setResponseDataCompareHandler(new JsonTestCompareHandler())
            .build();
    DataStoreConfig dataStoreConfig =
        DataStoreConfig.builder()
            .setDataStoreHandler(
                new FileBasedDataStoreHandler("/var/log/flipkart/fk-gojira/gojira-data"))
            .build();
    TestQueuedSenderConfig testQueuedSenderConfig =
        TestQueuedSenderConfig.builder()
            .setPath("/var/log/flipkart/fk-gojira/gojira-messages")
            .setQueueSize(10L)
            .build();
    SetupModule profileOrTestModule =
        new SetupModule(
            Mode.PROFILE,
            requestSamplingConfig,
            serdeConfig,
            comparisonConfig,
            dataStoreConfig,
            testQueuedSenderConfig);

    DI.install(profileOrTestModule);
    TestCompareHandler testCompareHandler = new JsonTestCompareHandler();
    TestSerdeHandler testSerdeHandler = new JsonMapListSerdeHandler();
    testCompareHandler.compare(testSerdeHandler.serialize(map1), testSerdeHandler.serialize(map2));
  }
}
