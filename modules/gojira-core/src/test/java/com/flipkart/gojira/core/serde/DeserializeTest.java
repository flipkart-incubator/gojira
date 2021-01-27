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

package com.flipkart.gojira.core.serde;

import com.fasterxml.jackson.core.type.TypeReference;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.http.HttpTestDataType;
import com.flipkart.gojira.models.http.HttpTestRequestData;
import com.flipkart.gojira.models.http.HttpTestResponseData;
import com.flipkart.gojira.serde.TestSerdeException;
import com.flipkart.gojira.serde.handlers.json.JsonDefaultTestSerdeHandler;
import com.flipkart.gojira.serde.handlers.json.JsonMapListSerdeHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;

public class DeserializeTest {

  private final JsonDefaultTestSerdeHandler jsonDefaultSerdeHandler =
      new JsonDefaultTestSerdeHandler();
  private final JsonMapListSerdeHandler jsonMapListSerdeHandler = new JsonMapListSerdeHandler();

  @Test
  public void test_GenericSerDeser() throws TestSerdeException {
    Map<String, String> testClassInternalMap = new HashMap<>();
    testClassInternalMap.put("hi", "hello");
    testClassInternalMap.put("alpha", "beta");
    testClassInternalMap.put("jingle", "bells");
    TestClass testClass = new TestClass();
    testClass.map = testClassInternalMap;
    Map<String, TestClass> stringTestClassMap = new HashMap<>();
    stringTestClassMap.put("gamma", testClass);
    TypeReference typeReference = new TypeReference<Map<String, TestClass>>() {};
    byte[] serializedBytes = jsonDefaultSerdeHandler.serialize(stringTestClassMap);
    Map<String, TestClass> stringTestClassMapDeSer =
        jsonDefaultSerdeHandler.deserialize(serializedBytes, typeReference.getType());
    Assert.assertNotNull(stringTestClassMapDeSer);
  }

  @Test
  public void test_TestDataSerDeser() throws TestSerdeException {
    TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> testData =
        new TestData<>();
    HttpTestRequestData requestData = HttpTestRequestData.builder().build();
    HttpTestResponseData responseData = HttpTestResponseData.builder().build();
    testData.setRequestData(requestData);
    testData.setResponseData(responseData);
    byte[] serializedTestData = jsonDefaultSerdeHandler.serialize(testData);
    TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> deserializedTestData =
        jsonDefaultSerdeHandler.deserialize(serializedTestData, TestData.class);
    Assert.assertEquals(deserializedTestData.getId(), testData.getId());
  }

  @Test
  public void test_MapListSerDeser() throws TestSerdeException {
    TestClass internalTestClass = new TestClass();
    Map<String, String> mapInInternalTestClass = new HashMap<>();
    mapInInternalTestClass.put("hi", "hello");
    mapInInternalTestClass.put("alpha", "beta");
    internalTestClass.map = mapInInternalTestClass;

    Map<TestClass, String> internalMap = new HashMap<>();
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

    List<Map<List<Map<TestClass, String>>, TestClass>> list = new ArrayList<>();
    list.add(externalMap);

    List deserializedList = null;
    Map deserializedMap = null;
    try {
      byte[] serDataForList = jsonMapListSerdeHandler.serialize(list);
      deserializedList = jsonMapListSerdeHandler.deserialize(serDataForList, List.class);

      byte[] serDataForMap = jsonMapListSerdeHandler.serialize(externalMap);
      deserializedMap = jsonMapListSerdeHandler.deserialize(serDataForMap, Map.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    Assert.assertEquals(list, deserializedList);
    Assert.assertEquals(externalMap, deserializedMap);
  }

  @Test
  public void test_Map() throws TestSerdeException {
    Map<String, String> map = new HashMap<>();
    map.put("testKey", "testValue");
    map.put("testKeyAnother", "testValueAnother");
    String ser = new String(jsonMapListSerdeHandler.serialize(map));
    jsonMapListSerdeHandler.deserialize(ser.getBytes(), Map.class);
  }

  @Test
  public void test_CustomMapInsideObjectSerDeser() throws TestSerdeException {
    TestClass testClassAsKey = new TestClass();
    Map<String, String> mapInInternalTestClass = new HashMap<>();
    mapInInternalTestClass.put("hi", "hello");
    mapInInternalTestClass.put("alpha", "beta");
    testClassAsKey.map = mapInInternalTestClass;

    TestClass testClassAsValue = new TestClass();
    Map<String, String> mapInExternalTestClass = new LinkedHashMap<>();
    mapInExternalTestClass.put("jingle", "bells");
    mapInExternalTestClass.put("gamma", "delta");
    testClassAsValue.map = mapInExternalTestClass;

    TestClassWithCustomMap testClassWithCustomMap = new TestClassWithCustomMap();
    Map<TestClass, TestClass> map = new HashMap<>();
    map.put(testClassAsKey, testClassAsValue);
    testClassWithCustomMap.setMap(map);

    TestClassWithCustomMap deserializedTestClassWithCustomMap = null;
    try {
      String serDataForTestClassNew =
          new String(jsonMapListSerdeHandler.serialize(testClassWithCustomMap));
      deserializedTestClassWithCustomMap =
          jsonMapListSerdeHandler.deserialize(
              serDataForTestClassNew.getBytes(), TestClassWithCustomMap.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    Assert.assertEquals(testClassWithCustomMap, deserializedTestClassWithCustomMap);
  }

  public static class TestClass {

    Map<String, String> map = new HashMap<>();

    public Map<String, String> getMap() {
      return map;
    }

    public void setMap(Map<String, String> map) {
      this.map = map;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof TestClass)) {
        return false;
      }
      TestClass testClass = (TestClass) o;
      return map.equals(testClass.map);
    }

    @Override
    public int hashCode() {
      return Objects.hash(map);
    }
  }

  public static class TestClassWithCustomMap {
    Map<TestClass, TestClass> map;

    public Map<TestClass, TestClass> getMap() {
      return map;
    }

    public void setMap(Map<TestClass, TestClass> map) {
      this.map = map;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof TestClassWithCustomMap)) {
        return false;
      }
      TestClassWithCustomMap that = (TestClassWithCustomMap) o;
      return Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
      return Objects.hash(map);
    }
  }
}
