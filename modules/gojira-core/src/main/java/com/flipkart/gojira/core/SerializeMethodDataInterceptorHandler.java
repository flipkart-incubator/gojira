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

import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.execute.TestExecutionException;
import com.flipkart.gojira.models.MethodData;
import com.flipkart.gojira.models.MethodDataType;
import com.flipkart.gojira.models.ProfileData;
import com.flipkart.gojira.serde.SerdeHandlerRepository;
import com.flipkart.gojira.serde.handlers.TestSerdeHandler;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements {@link MethodDataInterceptorHandler} for mode {@link Mode#SERIALIZE}.
 *
 * <p>TODO: Refactor this class.
 */
public class SerializeMethodDataInterceptorHandler implements MethodDataInterceptorHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SerializeMethodDataInterceptorHandler.class);

  private SerdeHandlerRepository serdeHandlerRepository =
      GuiceInjector.getInjector().getInstance(SerdeHandlerRepository.class);

  public SerializeMethodDataInterceptorHandler() {}

  /**
   * Throws a {@link TestExecutionException} if {@link ProfileData#profileState} is not {@link
   * ProfileState#INITIATED}
   *
   * <p>Loops throw every instance of {@link MethodData} and de-serializes them using appropriate
   * {@link TestSerdeHandler} instances from {@link SerdeHandlerRepository}
   *
   * <p>If any de-serialization fails, an error is logged.
   *
   * @param invocation intercepted method invocation
   * @return returns object passed along by the called method to the calling method
   * @throws Throwable throws any exception by the called method or {@link TestExecutionException}
   */
  @Override
  public Object handle(MethodInvocation invocation) throws Throwable {
    if (!ProfileRepository.getProfileState().equals(ProfileState.INITIATED)) {
      throw new TestExecutionException("Serialize test was not initiated.");
    }

    String genericMethodName = null;
    String globalPerRequestId = null;

    globalPerRequestId = ProfileRepository.getGlobalPerRequestID();
    genericMethodName = invocation.getMethod().toGenericString();

    ConcurrentSkipListMap<Long, ConcurrentHashMap<MethodDataType, List<MethodData>>>
        perMethodAllEntries =
            ((ConcurrentSkipListMap<Long, ConcurrentHashMap<MethodDataType, List<MethodData>>>)
                ProfileRepository.getTestData().getMethodDataMap().get(genericMethodName));
    for (Map.Entry<Long, ConcurrentHashMap<MethodDataType, List<MethodData>>> perMethodEntry :
        perMethodAllEntries.entrySet()) {
      Map<MethodDataType, List<MethodData>> methodDataMap = perMethodEntry.getValue();

      if (methodDataMap.containsKey(MethodDataType.ARGUMENT_BEFORE)
          && !methodDataMap.get(MethodDataType.ARGUMENT_BEFORE).isEmpty()) {
        int index = -1;
        for (MethodData methodData : methodDataMap.get(MethodDataType.ARGUMENT_BEFORE)) {
          index++;
          if (methodData.getData() == null) {
            try {
              LOGGER.info("deserialization starting for ");
              serdeHandlerRepository
                  .getOrUpdateAndGetOrDefaultMethodArgumentDataSerdeHandler(invocation, index)
                  .deserializeToInstance(methodData.getData(), invocation.getArguments()[index]);
            } catch (Exception e) {
              LOGGER.error("deserialization failed");
            }
          }
        }
      }

      if (methodDataMap.containsKey(MethodDataType.ARGUMENT_AFTER)
          && !methodDataMap.get(MethodDataType.ARGUMENT_AFTER).isEmpty()) {
        int index = -1;
        for (MethodData methodData : methodDataMap.get(MethodDataType.ARGUMENT_AFTER)) {
          index++;
          if (methodData.getData() != null) {
            try {
              LOGGER.info("deserialization starting for ");
              serdeHandlerRepository
                  .getOrUpdateAndGetOrDefaultMethodArgumentDataSerdeHandler(invocation, index)
                  .deserializeToInstance(methodData.getData(), invocation.getArguments()[index]);
            } catch (Exception e) {
              LOGGER.error("deserialization failed");
            }
          }
        }
      }

      if (methodDataMap.containsKey(MethodDataType.EXCEPTION)
          && !methodDataMap.get(MethodDataType.EXCEPTION).isEmpty()) {
        if (methodDataMap.get(MethodDataType.EXCEPTION).get(0) == null
            || methodDataMap.get(MethodDataType.EXCEPTION).get(0).getClassName() == null
            || methodDataMap.get(MethodDataType.EXCEPTION).get(0).getData() == null) {
          LOGGER.error(
              "exception methodData or methodData.className or methodData.data null,"
                  + " error running test. Method: "
                  + genericMethodName
                  + " globalPerRequestId: "
                  + globalPerRequestId);
        }
        try {
          LOGGER.info(
              "deserialization starting for "
                  + Class.forName(
                      methodDataMap.get(MethodDataType.EXCEPTION).get(0).getClassName()));
          serdeHandlerRepository
              .getExceptionDataSerdeHandler(
                  genericMethodName,
                  methodDataMap.get(MethodDataType.EXCEPTION).get(0).getClassName())
              .deserialize(
                  methodDataMap.get(MethodDataType.EXCEPTION).get(0).getData(),
                  Class.forName(methodDataMap.get(MethodDataType.EXCEPTION).get(0).getClassName()));
        } catch (Exception e) {
          LOGGER.error(
              "deserialization failed for "
                  + Class.forName(
                      methodDataMap.get(MethodDataType.EXCEPTION).get(0).getClassName()));
        }
      }

      if (methodDataMap.containsKey(MethodDataType.RETURN)
          && !methodDataMap.get(MethodDataType.RETURN).isEmpty()) {
        if (methodDataMap.get(MethodDataType.RETURN).get(0) == null
            || (methodDataMap.get(MethodDataType.RETURN).get(0).getData() != null
                && methodDataMap.get(MethodDataType.RETURN).get(0).getClassName() == null)) {
          LOGGER.error(
              "exception methodData or methodData.className or methodData.data null,"
                  + " error running test. Method: "
                  + genericMethodName
                  + " globalPerRequestId: "
                  + globalPerRequestId);
        }
        try {
          LOGGER.info(
              "deserialization starting for "
                  + Class.forName(methodDataMap.get(MethodDataType.RETURN).get(0).getClassName()));
          serdeHandlerRepository
              .getOrUpdateAndGetOrDefaultReturnDataSerdeHandler(invocation)
              .deserialize(
                  methodDataMap.get(MethodDataType.RETURN).get(0).getData(),
                  Class.forName(methodDataMap.get(MethodDataType.RETURN).get(0).getClassName()));
        } catch (Exception e) {
          LOGGER.error(
              "deserialization failed for "
                  + Class.forName(methodDataMap.get(MethodDataType.RETURN).get(0).getClassName()));
        }
      }
    }
    // TODO: Return or throw exception for execution to simulate what happened during profiling.
    return null;
  }
}
