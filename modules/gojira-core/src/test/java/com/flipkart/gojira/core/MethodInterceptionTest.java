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

import com.flipkart.gojira.compare.annotations.CompareHandler;
import com.flipkart.gojira.core.annotations.ProfileOrTest;
import com.flipkart.gojira.hash.annotations.HashHandler;

public class MethodInterceptionTest {

  @ProfileOrTest
  public String checkMethodInterception(Long input1,
      @HashHandler(hashHandlerClass = ByteArrayHashHandler.class)
      @CompareHandler(compareHandlerClass = ByteArrayCompareHandler.class) byte[] input2,
      @CompareHandler(compareHandlerClass = SystemTimeCompareHandler.class) Long time,
      @HashHandler(hashHandlerClass = ByteArrayHashHandler.class) Integer input3) {
    return input1.toString();
  }
}
