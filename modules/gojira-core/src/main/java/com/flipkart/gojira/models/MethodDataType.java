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

package com.flipkart.gojira.models;

import com.flipkart.gojira.core.Mode;

/**
 * Different types of method data that are required to be captured against a single method
 * invocation.
 */
public enum MethodDataType {
  /**
   * Method argument data recorded in {@link Mode#PROFILE} mode before method execution begins. This
   * is necessary for matching purposes when there are multiple entries against a single method. It
   * also serves the purpose of comparing method arguments before execution in other {@link Mode}.
   * Unfortunately comparison is not optional since it is needed for getting a matching set of
   * data.
   */
  ARGUMENT_BEFORE,

  /**
   * Return data recorded in {@link Mode#PROFILE} after method is invoked. This is used to return in
   * other execution {@link Mode} to simulate profiled behavior.
   */
  RETURN,

  /**
   * Exception data recorded in {@link Mode#PROFILE} after method is invoked. This is used to thrown
   * exception in other execution {@link Mode} to simulate profiled behavior.
   */
  EXCEPTION,

  /**
   * Method argument data recorded in {@link Mode#PROFILE} mode after method execution ends. This is
   * necessary if there are cases where method arguments are updated within the method context and
   * are required to be available to the calling method.
   */
  ARGUMENT_AFTER

}
