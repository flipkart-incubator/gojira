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

import com.flipkart.gojira.models.ExecutionData;
import com.flipkart.gojira.models.TestDataType;
import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.models.TestResponseData;

/**
 * Interface for performing various logic around starting and ending test in various {@link Mode}.
 */
public interface StartEndTestHandler<T extends TestDataType> {

  /**
   * Implementations of this interface are expected to take care of setting {@link
   * ExecutionData#getProfileState()} ()} for different {@link Mode}.
   *
   * <p>They are also expected to take care of retrieving test data against id if required and call
   * {@link ProfileRepository#begin(String)} as needed per {@link Mode}
   *
   * @param id this is the id, which will be used for synchronizing testing across multiple threads
   *     within a single request-response scope.
   * @param requestData this is the request-data with which test is initiated
   * @param requestMode this is the mode of execution of gojira at a request level
   */
  void start(String id, TestRequestData<T> requestData, Mode requestMode);

  /**
   * Implementations of this interface are expected to take care of setting {@link
   * ExecutionData#getProfileState()} for different {@link Mode}.
   *
   * <p>They are also expected to take care of storing test data against test-data id if required
   * and call {@link ProfileRepository#end()} as needed per {@link Mode}
   *
   * @param responseData this is the response-data after the request is processed by the client
   *     application.
   */
  void end(TestResponseData<T> responseData);
}
