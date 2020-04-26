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

import com.flipkart.gojira.models.TestData;

/**
 * Interface to execute tests for different types of {@link TestData}.
 */
public interface TestExecutor<T extends TestData> {
  /**
   * Method to execute tests for given testData and clientId.
   *
   * @param testData testData which is used for invoking execution
   * @param clientId identifier to indicate which system hit
   * @throws TestExecutionException on failure to initiate a test execution
   */
  void execute(T testData, String clientId) throws TestExecutionException;
}
