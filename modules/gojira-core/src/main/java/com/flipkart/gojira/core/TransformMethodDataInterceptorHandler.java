package com.flipkart.gojira.core;

import com.flipkart.compare.TestCompareException;
import com.flipkart.compare.handlers.TestCompareHandler;
import com.flipkart.gojira.compare.GojiraCompareHandlerRepository;
import com.flipkart.gojira.core.annotations.ProfileOrTest;
import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.execute.TestExecutionException;
import com.flipkart.gojira.execute.TranssformExecutionException;
import com.flipkart.gojira.hash.HashHandlerUtil;
import com.flipkart.gojira.hash.TestHashHandler;
import com.flipkart.gojira.models.ExecutionData;
import com.flipkart.gojira.models.MethodData;
import com.flipkart.gojira.models.MethodDataType;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.serde.SerdeHandlerRepository;
import com.flipkart.gojira.serde.handlers.TestSerdeHandler;
import com.google.common.base.Strings;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformMethodDataInterceptorHandler implements MethodDataInterceptorHandler {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(TransformMethodDataInterceptorHandler.class);

  private GojiraCompareHandlerRepository gojiraCompareHandlerRepository =
      GuiceInjector.getInjector().getInstance(GojiraCompareHandlerRepository.class);

  private SerdeHandlerRepository serdeHandlerRepository =
      GuiceInjector.getInjector().getInstance(SerdeHandlerRepository.class);

  public TransformMethodDataInterceptorHandler() {}

  /**
   * Gets {@link ExecutionData#profileState}, throws exception if not {@link ProfileState#INITIATED}
   *
   * <p>Gets the {@link ProfileRepository#getGlobalPerRequestID()}, {@link Method#toGenericString()}
   * of {@link MethodInvocation#getMethod()} to get data corresponding to this specific method. On
   * error when getting the data or if data is null, {@link TestExecutionException} is thrown.
   *
   * <p>Gets the {@link Map#get(Object)} }} of {@link TestData#getMethodDataMap()}, of {@link
   * ProfileRepository#getTestData()} ()}
   * to get data corresponding to this specific method.
   * if data is null, {@link TestExecutionException} is thrown.
   *
   * <p>During {@link Mode#PROFILE} mode, it is possible that multiple invocations of the same
   * method are called. It is also possible that they are called by different threads. So, to get
   * the data corresponding to particular method's invocation, we compare the method arguments
   * first. For this we iterate over all the entries stored against the method. For each method
   * entry, we do some validations. If either entry is null or empty or {@link
   * MethodDataType#RETURN} and {@link MethodDataType#EXCEPTION} are empty, {@link
   * TestExecutionException} is thrown. Then for each entry in {@link
   * MethodDataType#ARGUMENT_BEFORE}, we compare current data with what is stored during {@link
   * Mode#PROFILE} mode. This is done by calling {@link TestCompareHandler#compare(byte[], byte[])}
   * using {@link
   * GojiraCompareHandlerRepository#getOrUpdateAndGetOrDefaultMethodArgumentDataCompareHandler}
   * instance with arguments as the recorded data which is already in bytes and then getting byte[]
   * of current execution's method data by hashing if required by calling {@link
   * TestHashHandler#hash(byte[])} using {@link HashHandlerUtil#getHashHandler(MethodInvocation,
   * int)} instance and then serializing by calling {@link TestSerdeHandler#serialize(Object)} using
   * {@link SerdeHandlerRepository
   * #getOrUpdateAndGetOrDefaultMethodArgumentDataSerdeHandler(MethodInvocation, int)} instance. If
   * de-serialization fails, an {@link TestExecutionException} is thrown. If comparison fails, we
   * try matching with the next entry in {@link ConcurrentSkipListMap}. If none of them match, we
   * throw {@link TestCompareException}. If there is a match, then we attempt to remove it from
   * {@link ConcurrentSkipListMap} in a thread-safe manner. If we are able to remove, we proceed.
   * Else we try matching with another entry, this could happen in a rare case, where all
   * attributes, for all arguments, being compared, the way they are compared, results in multiple
   * matches.
   *
   * <p>Once we have a found a matching entry for the method, we then deserialize to instance the
   * data recorded in {@link MethodDataType#ARGUMENT_AFTER} by calling {@link
   * TestSerdeHandler#deserializeToInstance(byte[], Object)} using {@link SerdeHandlerRepository
   * #getOrUpdateAndGetOrDefaultMethodArgumentDataSerdeHandler(MethodInvocation, int)} instance. If
   * deserialization to instance fails, we throw {@link TestExecutionException}. Here deserialize to
   * instance means that we have to update method arguments instead of creating a new instance since
   * that is how the behavior would have been in {@link Mode#PROFILE} mode.
   *
   * <p>Then we deserialize exception data if it is present by calling {@link
   * TestSerdeHandler#deserialize(byte[], Class)} using {@link
   * SerdeHandlerRepository#getExceptionDataSerdeHandler(String, String)} instance and throw the
   * exception object. If deserialization fails, we throw {@link TestExecutionException}.
   *
   * <p>If there is no exception data, and {@link MethodInvocation has transformed method specefied
   * same is invoked with {@link MethodDataType#RETURN}, {@link TestData#getTag()},
   * {@link Object as varArgs}  to get transformed data. Same is used to reprofile Method Return
   * and exceptions and returned.
   *
   * @param invocation intercepted method invocation
   * @return object passed along by the called method to the calling method
   * @throws Throwable for any exception by the called method or {@link TestExecutionException} or
   *
   */
  @Override
  public Object handle(MethodInvocation invocation) throws Throwable {
    if (!ProfileRepository.getProfileState().equals(ProfileState.INITIATED)) {
      throw new TestExecutionException("Transform was not initiated.");
    }

    String genericMethodName = null;
    String globalPerRequestId = null;

    try {
      globalPerRequestId = ProfileRepository.getGlobalPerRequestID();
    } catch (Exception e) {
      LOGGER.error("error getting globalPerRequestId.", e);
      throw new TestExecutionException("error getting globalPerRequestId.", e);
    }

    try {
      genericMethodName = invocation.getMethod().toGenericString();
    } catch (Exception e) {
      LOGGER.error(
          "error getting methodGenericString." + " globalPerRequestId: " + globalPerRequestId, e);
      throw new TestExecutionException(
          "error getting methodGenericString." + " globalPerRequestId: " + globalPerRequestId, e);
    }

    if (ProfileRepository.getTestData() == null) {
      LOGGER.error(
          "test data null. method: "
              + genericMethodName
              + " globalPerRequestId: "
              + globalPerRequestId);
      throw new TestExecutionException(
          "test data null. method: "
              + genericMethodName
              + " globalPerRequestId: "
              + globalPerRequestId);
    }

    if (ProfileRepository.getTestData().getMethodDataMap() == null) {
      LOGGER.error(
          "test data method data map null. method: "
              + genericMethodName
              + " globalPerRequestId: "
              + globalPerRequestId);
      throw new TestExecutionException(
          "test data method data map null. method: "
              + genericMethodName
              + " globalPerRequestId: "
              + globalPerRequestId);
    }

    if (ProfileRepository.getTestData().getMethodDataMap().get(genericMethodName) == null) {
      LOGGER.error(
          "test data method data map for method null. method: "
              + genericMethodName
              + " globalPerRequestId: "
              + globalPerRequestId);
      throw new TestExecutionException(
          "test data method data map  for method null. method: "
              + genericMethodName
              + " globalPerRequestId: "
              + globalPerRequestId);
    }

    ConcurrentSkipListMap<Long, ConcurrentHashMap<MethodDataType, List<MethodData>>>
        perMethodAllEntries =
            ((ConcurrentSkipListMap<Long, ConcurrentHashMap<MethodDataType, List<MethodData>>>)
                ProfileRepository.getTestData().getMethodDataMap().get(genericMethodName));
    String tag = ProfileRepository.getTestData().getTag();

    ConcurrentHashMap<MethodDataType, List<MethodData>> methodDataMap = null;

    for (Map.Entry<Long, ConcurrentHashMap<MethodDataType, List<MethodData>>> perMethodEntry :
        perMethodAllEntries.entrySet()) {
      // validation for every entry in the queue
      Long perMethodEntryKey = perMethodEntry.getKey();
      ConcurrentHashMap<MethodDataType, List<MethodData>> perMethodEntryValue =
          perMethodEntry.getValue();
      if (perMethodEntryValue == null || perMethodEntryValue.isEmpty()) {
        LOGGER.error(
            "methodDataMap null or empty, error running transform. "
                + "method: "
                + genericMethodName
                + " globalPerRequestId: "
                + globalPerRequestId);
        throw new TestExecutionException(
            "methodDataMap null or empty, error running transform. "
                + "method: "
                + genericMethodName
                + " globalPerRequestId: "
                + globalPerRequestId);
      }

      if (!(perMethodEntryValue.containsKey(MethodDataType.EXCEPTION)
              && !perMethodEntryValue.get(MethodDataType.EXCEPTION).isEmpty())
          && !(perMethodEntryValue.containsKey(MethodDataType.RETURN)
              && !perMethodEntryValue.get(MethodDataType.RETURN).isEmpty())) {
        LOGGER.error(
            "both return and exception data not present in methodDataMap, error running transform. "
                + "method: "
                + genericMethodName
                + " globalPerRequestId: "
                + globalPerRequestId);
        throw new TestExecutionException(
            "both return and exception data not present in methodDataMap, error running transform. "
                + "method: "
                + genericMethodName
                + " globalPerRequestId: "
                + globalPerRequestId);
      }
    }

    if (methodDataMap == null) {
      LOGGER.error(
          "unable to find any matching argument(s) from "
              + "the queue of arguments against method signature: "
              + genericMethodName
              + ". this could mean not ignored diffs in the comparison "
              + "that is done for arguments before method execution. "
              + "method: "
              + genericMethodName
              + " globalPerRequestId: "
              + globalPerRequestId);
      throw new TestCompareException(
          "unable to find any matching argument(s) from "
              + "the queue of arguments against method signature: "
              + genericMethodName
              + ". this could mean not ignored diffs in the comparison "
              + "that is done for arguments before method execution. "
              + "method: "
              + genericMethodName
              + " globalPerRequestId: "
              + globalPerRequestId);
    }

    // de-serialize to instance
    if (methodDataMap.containsKey(MethodDataType.ARGUMENT_AFTER)
        && !methodDataMap.get(MethodDataType.ARGUMENT_AFTER).isEmpty()) {
      int index = -1;
      for (MethodData methodData : methodDataMap.get(MethodDataType.ARGUMENT_AFTER)) {
        if (methodData == null
            || (methodData.getData() != null && methodData.getClassName() == null)) {
          LOGGER.error(
              "argument "
                  + index
                  + " methodData or methodData.getData not null and methodData.className null, "
                  + "error running test."
                  + "method: "
                  + genericMethodName
                  + " globalPerRequestId: "
                  + globalPerRequestId);
          throw new TestExecutionException(
              "argument "
                  + index
                  + " methodData or methodData.getData not null and methodData.className null, "
                  + "error running test."
                  + "method: "
                  + genericMethodName
                  + " globalPerRequestId: "
                  + globalPerRequestId);
        }
        index++;
        if (methodData.getData() == null) {
          invocation.getArguments()[index] = null;
        } else {
          serdeHandlerRepository
              .getOrUpdateAndGetOrDefaultMethodArgumentDataSerdeHandler(
                  invocation, methodData.getPosition())
              .deserializeToInstance(methodData.getData(), invocation.getArguments()[index]);
          // TODO: Throw TestExecutionException if deserialization fails.
        }
      }
    }

    // throw exception or return transformed data
    if (methodDataMap.containsKey(MethodDataType.EXCEPTION)
        && !methodDataMap.get(MethodDataType.EXCEPTION).isEmpty()) {
      if (methodDataMap.get(MethodDataType.EXCEPTION).get(0) == null
          || methodDataMap.get(MethodDataType.EXCEPTION).get(0).getClassName() == null
          || methodDataMap.get(MethodDataType.EXCEPTION).get(0).getData() == null) {
        LOGGER.error(
            "exception methodData or methodData.className or methodData.data null, "
                + "error running test."
                + "method: "
                + genericMethodName
                + " globalPerRequestId: "
                + globalPerRequestId);
        throw new TestExecutionException(
            "exception methodData or methodData.className or methodData.data null, "
                + "error running test."
                + "method: "
                + genericMethodName
                + " globalPerRequestId: "
                + globalPerRequestId);
      }
      LOGGER.info("Throwing exception that was captured while profiling.");
      throw (Throwable)
          serdeHandlerRepository
              .getExceptionDataSerdeHandler(
                  genericMethodName,
                  methodDataMap.get(MethodDataType.EXCEPTION).get(0).getClassName())
              .deserialize(
                  methodDataMap.get(MethodDataType.EXCEPTION).get(0).getData(),
                  Class.forName(methodDataMap.get(MethodDataType.EXCEPTION).get(0).getClassName()));
    }

    if (methodDataMap.containsKey(MethodDataType.RETURN)
        && !methodDataMap.get(MethodDataType.RETURN).isEmpty()) {
      if (methodDataMap.get(MethodDataType.RETURN).get(0) == null
          || (methodDataMap.get(MethodDataType.RETURN).get(0).getData() != null
              && methodDataMap.get(MethodDataType.RETURN).get(0).getClassName() == null)) {
        LOGGER.error(
            "return methodData or methodData.getData not null and methodData.className null, "
                + "error running test."
                + "method: "
                + genericMethodName
                + " globalPerRequestId: "
                + globalPerRequestId);
        throw new TestExecutionException(
            "return methodData or methodData.getData not null and methodData.className null, "
                + "error running test."
                + "method: "
                + genericMethodName
                + " globalPerRequestId: "
                + globalPerRequestId);
      }
      final Object originalObject =
          methodDataMap.get(MethodDataType.RETURN).get(0).getData() == null
              ? null
              : serdeHandlerRepository
                  .getOrUpdateAndGetOrDefaultReturnDataSerdeHandler(invocation)
                  .deserialize(
                      methodDataMap.get(MethodDataType.RETURN).get(0).getData(),
                      Class.forName(
                          methodDataMap.get(MethodDataType.RETURN).get(0).getClassName()));
      List<MethodData> returnDataList = new ArrayList<>();
      Object transformedObject = null;
      Exception invocationException = null;

      final ProfileOrTest annotation = invocation.getMethod().getAnnotation(ProfileOrTest.class);
      Method transformedMethod = null;
      if (!Strings.isNullOrEmpty(annotation.transformMethod())) {
        try {
          transformedMethod =
              invocation
                  .getMethod()
                  .getDeclaringClass()
                  .getDeclaredMethod(
                      annotation.transformMethod(),
                      invocation.getMethod().getReturnType(),
                      String.class,
                      invocation.getArguments().getClass());
        } catch (Exception e) {
          throw new TranssformExecutionException(
              "not able to find transformed method,"
                  + "error running transform."
                  + "method: "
                  + genericMethodName
                  + " globalPerRequestId: "
                  + globalPerRequestId);
        }
        try {
          transformedObject =
              transformedMethod.invoke(
                  invocation.getMethod().getDeclaringClass().newInstance(),
                  originalObject,
                  tag,
                  invocation.getArguments());

        } catch (Exception e) {
          invocationException = e;
        }
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
              "error transforming argument data before method execution, method: "
                  + genericMethodName
                  + " argument index: "
                  + index
                  + " globalPerRequestId: "
                  + globalPerRequestId,
              e);
          ProfileRepository.setProfileState(ProfileState.FAILED);
        }
        if (invocationException == null) {
          returnDataList.add(
              new MethodData(
                  MethodDataType.RETURN,
                  transformedObject == null ? null : transformedObject.getClass().getName(),
                  transformedObject == null
                      ? null
                      : serdeHandlerRepository
                          .getOrUpdateAndGetOrDefaultReturnDataSerdeHandler(invocation)
                          .serialize(transformedObject),
                  0));
          methodDataMap.put(MethodDataType.RETURN, returnDataList);

        } else {
          returnDataList.add(
              new MethodData(
                  MethodDataType.EXCEPTION,
                  invocationException.getClass().getName(),
                  serdeHandlerRepository
                      .getExceptionDataSerdeHandler(
                          genericMethodName, invocationException.getClass().getName())
                      .serialize(invocationException),
                  0));
          methodDataMap.put(MethodDataType.EXCEPTION, returnDataList);
          methodDataMap.remove(MethodDataType.RETURN);
        }
        try {
          ProfileRepository.addInterceptedData(genericMethodName, methodDataMap);
        } catch (Exception e) {
          LOGGER.error(
              "error adding intercepted data against method : "
                  + genericMethodName
                  + " globalPerRequestId: "
                  + globalPerRequestId);
          ProfileRepository.setProfileState(ProfileState.FAILED);
        }
        if (invocationException != null) {
          throw invocationException;
        }
        return transformedObject;
      }
      return originalObject;
    }
    LOGGER.info(
        "returning null from TestMethodDataInterceptorHandler. "
            + "No return data or exception was profiled.");
    // TODO: throw an exception here. check what happens if return is void.
    return null;
  }
}
