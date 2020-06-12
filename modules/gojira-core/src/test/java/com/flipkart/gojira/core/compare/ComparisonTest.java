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

import com.flipkart.compare.ComparisonModule;
import com.flipkart.compare.TestCompareException;
import com.flipkart.compare.config.ComparisonConfig;
import com.flipkart.compare.handlers.TestCompareHandler;
import com.flipkart.compare.handlers.json.JsonTestCompareHandler;
import com.flipkart.gojira.compare.config.GojiraComparisonConfig;
import com.flipkart.gojira.core.DI;
import com.flipkart.gojira.core.serde.DeserializeTest;
import com.flipkart.gojira.serde.TestSerdeException;
import com.flipkart.gojira.serde.handlers.TestSerdeHandler;
import com.flipkart.gojira.serde.handlers.json.JsonMapListSerdeHandler;
import java.util.HashMap;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Class to test comparison of Map.
 */
public class ComparisonTest {

  /**
   * Setup for the class.
   * Installs the {@link ComparisonModule} with {@link ComparisonConfig} for testing.
   */
  @BeforeClass
  public static void setup() {
    ComparisonConfig comparisonConfig =
        GojiraComparisonConfig.builder()
            .setDiffIgnoreMap(null)
            .setDefaultCompareHandler(new JsonTestCompareHandler())
            .setResponseDataCompareHandler(new JsonTestCompareHandler())
            .build();
    DI.install(new ComparisonModule(comparisonConfig));
  }

  /**
   * Creates 2 instances of Map with different data for comparison purposes and checks that
   * exception is thrown.
   * @throws TestSerdeException serialization exception
   * @throws TestCompareException comparison exception
   */
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

    TestCompareHandler testCompareHandler = new JsonTestCompareHandler();
    TestSerdeHandler testSerdeHandler = new JsonMapListSerdeHandler();
    testCompareHandler.compare(testSerdeHandler.serialize(map1), testSerdeHandler.serialize(map2));
  }
}
