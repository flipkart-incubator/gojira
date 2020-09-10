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

import com.flipkart.gojira.core.annotations.ProfileOrTest;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This module is responsible for binding methods annotated with {@link ProfileOrTest} with the
 * respective {@link org.aopalliance.intercept.MethodInterceptor} Current implementation is done for
 * guice interceptor.
 */
public class BindingsModule extends AbstractModule {

  public static final Logger LOGGER = LoggerFactory.getLogger(BindingsModule.class);

  @Override
  protected void configure() {
    if (!ProfileRepository.getGlobalProfileSetting().getMode().equals(Mode.NONE)) {
      ProfileOrTestMethodInterceptor profileOrTestMethodInterceptor =
          new ProfileOrTestMethodInterceptor();
      bindInterceptor(
          Matchers.any(),
          new NoCompilerMethodMatcher().and(Matchers.annotatedWith(ProfileOrTest.class)),
          profileOrTestMethodInterceptor);
    }
  }

  private static class NoCompilerMethodMatcher extends AbstractMatcher<Method> {
    NoCompilerMethodMatcher() {
    }

    @Override
    public boolean matches(Method method) {
      // Bridged methods are implicitly synthetic. Adding the additional check for developer clarity.
      return !method.isSynthetic() && !method.isBridge();
    }
  }
}
