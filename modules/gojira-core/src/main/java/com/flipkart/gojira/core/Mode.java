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

/**
 * Different types of modes the gojira library runs in.
 */
public enum Mode {
  /**
   * Use this method when profiling production traffic.
   */
  PROFILE,
  /**
   * Use this method when replaying for regression testing.
   */
  TEST,
  /**
   * Use this mode, if you do not want data to be profiled or tested. This has the least non-
   * functional impact on the application. In this {@link Mode}, as a performance optimization,
   * for Guice {@link org.aopalliance.intercept.MethodInterceptor} is not bound.
   */
  NONE,
  /**
   * request-level mode. if you set this as the mode, all requests are expected to provide
   * {@link GojiraConstants#MODE_HEADER}. If not, it will be assumed to {@link Mode#NONE}.
   * {@link ProfileOrTestMethodInterceptor} will be bound in this mode.
   */
  DYNAMIC,
  /**
   * Use this mode for testing de-serialization of data profiled. More often than not, it is
   * observed that de-serialization of method dat is the biggest friction to on-boarding, so we
   * have added this as a mode, so integrating applications can use this {@link  Mode} to identify
   * all failures, before running in {@link Mode#TEST}.
   */
  SERIALIZE
}
