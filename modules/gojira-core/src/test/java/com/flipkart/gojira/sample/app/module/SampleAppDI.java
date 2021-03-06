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

package com.flipkart.gojira.sample.app.module;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * DI class for SampleApp.
 */
public class SampleAppDI {
  public static Injector INJECTOR = null;

  /**
   * Calls {@link Guice#createInjector(Module...)}.
   * @param module creates a {@link com.flipkart.gojira.core.injectors.GuiceInjector}
   *               with the given module and assigns it to {@link #INJECTOR}
   */
  public static void install(Module module) {
    synchronized (SampleAppDI.class) {
      INJECTOR = Guice.createInjector(module);
    }
  }

  /**
   * returns the {@link #INJECTOR}.
   * @return returns the {@link #INJECTOR}
   */
  public static Injector di() {
    return INJECTOR;
  }
}
