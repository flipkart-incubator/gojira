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

import org.aopalliance.intercept.MethodInvocation;

/**
 * Implementation for {@link MethodDataInterceptorHandler} for mode {@link Mode#NONE}.
 */
public class NoneMethodDataInterceptorHandler implements MethodDataInterceptorHandler {

  /**
   * Simply invokes {@link MethodInvocation#proceed()}.
   *
   * @param invocation intercepted method invocation
   * @return the object returned by the called method
   * @throws Throwable for any exception thrown by the called method
   */
  @Override
  public Object handle(MethodInvocation invocation) throws Throwable {
    return invocation.proceed();
  }
}
