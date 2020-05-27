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

import com.flipkart.gojira.hash.HashHandlerUtil;
import com.flipkart.gojira.hash.TestHashHandler;
import com.flipkart.gojira.models.MethodData;
import com.flipkart.gojira.models.MethodDataType;
import com.flipkart.gojira.models.ProfileData;
import com.flipkart.gojira.serde.SerdeHandlerRepository;
import com.flipkart.gojira.serde.handlers.TestSerdeHandler;
import com.google.inject.Inject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for {@link MethodDataInterceptorHandler} for mode {@link Mode#PROFILE}.
 *
 * <p>TODO: Refactor this class.
 */
public class ProfileMethodDataInterceptorHandler implements MethodDataInterceptorHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProfileOrTestMethodInterceptor.class);

  private SerdeHandlerRepository serdeHandlerRepository;

  @Inject
  public ProfileMethodDataInterceptorHandler(SerdeHandlerRepository serdeHandlerRepository) {
    this.serdeHandlerRepository = serdeHandlerRepository;
  }

  /**
   * Checks {@link ProfileData#profileState}, if not {@link ProfileState#INITIATED} calls {@link
   * MethodInvocation#proceed()} and returns the object returned by the invocation
   *
   * <p>On error checking for {@link ProfileData#profileState} or when performing any of the below
   * operations, marks {@link ProfileData#profileState} as {@link ProfileState#FAILED} and calls
   * {@link MethodInvocation#proceed()} if not already done and returns the object returned by the
   * invocation.
   *
   * <p>Gets {@link ProfileRepository#getGlobalPerRequestID()}.
   *
   * <p>Gets {@link Method#toGenericString()} of ()} of {@link MethodInvocation#getMethod()}.
   *
   * <p>Instantiates a map of {@link ConcurrentHashMap} with key as {@link MethodDataType} and value
   * as {@link ArrayList} of {@link MethodData}.
   *
   * <p>Iterates through all method arguments. Hashes the data if required for security purposes.
   * This is done by calling {@link TestHashHandler#hash(byte[])} method on {@link
   * HashHandlerUtil#getHashHandler(MethodInvocation, int)} instance. Data is serialized by calling
   * {@link TestSerdeHandler#serialize(Object)} method on {@link SerdeHandlerRepository
   * #getOrUpdateAndGetOrDefaultMethodArgumentDataSerdeHandler(MethodInvocation,int)} instance.
   *
   * <p>After recording arguments before invocation, calls {@link MethodInvocation#proceed()} and
   * stores the object returned if no exception or the exception object otherwise.
   *
   * <p>Again, iterates through all method arguments. Hashes the data if required for security
   * purposes. This is done by calling {@link TestHashHandler#hash(byte[])} method on {@link
   * HashHandlerUtil#getHashHandler(MethodInvocation, int)} instance. Data is serialized by calling
   * {@link TestSerdeHandler#serialize(Object)} method on {@link SerdeHandlerRepository
   * #getOrUpdateAndGetOrDefaultMethodArgumentDataSerdeHandler(MethodInvocation,int)} instance. This
   * is to record method arguments after method execution, in case they are modified inside the
   * method.
   *
   * <p>After that return object and exception data are serialized in a similar manner.
   *
   * <p>Then if an exception was thrown by the method being called, the same is thrown, else the
   * object is retuned.
   *
   * @param invocation intercepted method invocation
   * @return object passed along by the called method to the calling method
   * @throws Throwable for any exception by the called method
   */
  @Override
  public Object handle(MethodInvocation invocation) throws Throwable {
    try {
      if (!ProfileRepository.getProfileState().equals(ProfileState.INITIATED)) {
        return invocation.proceed();
      }
    } catch (Exception e) {
      LOGGER.error("Error getting profile state in ProfileMethodDataInterceptorHandler. ", e);
      ProfileRepository.setProfileState(ProfileState.FAILED);
      return invocation.proceed();
    }
    String methodGenericString = null;
    String globalPerRequestId = null;

    try {
      globalPerRequestId = ProfileRepository.getGlobalPerRequestID();
    } catch (Exception e) {
      LOGGER.error("error getting globalPerRequestId.", e);
      ProfileRepository.setProfileState(ProfileState.FAILED);
      return invocation.proceed();
    }

    try {
      methodGenericString = invocation.getMethod().toGenericString();
    } catch (Exception e) {
      LOGGER.error(
          "error getting methodGenericString." + " globalPerRequestId: " + globalPerRequestId, e);
      ProfileRepository.setProfileState(ProfileState.FAILED);
      return invocation.proceed();
    }

    Object invocationReturnData = null;
    Exception invocationException = null;

    // profile argument data for comparison before method invocation first
    ConcurrentHashMap<MethodDataType, List<MethodData>> methodDataMap = new ConcurrentHashMap<>();
    {
      int index = -1;
      try {
        List<MethodData> argumentBeforeList = new ArrayList<>();
        for (Object arg : invocation.getArguments()) {
          index++;
          TestHashHandler hashHandler = HashHandlerUtil.getHashHandler(invocation, index);
          MethodData methodData =
              new MethodData(
                  MethodDataType.ARGUMENT_BEFORE,
                  arg == null ? null : arg.getClass().getName(),
                  arg == null
                      ? null
                      : hashHandler == null
                          ? serdeHandlerRepository
                              .getOrUpdateAndGetOrDefaultMethodArgumentDataSerdeHandler(
                                  invocation, index)
                              .serialize(arg)
                          : hashHandler.hash(
                              serdeHandlerRepository
                                  .getOrUpdateAndGetOrDefaultMethodArgumentDataSerdeHandler(
                                      invocation, index)
                                  .serialize(arg)),
                  index);
          argumentBeforeList.add(methodData);
        }
        methodDataMap.put(MethodDataType.ARGUMENT_BEFORE, argumentBeforeList);
      } catch (Exception e) {
        // on failure, mark failed and proceed with method invocation
        LOGGER.warn(
            "error profiling argument data before method execution, method: "
                + methodGenericString
                + " argument index: "
                + index
                + " globalPerRequestId: "
                + globalPerRequestId,
            e);
        ProfileRepository.setProfileState(ProfileState.FAILED);
        return invocation.proceed();
      }
    }

    // proceed with method invocation and record
    try {
      invocationReturnData = invocation.proceed();
    } catch (Exception e) {
      invocationException = e;
    }

    // return, exception and replace with data
    try {
      // replace with data
      int index = -1;
      try {
        List<MethodData> argumentAfterList = new ArrayList<>();
        for (Object arg : invocation.getArguments()) {
          index++;
          TestHashHandler hashHandler = HashHandlerUtil.getHashHandler(invocation, index);
          MethodData methodData =
              new MethodData(
                  MethodDataType.ARGUMENT_AFTER,
                  arg == null ? null : arg.getClass().getName(),
                  arg == null
                      ? null
                      : hashHandler == null
                          ? serdeHandlerRepository
                              .getOrUpdateAndGetOrDefaultMethodArgumentDataSerdeHandler(
                                  invocation, index)
                              .serialize(arg)
                          : hashHandler.hash(
                              serdeHandlerRepository
                                  .getOrUpdateAndGetOrDefaultMethodArgumentDataSerdeHandler(
                                      invocation, index)
                                  .serialize(arg)),
                  index);
          argumentAfterList.add(methodData);
        }
        methodDataMap.put(MethodDataType.ARGUMENT_AFTER, argumentAfterList);
      } catch (Exception e) {
        LOGGER.warn(
            "error profiling argument data after method execution, method: "
                + methodGenericString
                + " argument index: "
                + index
                + " globalPerRequestId: "
                + globalPerRequestId,
            e);
        ProfileRepository.setProfileState(ProfileState.FAILED);
      }

      {
        List<MethodData> returnDataList = new ArrayList<>();
        if (invocationException == null) {
          try {
            // TODO: Take care of hashing for return object.
            returnDataList.add(
                new MethodData(
                    MethodDataType.RETURN,
                    invocationReturnData == null ? null : invocationReturnData.getClass().getName(),
                    invocationReturnData == null
                        ? null
                        : serdeHandlerRepository
                            .getOrUpdateAndGetOrDefaultReturnDataSerdeHandler(invocation)
                            .serialize(invocationReturnData),
                    0));
            methodDataMap.put(MethodDataType.RETURN, returnDataList);
          } catch (Exception e) {
            LOGGER.warn(
                "error profiling invocation return data after method execution, method: "
                    + methodGenericString
                    + " globalPerRequestId: "
                    + globalPerRequestId,
                e);
            ProfileRepository.setProfileState(ProfileState.FAILED);
          }
        } else {
          try {
            returnDataList.add(
                new MethodData(
                    MethodDataType.EXCEPTION,
                    invocationException.getClass().getName(),
                    serdeHandlerRepository
                        .getExceptionDataSerdeHandler(
                            methodGenericString, invocationException.getClass().getName())
                        .serialize(invocationException),
                    0));
            methodDataMap.put(MethodDataType.EXCEPTION, returnDataList);
          } catch (Exception e) {
            LOGGER.warn(
                "error profiling invocation exception data after method execution, method: "
                    + methodGenericString
                    + " globalPerRequestId: "
                    + globalPerRequestId,
                e);
            ProfileRepository.setProfileState(ProfileState.FAILED);
          }
        }
      }

      try {
        ProfileRepository.addInterceptedData(methodGenericString, methodDataMap);
      } catch (Exception e) {
        LOGGER.error(
            "error adding intercepted data against method : "
                + methodGenericString
                + " globalPerRequestId: "
                + globalPerRequestId);
        ProfileRepository.setProfileState(ProfileState.FAILED);
      }
    } catch (Exception e) {
      LOGGER.warn(
          "error profiling data, method: "
              + methodGenericString
              + " globalPerRequestId: "
              + globalPerRequestId,
          e);
      ProfileRepository.setProfileState(ProfileState.FAILED);
    }

    if (invocationException != null) {
      throw invocationException;
    }
    return invocationReturnData;
  }
}
