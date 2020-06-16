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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class DI {
  public static Injector INJECTOR = null;

  /**
   * Creates guice injector.
   *
   * @param modules for which guice injector is to be created.
   */
  public static void install(Module... modules) {
    synchronized (DI.class) {
      INJECTOR = Guice.createInjector(modules);
    }
  }

  /** Returns latest guice injector instance. */
  public static Injector di() {
    return INJECTOR;
  }
}
