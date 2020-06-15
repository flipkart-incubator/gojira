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

package com.flipkart.gojira.core.injectors;

import com.flipkart.gojira.core.BindingsModule;
import com.flipkart.gojira.core.SetupModule;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GuiceInjector is primary guice injector instance for {@link SetupModule} and {@link
 * BindingsModule}.
 */
public class GuiceInjector {

  private static final Logger LOGGER = LoggerFactory.getLogger(GuiceInjector.class);

  private static volatile Injector injector = null;

  /**
   * Assigns the provided {@link Injector} instance.
   *
   * @param givenInjector If already assigned, throws an {@link IllegalStateException}
   */
  public static void assignInjector(Injector givenInjector) {
    if (injector == null) {
      synchronized (GuiceInjector.class) {
        if (injector == null) {
          injector = givenInjector;
        }
      }
    } else {
      LOGGER.error("Injector instance was already assigned.");
      throw new IllegalStateException("Injector instance was already assigned.");
    }
  }

  /**
   * Mark assigned injector as null. Added for un-assigning when running tests.
   */
  @VisibleForTesting
  public static void unAssignInjector() {
    if (injector != null) {
      synchronized (GuiceInjector.class) {
        if (injector != null) {
          injector = null;
        }
      }
    } else {
      LOGGER.error("Injector instance was already un-assigned.");
      throw new IllegalStateException("Injector instance was already un-assigned.");
    }
  }

  /**
   * Retrieves the provided {@link Injector instance} If it is null, throws an {@link
   * IllegalStateException}.
   */
  public static Injector getInjector() {
    if (injector == null) {
      synchronized (GuiceInjector.class) {
        if (injector == null) {
          LOGGER.error("Injector instance is not assignemd. Please assign first.");
          throw new IllegalStateException("Injector instance is not assigned. Please assign first");
        }

      }
    }
    return injector;
  }
}
