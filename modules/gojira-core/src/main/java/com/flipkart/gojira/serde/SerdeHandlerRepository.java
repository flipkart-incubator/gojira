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
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Interface that holds all different types of {@link TestSerdeHandler} instances.
 */
public abstract class SerdeHandlerRepository {

  protected TestSerdeHandler defaultSerdeHandler = null;
  protected TestSerdeHandler reqRespDataSerdeHandler = null;
  protected TestSerdeHandler testDataSerdeHandler = null;
  protected ConcurrentHashMap<String, TestSerdeHandler> returnDataSerdeHandler =
      new ConcurrentHashMap<>();
  protected ConcurrentHashMap<String, TestSerdeHandler> exceptionDataSerdeHandler =
      new ConcurrentHashMap<>();
  protected ConcurrentHashMap<String, TestSerdeHandler> methodArgumentDataSerdeHandler =
      new ConcurrentHashMap<>();

  /**
   * This method adds exceptionData serde handler provided in {@link SerdeModule} to {@link
   * #exceptionDataSerdeHandler} Key to the map is methodId + "|" + fqClassName. methodId should be
   * generated as {@link Method#toGenericString()} and fqClassName should be generated as {@link
   * Class#getName()}.
   *
   * @param methodId unique method identifier.
   * @param fqClassName fully qualified class name.
   * @param serdeHandler to add against the specified key.
   */
  abstract void addExceptionDataSerdeHandler(
      String methodId, String fqClassName, TestSerdeHandler serdeHandler);

  /**
   * Returns the default SerdeHandler.
   *
   * @return {@link #defaultSerdeHandler} set in
   * @see SerdeModule
   */
  public abstract TestSerdeHandler getDefaultSerdeHandler();

  /**
   * Sets the default SerdeHandler.
   *
   * @param serdeHandler This method sets the default serde handler provided in
   * @see SerdeModule to {@link #defaultSerdeHandler}
   */
  abstract void setDefaultSerdeHandler(TestSerdeHandler serdeHandler);

  /**
   * Returns the Request/Response Data SerdeHandler.
   *
   * @return {@link #reqRespDataSerdeHandler} set in
   * @see SerdeModule
   */
  public abstract TestSerdeHandler getReqRespDataSerdeHandler();

  /**
   * Gets the Request/Response Data SerdeHandler.
   *
   * @param serdeHandler This sets the default response serde handler provided in
   * @see SerdeModule to {@link #reqRespDataSerdeHandler}
   */
  abstract void setReqRespDataSerdeHandler(TestSerdeHandler serdeHandler);

  /**
   * Gets the TestData SerdeHandler.
   *
   * @return {@link #testDataSerdeHandler} set in
   * @see SerdeModule
   */
  public abstract TestSerdeHandler getTestDataSerdeHandler();

  /**
   * Returns the TestData SerdeHandler.
   *
   * @param serdeHandler This sets the default jsonDefaultTest serde handler provided in
   * @see SerdeModule to {@link #testDataSerdeHandler}
   */
  abstract void setTestDataSerdeHandler(TestSerdeHandler serdeHandler);

  /**
   * This method checks if there is an entry in {@link #returnDataSerdeHandler} Key is
   * methodInvocation.getMethod().toGenericString() If entry exists returns it. Else checks if there
   * exists an {@link SerdeHandler} associated with the method. If it exists, it will create an
   * instance and add it to {@link #returnDataSerdeHandler} else add {@link #defaultSerdeHandler} to
   * {@link #returnDataSerdeHandler} return the entry against the key.
   *
   * @param methodInvocation current methodInvocation instance.
   * @return {@link TestSerdeHandler} instance against the specified key.
   */
  public abstract TestSerdeHandler getOrUpdateAndGetOrDefaultReturnDataSerdeHandler(
      MethodInvocation methodInvocation) throws Throwable;

  /**
   * Returns serdeHandler registered specific to an exception for a given method from
   * {@link #exceptionDataSerdeHandler} Key to the map is methodId + "|" + fqClassName. methodId
   * should be generated as {@link Method#toGenericString()} and fqClassName should be generated as
   * {@link Class#getName()}.
   *
   * @param methodId unique method identifier
   * @param fqClassName fully qualified class name.
   * @return {@link TestSerdeHandler} instance against the specified key.
   */
  public abstract TestSerdeHandler getExceptionDataSerdeHandler(
      String methodId, String fqClassName);

  /**
   * This method checks if there is an entry in {@link #methodArgumentDataSerdeHandler} Key is
   * methodInvocation.getMethod().toGenericString() + "|" + argument If entry exists returns it.
   * Else checks if there exists an {@link SerdeHandler} associated with the method argument. If it
   * exists, it will create an instance and add it to {@link #methodArgumentDataSerdeHandler} else
   * add {@link #defaultSerdeHandler} to {@link #methodArgumentDataSerdeHandler} return the entry
   * against the key. * @param methodInvocation current methodInvocation instance.
   *
   * @param argument specific argument sequence # whose serdeHandler is needed.
   * @return {@link TestSerdeHandler} instance against the specified key.
   */
  public abstract TestSerdeHandler getOrUpdateAndGetOrDefaultMethodArgumentDataSerdeHandler(
      MethodInvocation methodInvocation, int argument) throws Throwable;
}
