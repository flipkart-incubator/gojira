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

package com.flipkart.gojira.serde;

import com.flipkart.gojira.serde.annotations.SerdeHandler;
import com.flipkart.gojira.serde.handlers.TestSerdeHandler;
import java.lang.annotation.Annotation;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Implementaion of {@link SerdeHandlerRepository}.
 */
public class SerdeHandlerRepositoryImpl extends SerdeHandlerRepository {

  /**
   * This method fetches the {@link SerdeHandler} annotation for a give method argument.
   *
   * @param invocation method invocation instance
   * @param position position of the method argument
   * @return {@link SerdeHandler} annotation for a give method argument.
   */
  private static SerdeHandler annotatedSerdeHandler(MethodInvocation invocation, int position) {
    Annotation[] annotations = invocation.getMethod().getParameterAnnotations()[position];
    for (Annotation annotation : annotations) {
      if (annotation instanceof SerdeHandler) {
        return (SerdeHandler) annotation;
      }
    }
    return null;
  }

  /**
   * This method fetches the {@link SerdeHandler} annotation for method return data.
   *
   * @param invocation method invocation instance
   * @return {@link SerdeHandler} annotation for a give method return data.
   */
  private static SerdeHandler annotatedReturnSerdeHandler(MethodInvocation invocation) {
    Annotation[] annotations = invocation.getMethod().getDeclaredAnnotations();
    for (Annotation annotation : annotations) {
      if (annotation instanceof SerdeHandler) {
        return (SerdeHandler) annotation;
      }
    }
    return null;
  }

  @Override
  void addExceptionDataSerdeHandler(
      String methodId, String fqClassName, TestSerdeHandler serdeHandler) {}

  @Override
  public TestSerdeHandler getDefaultSerdeHandler() {
    return defaultSerdeHandler;
  }

  @Override
  void setDefaultSerdeHandler(TestSerdeHandler serdeHandler) {
    defaultSerdeHandler = serdeHandler;
  }

  @Override
  public TestSerdeHandler getReqRespDataSerdeHandler() {
    return reqRespDataSerdeHandler;
  }

  @Override
  void setReqRespDataSerdeHandler(TestSerdeHandler serdeHandler) {
    reqRespDataSerdeHandler = serdeHandler;
  }

  @Override
  public TestSerdeHandler getTestDataSerdeHandler() {
    return testDataSerdeHandler;
  }

  @Override
  void setTestDataSerdeHandler(TestSerdeHandler testSerdeHandler) {
    testDataSerdeHandler = testSerdeHandler;
  }

  @Override
  public TestSerdeHandler getOrUpdateAndGetOrDefaultReturnDataSerdeHandler(
      MethodInvocation methodInvocation) throws Throwable {
    String mapEntryVar = methodInvocation.getMethod().toGenericString();
    if (!returnDataSerdeHandler.containsKey(mapEntryVar)) {
      SerdeHandler annotatedSerdeHandler = annotatedReturnSerdeHandler(methodInvocation);
      if (annotatedSerdeHandler != null) {
        {
          Class<? extends TestSerdeHandler> serdeHandler =
              annotatedSerdeHandler.serdeHandlerClass();
          TestSerdeHandler testSerdeHandler = serdeHandler.newInstance();
          returnDataSerdeHandler.put(mapEntryVar, testSerdeHandler);
        }
      } else {
        returnDataSerdeHandler.put(mapEntryVar, getDefaultSerdeHandler());
      }
    }
    return returnDataSerdeHandler.get(mapEntryVar);
  }

  @Override
  public TestSerdeHandler getExceptionDataSerdeHandler(String methodId, String fqClassName) {
    return exceptionDataSerdeHandler.getOrDefault(
        methodId + "|" + fqClassName, getDefaultSerdeHandler());
  }

  @Override
  public TestSerdeHandler getOrUpdateAndGetOrDefaultMethodArgumentDataSerdeHandler(
      MethodInvocation methodInvocation, int argument) throws Throwable {
    String mapEntryVar = methodInvocation.getMethod().toGenericString() + "|" + argument;
    if (!methodArgumentDataSerdeHandler.containsKey(mapEntryVar)) {
      SerdeHandler annotatedSerdeHandler = annotatedSerdeHandler(methodInvocation, argument);
      if (annotatedSerdeHandler != null) {
        {
          Class<? extends TestSerdeHandler> serdeHandler =
              annotatedSerdeHandler.serdeHandlerClass();
          TestSerdeHandler testSerdeHandler = serdeHandler.newInstance();
          methodArgumentDataSerdeHandler.put(mapEntryVar, testSerdeHandler);
        }
      } else {
        methodArgumentDataSerdeHandler.put(mapEntryVar, getDefaultSerdeHandler());
      }
    }
    return methodArgumentDataSerdeHandler.get(mapEntryVar);
  }
}
