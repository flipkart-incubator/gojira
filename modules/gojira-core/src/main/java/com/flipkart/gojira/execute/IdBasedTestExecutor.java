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

package com.flipkart.gojira.execute;

import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.TestDataType;
import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.models.TestResponseData;
import com.flipkart.gojira.serde.SerdeHandlerRepository;
import com.flipkart.gojira.serde.TestSerdeException;
import com.flipkart.gojira.serde.handlers.TestSerdeHandler;
import com.flipkart.gojira.sinkstore.SinkException;
import com.flipkart.gojira.sinkstore.handlers.SinkHandler;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class is used as the interface for executing id based tests. */
public class IdBasedTestExecutor<T extends TestDataType> {

  private static final Logger LOGGER = LoggerFactory.getLogger(IdBasedTestExecutor.class);

  private SinkHandler sinkHandler = GuiceInjector.getInjector().getInstance(SinkHandler.class);
  private TestSerdeHandler testDataSerdeHandler =
      GuiceInjector.getInjector()
          .getInstance(SerdeHandlerRepository.class)
          .getTestDataSerdeHandler();

  /**
   * This method, given a testId, reads data by calling {@link SinkHandler#read(String)},
   * deserializes data by calling {@link TestSerdeHandler#deserialize(byte[], Class)} using {@link
   * SerdeHandlerRepository#getTestDataSerdeHandler()} instance and calls {@link
   * TestExecutor#execute(TestData, String)} using appropriate instance based on {@link
   * TestDataType}.
   *
   * @param testId testId against which we need to initiate execution.
   * @param clientId clientId is the identifier which can be used to know which system to hit.
   * @throws TestExecutionException if we are not able to initiate the execution
   * @throws SinkException if we are not able to read data from {@link SinkHandler} implementation
   * @throws TestSerdeException if we are not able to deserialize read data to
   *     {@link TestData}
   */
  public void execute(String testId, String clientId)
      throws TestExecutionException, SinkException, TestSerdeException {
    TestData<TestRequestData<T>, TestResponseData<T>, T> testData =
        testDataSerdeHandler.deserialize(sinkHandler.read(testId), TestData.class);
    GuiceInjector.getInjector()
        .getInstance(Key.get(TestExecutor.class, Names.named(testData.getRequestData().getType())))
        .execute(testData, clientId);
  }
}
