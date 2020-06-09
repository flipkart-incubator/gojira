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
import com.flipkart.gojira.serde.handlers.json.JsonMapListSerdeHandler;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class DeserializeToInstanceTest {
  // Refer https://www.logicbig.com/tutorials/misc/jackson/reader-for-updating.html

  private JsonMapListSerdeHandler jsonMapListSerdeHandler = new JsonMapListSerdeHandler();
  private JsonDefaultTestSerdeHandler jsonDefaultTestSerdeHandler =
      new JsonDefaultTestSerdeHandler();

  // Primitives are neither expected nor supported to be updated
  @Test
  public void test_intJsonUpdate() throws TestSerdeException {
    int intBase = 1;
    int intToUpdate = 2;

    new JsonMapListSerdeHandler()
        .deserializeToInstance(new JsonDefaultTestSerdeHandler().serialize(intBase), intToUpdate);

    Assert.assertNotEquals(intBase, intToUpdate);
  }

  // Primitives are neither expected nor supported to be updated
  @Test
  public void test_IntegerJsonUpdate() throws TestSerdeException {
    Integer integerBase = 1;
    Integer integerToUpdate = 2;
    jsonDefaultTestSerdeHandler.deserializeToInstance(
        jsonDefaultTestSerdeHandler.serialize(integerBase), integerToUpdate);

    Assert.assertNotEquals(integerBase, integerToUpdate);
  }

  // Primitives are neither expected nor supported to be updated
  @Test
  public void test_StringJsonUpdate() throws TestSerdeException {
    String stringBase = "abc";
    String stringToUpdate = "def";

    jsonDefaultTestSerdeHandler.deserializeToInstance(
        jsonDefaultTestSerdeHandler.serialize(stringBase), stringToUpdate);

    Assert.assertNotEquals(stringBase, stringToUpdate);
  }

  // Enums are neither expected nor supported to be updated
  @Test
  public void test_EnumJsonUpdate() throws TestSerdeException {
    EnumTest etBase = EnumTest.TEST;
    EnumTest etToUpdate = EnumTest.CHECK;
    jsonDefaultTestSerdeHandler.deserializeToInstance(
        jsonDefaultTestSerdeHandler.serialize(etBase), etToUpdate);
    Assert.assertNotEquals(etBase, etToUpdate);
  }

  @Test
  public void test_IntegerAndListWrapperJsonUpdate() throws TestSerdeException {
    IntegerAndListWrapper integerAndListWrapperBase = new IntegerAndListWrapper();
    integerAndListWrapperBase.setInteger(1);
    List<String> stringList1 = new ArrayList<>();
    stringList1.add("testing");
    stringList1.add("started");
    integerAndListWrapperBase.setStringList(stringList1);

    IntegerAndListWrapper integerAndListWrapperToUpdate = new IntegerAndListWrapper();
    integerAndListWrapperToUpdate.setInteger(2);
    List<String> stringList2 = new ArrayList<>();
    stringList2.add("testing");
    stringList2.add("stopped");
    integerAndListWrapperToUpdate.setStringList(stringList2);

    jsonDefaultTestSerdeHandler.deserializeToInstance(
        jsonDefaultTestSerdeHandler.serialize(integerAndListWrapperBase),
        integerAndListWrapperToUpdate);

    Assert.assertEquals(
        integerAndListWrapperBase.getInteger(), integerAndListWrapperToUpdate.getInteger());
    Assert.assertEquals(
        integerAndListWrapperBase.getStringList(), integerAndListWrapperToUpdate.getStringList());
  }

  // Using JsonDefaultTestSerdeHandler for Lists and Maps will lead to deep merge which is not
  // expected in Gojira's use case. See test_ListJsonUpdate_JsonMapListSerdeHandler() for more
  // details.
  @Test(expected = AssertionError.class)
  public void test_ListJsonUpdate() throws TestSerdeException {
    List<Integer> listBase = new ArrayList<>();
    listBase.add(1);
    listBase.add(2);
    List<Integer> listToUpdate = new ArrayList<>();
    listToUpdate.add(2);
    listToUpdate.add(3);

    jsonDefaultTestSerdeHandler.deserializeToInstance(
        jsonDefaultTestSerdeHandler.serialize(listBase), listToUpdate);
    Assert.assertEquals("The list should be replaced, not deep merged.", listBase, listToUpdate);
  }

  // Lists and Maps will be replaced and not deep merged as per Gojira's use-case when
  // JsonMapListSerdeHandler is used.
  @Test
  public void test_ListJsonUpdate_JsonMapListSerdeHandler() throws TestSerdeException {
    List<Integer> listBase = new ArrayList<>();
    listBase.add(1);
    listBase.add(2);
    List<Integer> listToUpdate = new ArrayList<>();
    listToUpdate.add(2);
    listToUpdate.add(3);

    jsonMapListSerdeHandler.deserializeToInstance(
        jsonMapListSerdeHandler.serialize(listBase), listToUpdate);
    Assert.assertEquals(listBase, listToUpdate);
  }

  @Test
  public void test_TestDataJsonUpdate() throws TestSerdeException {
    TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> testDataBase =
        new TestData<>();
    HttpTestRequestData requestData1 = HttpTestRequestData.builder().build();
    HttpTestResponseData responseData1 = HttpTestResponseData.builder().build();
    testDataBase.setRequestData(requestData1);
    testDataBase.setResponseData(responseData1);

    TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> testDataToUpdate =
        new TestData<>();
    HttpTestRequestData requestData2 = HttpTestRequestData.builder().build();
    HttpTestResponseData responseData2 = HttpTestResponseData.builder().build();
    testDataBase.setRequestData(requestData2);
    testDataBase.setResponseData(responseData2);

    Assert.assertNotEquals(testDataBase.getId(), testDataToUpdate.getId());

    jsonDefaultTestSerdeHandler.deserializeToInstance(
        jsonDefaultTestSerdeHandler.serialize(testDataBase),
        testDataToUpdate); // testDataToUpdate's ID will be updated with testDataBase's ID

    Assert.assertEquals(testDataBase.getId(), testDataToUpdate.getId());
  }

  private enum EnumTest {
    TEST,
    CHECK
  }

  private static class IntegerAndListWrapper {

    private Integer integer;

    private List<String> stringList;

    public Integer getInteger() {
      return integer;
    }

    public void setInteger(Integer integer) {
      this.integer = integer;
    }

    public List<String> getStringList() {
      return stringList;
    }

    public void setStringList(List<String> stringList) {
      this.stringList = stringList;
    }
  }
}
