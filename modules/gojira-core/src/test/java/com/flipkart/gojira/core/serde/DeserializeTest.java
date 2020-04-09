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

import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.http.HttpTestDataType;
import com.flipkart.gojira.models.http.HttpTestRequestData;
import com.flipkart.gojira.models.http.HttpTestResponseData;
import com.flipkart.gojira.serde.TestSerdeException;
import com.flipkart.gojira.serde.handlers.json.JsonDefaultTestSerdeHandler;
import com.flipkart.gojira.serde.handlers.json.JsonStdSerdeHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created by arunachalam.s on 11/10/17.
 */
public class DeserializeTest {

  private static final JsonDefaultTestSerdeHandler jsonDefaultSerdeHandler = new JsonDefaultTestSerdeHandler();
  private static final JsonStdSerdeHandler jsonStdSerdeHandler = new JsonStdSerdeHandler();

  @Test
  public void test_TestDataSerDeser() throws TestSerdeException {
    TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> testData = new TestData<>();
    HttpTestRequestData requestData = HttpTestRequestData.builder().build();
    HttpTestResponseData responseData = HttpTestResponseData.builder().build();
    testData.setRequestData(requestData);
    testData.setResponseData(responseData);
    TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> deserializedTestData = jsonDefaultSerdeHandler
        .deserialize(jsonDefaultSerdeHandler.serialize(testData), TestData.class);
    Assert.assertEquals(deserializedTestData.getId(), testData.getId());
  }

  @Test
  public void test_GenericSerDeser() throws TestSerdeException {
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

    List deserializedList = null;
    Map deserializedMap = null;
    try {
      byte[] serDataForList = jsonStdSerdeHandler.serialize(list);
      deserializedList = jsonStdSerdeHandler.deserialize(serDataForList, List.class);

      byte[] serDataForMap = jsonStdSerdeHandler.serialize(externalMap);
      deserializedMap = jsonStdSerdeHandler.deserialize(serDataForMap, Map.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    Assert.assertEquals(externalMap, deserializedMap);
    Assert.assertEquals(list, deserializedList);
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
      if (this == o) return true;
      if (!(o instanceof TestClass)) return false;
      TestClass testClass = (TestClass) o;
      return map.equals(testClass.map);
    }

    @Override
    public int hashCode() {
      return Objects.hash(map);
    }
  }
}