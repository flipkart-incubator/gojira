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

package com.flipkart.gojira.compare;

import com.flipkart.compare.CompareHandlerRepository;
import com.flipkart.compare.ComparisonModule;
import com.flipkart.compare.handlers.TestCompareHandler;
import com.flipkart.gojira.compare.annotations.CompareHandler;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.intercept.MethodInvocation;

/**
 * This class extends {@link CompareHandlerRepository} and provides additional compare handlers for
 * response data and method arguments.
 */
public abstract class GojiraCompareHandlerRepository extends CompareHandlerRepository {

  protected TestCompareHandler responseDataCompareHandler = null;
  protected ConcurrentHashMap<String, TestCompareHandler> methodArgumentDataCompareHandler =
      new ConcurrentHashMap<>();

  /**
   * Returns {@link #responseDataCompareHandler} set in {@link ComparisonModule}.
   *
   * @return {@link #responseDataCompareHandler} set in {@link ComparisonModule}.
   */
  public abstract TestCompareHandler getResponseDataCompareHandler();

  /**
   * This sets the default response compare handler provided in {@link ComparisonModule}.
   *
   * @param compareHandler to set as {@link #responseDataCompareHandler}.
   */
  abstract void setResponseDataCompareHandler(TestCompareHandler compareHandler);

  /**
   * This method does the following: Checks if there is an entry in {@link
   * #methodArgumentDataCompareHandler}. Key is methodInvocation.getMethod().toGenericString() + "|"
   * + argument. If entry exists returns it. Else checks if there exists an {@link CompareHandler}
   * associated with the method argument. If it exists, it will create an * instance and add it to
   * {@link #methodArgumentDataCompareHandler} else add {@link #defaultCompareHandler} to {@link
   * #methodArgumentDataCompareHandler}
   *
   * @param methodInvocation current methodInvocation instance.
   * @param argument specific argument sequence # whose compareHandler is needed.
   * @return return the entry against the key specified above.
   */
  public abstract TestCompareHandler getOrUpdateAndGetOrDefaultMethodArgumentDataCompareHandler(
      MethodInvocation methodInvocation, int argument) throws Throwable;
}
