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

package com.flipkart.compare;

import com.flipkart.compare.handlers.TestCompareHandler;

/**
 * CompareHandlerRepository is the repository for custom compare handlers.
 */
public abstract class CompareHandlerRepository {

  protected TestCompareHandler defaultCompareHandler = null;

  /**
   * {@link #defaultCompareHandler} will be used as default CompareHandler when no annotated
   * CompareHandlers are provided.
   *
   * @return {@link #defaultCompareHandler} set in {@link ComparisonModule}
   */
  public abstract TestCompareHandler getDefaultCompareHandler();

  /**
   * {@link #defaultCompareHandler} will be used as default CompareHandler when no annotated
   * CompareHandlers are provided.
   *
   * @param compareHandler This method sets the default compare handler provided in {@link
   *                       ComparisonModule} to {@link #defaultCompareHandler}
   */
  public abstract void setDefaultCompareHandler(TestCompareHandler compareHandler);
}
