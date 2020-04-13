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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements {@link MethodInterceptor} to be used when binding with guice or any other
 * library that enables method interception. It contains all the method interceptor handlers per
 * {@link Mode}.
 */
public class ProfileOrTestMethodInterceptor implements MethodInterceptor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProfileOrTestMethodInterceptor.class);

  private Map<Mode, MethodDataInterceptorHandler> modeMethodDataInterceptorHandlerMap =
      Collections.unmodifiableMap(
          new HashMap<Mode, MethodDataInterceptorHandler>() {
            {
              put(Mode.NONE, new NoneMethodDataInterceptorHandler());
              put(Mode.PROFILE, new ProfileMethodDataInterceptorHandler());
              put(Mode.TEST, new TestMethodDataInterceptorHandler());
              put(Mode.SERIALIZE, new SerializeMethodDataInterceptorHandler());
            }
          });

  /**
   * Calls the {@link Mode} specific handler if available or calls {@link
   * MethodInvocation#proceed()} and returns the object returned by the invocation.
   *
   * @param invocation instance of {@link MethodInvocation} as part of this call chain.
   * @return returns the object returned by the called method.
   * @throws Throwable exception thrown by the called method.
   */
  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    if (modeMethodDataInterceptorHandlerMap.containsKey(ProfileRepository.getMode())) {
      return modeMethodDataInterceptorHandlerMap
          .get(ProfileRepository.getMode())
          .handle(invocation);
    }

    return invocation.proceed();
  }
}
