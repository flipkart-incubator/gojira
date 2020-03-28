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

import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.TestDataType;
import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.models.TestResponseData;
import com.flipkart.gojira.serde.SerdeHandlerRepository;
import com.flipkart.gojira.sinkstore.handlers.SinkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link StartEndTestHandler} for mode {@link Mode#SERIALIZE}
 *
 * @param <T> type of test-data
 */
public class SerializeStartEndTestHandler<T extends TestDataType> implements
    StartEndTestHandler<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SerializeStartEndTestHandler.class);

  /**
   * sinkHandler for persisting test-data
   */
  private SinkHandler sinkHandler = GuiceInjector.getInjector().getInstance(SinkHandler.class);
  /**
   * serdeHandlerRepository to get serializer for test-data
   */
  private SerdeHandlerRepository serdeHandlerRepository = GuiceInjector.getInjector()
      .getInstance(SerdeHandlerRepository.class);

  public SerializeStartEndTestHandler() {
  }

  /**
   * If is is null or empty, throws a {@link RuntimeException}
   * <p>
   * Reads the test data using {@link SinkHandler#read(String)} and deserializes using {@link
   * SerdeHandlerRepository#getTestDataSerdeHandler()} instance.
   * <p>
   * If testData is null, throws a {@link RuntimeException}
   * <p>
   * Begins execution by calling {@link ProfileRepository#begin(String)} and adds {@link TestData}
   * for execution by calling {@link ProfileRepository#setTestData(TestData)} to make method
   * intercepted and response data recorded in {@link Mode#PROFILE} mode available.
   *
   * @param id          this is the id, which will be used for synchronizing testing across multiple
   *                    threads within a single request-response scope.
   * @param requestData this is the request-data with which test is initiated
   */
  @Override
  public void start(String id, TestRequestData<T> requestData) {
    if (id == null || id.isEmpty()) {
      // TODO: Check if well-defined exception can be thrown.
      throw new RuntimeException("test id is null");
    }

    try {
      TestData<TestRequestData<T>, TestResponseData<T>, T> testData = serdeHandlerRepository
          .getTestDataSerdeHandler().deserialize(sinkHandler.read(id), TestData.class);
      if (testData == null) {
        // TODO: Check if well-defined exception can be thrown.
        throw new RuntimeException("no data available against mentioned test id: " + id);
      }
      ProfileRepository.begin(id);
      ProfileRepository.setTestData(testData);
    } catch (Exception e) {
      LOGGER.error("unable to fetch data against test id: " + id);
      // TODO: Check if well-defined exception can be thrown.
      throw new RuntimeException();
    }
  }

  /**
   * Ends the execution by calling {@link ProfileRepository#end()}
   *
   * @param responseData this is the response-data after the request is processed by the client
   *                     application.
   */
  @Override
  public void end(TestResponseData<T> responseData) {
    ProfileRepository.end();
  }
}