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

import com.flipkart.compare.TestCompareException;
import com.flipkart.compare.handlers.TestCompareHandler;
import com.flipkart.compare.handlers.json.JsonTestCompareHandler;
import com.flipkart.gojira.compare.config.GojiraComparisonConfig;
import com.flipkart.gojira.execute.TestExecutionException;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.http.HttpTestDataType;
import com.flipkart.gojira.models.http.HttpTestRequestData;
import com.flipkart.gojira.models.http.HttpTestResponseData;
import com.flipkart.gojira.queuedsender.config.TestQueuedSenderConfig;
import com.flipkart.gojira.requestsampling.config.RequestSamplingConfig;
import com.flipkart.gojira.serde.TestSerdeException;
import com.flipkart.gojira.serde.config.SerdeConfig;
import com.flipkart.gojira.serde.handlers.TestSerdeHandler;
import com.flipkart.gojira.serde.handlers.json.JsonDefaultTestSerdeHandler;
import com.flipkart.gojira.serde.handlers.json.JsonStdSerdeHandler;
import com.flipkart.gojira.sinkstore.config.DataStoreConfig;
import com.flipkart.gojira.sinkstore.file.FileBasedDataStoreHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created by arunachalam.s on 11/10/17.
 */
public class TestDataSerdeTest {

  private static final JsonDefaultTestSerdeHandler jsonDefaultSerdeHandler = new JsonDefaultTestSerdeHandler();

  @Test
  public void testHttpDataSerialization() throws TestSerdeException {
    TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> testData = new TestData<>();
    HttpTestRequestData requestData = HttpTestRequestData.builder().build();
    HttpTestResponseData responseData = HttpTestResponseData.builder().build();
    testData.setRequestData(requestData);
    testData.setResponseData(responseData);
    TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> testData1 = jsonDefaultSerdeHandler
        .deserialize(jsonDefaultSerdeHandler.serialize(testData), TestData.class);
  }

  public void testStringDeserializeToInstance() throws TestSerdeException {
    String s1 = "abc";
    String s2 = "def";

    new JsonStdSerdeHandler()
        .deserializeToInstance(new JsonDefaultTestSerdeHandler().serialize(s1), s2);

    System.out.println(s2);
  }

  public void testIntDeserializeToInstance() throws TestSerdeException {
    int i1 = 3;
    int i2 = 4;

    new JsonStdSerdeHandler()
        .deserializeToInstance(new JsonDefaultTestSerdeHandler().serialize(i1), i2);

    System.out.println(i2);
  }

  public void testIntegerDeserializeToInstance() throws TestSerdeException {
    Integer i1 = 3;
    Integer i2 = 4;

    new JsonStdSerdeHandler()
        .deserializeToInstance(new JsonDefaultTestSerdeHandler().serialize(i1), i2);

    System.out.println(i2);
  }

  @Test(expected = TestCompareException.class)
  public void checkCompare()
      throws TestSerdeException, TestCompareException, TestExecutionException {
    TestCompareHandler testCompareHandler = new JsonTestCompareHandler();
    TestSerdeHandler testSerdeHandler = new JsonStdSerdeHandler();
    TestClass testClass1 = new TestClass();
    Map<String, String> map1 = new HashMap<>();
    map1.put("hi", "hello");
    map1.put("alpha", "beta");
    map1.put("jingle", "bells");
    testClass1.map = map1;
    TestClass testClass2 = new TestClass();
    Map<String, String> map2 = new HashMap<>();
    map2.put("hi", "hello");
    map2.put("jingle", "bells");
    map2.put("alpha", "gamma");
    testClass2.map = map2;
    RequestSamplingConfig requestSamplingConfig = RequestSamplingConfig.builder()
        .setSamplingPercentage(100)
        .setWhitelist(new ArrayList<>())
        .build();
    SerdeConfig serdeConfig = SerdeConfig.builder()
        .setDefaultSerdeHandler(new JsonStdSerdeHandler()).build();
    GojiraComparisonConfig comparisonConfig = GojiraComparisonConfig.builder()
        .setDiffIgnoreMap(null)
        .setDefaultCompareHandler(new JsonTestCompareHandler())
        .setResponseDataCompareHandler(new JsonTestCompareHandler())
        .build();
    DataStoreConfig dataStoreConfig = DataStoreConfig.builder().setDataStoreHandler(
        new FileBasedDataStoreHandler("/var/log/flipkart/fk-gojira/gojira-data"))
        .build();
    TestQueuedSenderConfig testQueuedSenderConfig = TestQueuedSenderConfig.builder()
        .setPath("/var/log/flipkart/fk-gojira/gojira-messages")
        .setQueueSize(10L)
        .build();
    SetupModule profileOrTestModule = new SetupModule(Mode.PROFILE,
        requestSamplingConfig,
        serdeConfig,
        comparisonConfig,
        dataStoreConfig,
        testQueuedSenderConfig);

    DI.install(profileOrTestModule);
    testCompareHandler.compare(testSerdeHandler.serialize(map1), testSerdeHandler.serialize(map2));
  }

  @Test
  public void testSerDeser() throws TestSerdeException {
    JsonStdSerdeHandler jsonStdSerdeHandler = new JsonStdSerdeHandler();

    List<Map<List<Map<TestClass, String>>, TestClass>> list = new ArrayList<>();

    Map<TestClass, String> internalMap = new HashMap<>();

    TestClass internalTestClass = new TestClass();
    Map<String, String> mapInInternalTestClass = new HashMap<>();
    mapInInternalTestClass.put("hi", "hello");
    mapInInternalTestClass.put("alpha", "beta");
    internalTestClass.map = mapInInternalTestClass;
    internalMap.put(internalTestClass, "testing");

    List<Map<TestClass, String>> internalList = new ArrayList<>();
    internalList.add(internalMap);

    TestClass externalTestClass = new TestClass();
    Map<String, String> mapInExternalTestClass = new LinkedHashMap<>();
    mapInExternalTestClass.put("jingle", "bells");
    mapInExternalTestClass.put("gamma", "delta");
    externalTestClass.map = mapInExternalTestClass;

    Map<List<Map<TestClass, String>>, TestClass> externalMap = new HashMap<>();
    externalMap.put(internalList, externalTestClass);

    list.add(externalMap);
    String jsonStdSerdeHandlerResult;

    try {
      byte[] serDataForList = jsonStdSerdeHandler.serialize(list);
      jsonStdSerdeHandler.deserialize(serDataForList, List.class);

      byte[] serDataForMap = jsonStdSerdeHandler.serialize(externalMap);
      jsonStdSerdeHandler.deserialize(serDataForMap, Map.class);
      jsonStdSerdeHandlerResult = "SUCCESS";
    } catch (Exception e) {
      e.printStackTrace();
      jsonStdSerdeHandlerResult = "FAILURE";
    }

    Assert.assertEquals("SUCCESS", jsonStdSerdeHandlerResult);
  }

  public static class TestClass {

    Map<String, String> map;

    public Map<String, String> getMap() {
      return map;
    }

    public void setMap(Map<String, String> map) {
      this.map = map;
    }
  }
}