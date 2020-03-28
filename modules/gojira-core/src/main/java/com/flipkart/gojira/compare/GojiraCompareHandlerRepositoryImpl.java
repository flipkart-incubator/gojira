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

import com.flipkart.compare.handlers.TestCompareHandler;
import com.flipkart.gojira.compare.annotations.CompareHandler;
import java.lang.annotation.Annotation;
import org.aopalliance.intercept.MethodInvocation;

/**
 * This class extends {@link GojiraCompareHandlerRepository}
 */
public class GojiraCompareHandlerRepositoryImpl extends GojiraCompareHandlerRepository {

  /**
   * Given a
   *
   * @param invocation {@link MethodInvocation} instance and
   * @param position   the position of the argument
   * @return the {@link CompareHandler} annotation associated with the argument
   */
  private static CompareHandler annotatedCompareHandler(MethodInvocation invocation, int position) {
    Annotation[] annotations = invocation.getMethod().getParameterAnnotations()[position];
    for (Annotation annotation : annotations) {
      if (annotation instanceof CompareHandler) {
        return (CompareHandler) annotation;
      }
    }
    return null;
  }

  @Override
  void setResponseDataCompareHandler(TestCompareHandler compareHandler) {
    responseDataCompareHandler = compareHandler;
  }

  @Override
  public TestCompareHandler getDefaultCompareHandler() {
    return defaultCompareHandler;
  }

  @Override
  public void setDefaultCompareHandler(TestCompareHandler compareHandler) {
    defaultCompareHandler = compareHandler;
  }

  @Override
  public TestCompareHandler getResponseDataCompareHandler() {
    return responseDataCompareHandler;
  }

  @Override
  public TestCompareHandler getOrUpdateAndGetOrDefaultMethodArgumentDataCompareHandler(
      MethodInvocation methodInvocation, int argument) throws Throwable {
    String mapEntryVar = methodInvocation.getMethod().toGenericString() + "|" + argument;
    if (!methodArgumentDataCompareHandler.containsKey(mapEntryVar)) {
      CompareHandler annotatedCompareHandler = annotatedCompareHandler(methodInvocation, argument);
      if (annotatedCompareHandler != null) {
        {
          Class<? extends TestCompareHandler> compareHandler = annotatedCompareHandler
              .compareHandlerClass();
          TestCompareHandler testCompareHandler = compareHandler.newInstance();
          methodArgumentDataCompareHandler.put(mapEntryVar, testCompareHandler);
        }
      } else {
        methodArgumentDataCompareHandler.put(mapEntryVar, getDefaultCompareHandler());
      }
    }
    return methodArgumentDataCompareHandler.get(mapEntryVar);
  }
}
