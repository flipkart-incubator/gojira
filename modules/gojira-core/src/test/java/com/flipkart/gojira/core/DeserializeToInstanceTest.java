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

import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.http.HttpTestDataType;
import com.flipkart.gojira.models.http.HttpTestRequestData;
import com.flipkart.gojira.models.http.HttpTestResponseData;
import com.flipkart.gojira.serde.TestSerdeException;
import com.flipkart.gojira.serde.handlers.json.JsonDefaultTestSerdeHandler;
import com.flipkart.gojira.serde.handlers.json.JsonTestSerdeHandler;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class DeserializeToInstanceTest {
  //Refer https://www.logicbig.com/tutorials/misc/jackson/reader-for-updating.html

  private JsonTestSerdeHandler jsonTestSerdeHandler = new JsonTestSerdeHandler();
  private JsonDefaultTestSerdeHandler jsonDefaultTestSerdeHandler = new JsonDefaultTestSerdeHandler();

  public void IntegerJsonUpdateTest() throws TestSerdeException {
    Integer x = 1;
    Integer y = 2;
    jsonTestSerdeHandler.deserializeToInstance(jsonTestSerdeHandler.serialize(y), x);
  }

  @Test
  public void integerandListWrapperJsonUpdateTest() throws TestSerdeException {
    IntegerAndListWrapper x = new IntegerAndListWrapper();
    x.setInteger(1);
    List<String> strings1 = new ArrayList<>();
    strings1.add("testing");
    strings1.add("started");
    x.setStringList(strings1);

    IntegerAndListWrapper y = new IntegerAndListWrapper();
    y.setInteger(2);
    List<String> strings2 = new ArrayList<>();
    strings2.add("testing");
    strings2.add("stopped");
    y.setStringList(strings2);

    jsonTestSerdeHandler
        .deserialize(jsonTestSerdeHandler.serialize(y), IntegerAndListWrapper.class);
    jsonTestSerdeHandler.deserializeToInstance(jsonTestSerdeHandler.serialize(y), x);
    Assert.assertEquals(x.getInteger(), y.getInteger());
    Assert.assertEquals(x.getStringList(), y.getStringList());
  }

  @Test
  public void listJsonUpdateTest() throws TestSerdeException {
    List<Integer> x = new ArrayList<>();
    x.add(1);
    x.add(2);
    List<Integer> y = new ArrayList<>();
    y.add(2);
    y.add(3);

    jsonTestSerdeHandler.deserializeToInstance(jsonDefaultTestSerdeHandler.serialize(y), x);
    Assert.assertEquals(4, x.size());
  }

  @Test
  public void testDataJsonUpdateTest() throws TestSerdeException {
    TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> testData1 = new TestData<>();
    HttpTestRequestData requestData1 = HttpTestRequestData.builder().build();
    HttpTestResponseData responseData1 = HttpTestResponseData.builder().build();
    testData1.setRequestData(requestData1);
    testData1.setResponseData(responseData1);

    TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> testData2 = new TestData<>();
    HttpTestRequestData requestData2 = HttpTestRequestData.builder().build();
    HttpTestResponseData responseData2 = HttpTestResponseData.builder().build();
    testData1.setRequestData(requestData2);
    testData1.setResponseData(responseData2);

    Assert.assertFalse(testData1.getId().equals(testData2.getId()));

    jsonDefaultTestSerdeHandler
        .deserializeToInstance(jsonDefaultTestSerdeHandler.serialize(testData1),
            testData2); //testData2 will be updated with testData1's ID

    Assert.assertEquals(testData1.getId(), testData2.getId());
  }

  public void enumJsonUpdateTest() throws TestSerdeException {
    EnumTest et = EnumTest.TEST;
    EnumTest updated_et = EnumTest.CHECK;
    JsonTestSerdeHandler jsonTestSerdeHandler = new JsonTestSerdeHandler();
    jsonTestSerdeHandler.deserialize(jsonTestSerdeHandler.serialize(et), EnumTest.class);
    jsonTestSerdeHandler.deserializeToInstance(jsonTestSerdeHandler.serialize(updated_et), et);
  }

  public enum EnumTest {
    TEST,
    CHECK
  }

  static class IntegerAndListWrapper {

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
