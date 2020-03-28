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

import com.flipkart.gojira.models.MethodData;
import com.flipkart.gojira.models.MethodDataType;
import com.flipkart.gojira.models.ProfileData;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.TestDataType;
import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.models.TestResponseData;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class currently acts as coordinator of execution in various {@link Mode}. It also holds a
 * reference to the thread local variable which specific request-response scope data and state.
 * <p>
 * TODO: Refactor this class. Split responsibility for temp data storage during execution,
 * co-ordination etc...
 */
public class ProfileRepository<InputData extends TestRequestData<T>, OutputData extends TestResponseData<T>, T extends TestDataType> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProfileRepository.class);
  private static final String defaultGlobalPerRequestID = "GLOBAL_PER_REQUEST_ID";
  private final static InheritableThreadLocal<String> GLOBAL_PER_REQUEST_ID = new InheritableThreadLocal<String>() {
    /**
     * @see ThreadLocal#initialValue()
     */
    @Override
    protected String initialValue() {
      return defaultGlobalPerRequestID;
    }
  };
  private static ProfileSetting globalProfileSetting = new ProfileSetting();
  //TODO: Wrap below map in a size restricted collection for memory protection
  private static ConcurrentHashMap<String, ProfileData> globalProfiledDataMap = new ConcurrentHashMap<>();

  public static String getGlobalPerRequestID() {
    return GLOBAL_PER_REQUEST_ID.get();
  }

  public static void setGlobalPerRequestID(String globalPerRequestID) {
    if (globalPerRequestID != null) {
      GLOBAL_PER_REQUEST_ID.set(globalPerRequestID);
    } else {
      LOGGER.error("globalPerRequestId cannot be null.");
    }
  }

  public static void clearGlobalPerRequestID() {
    GLOBAL_PER_REQUEST_ID.remove();
  }

  public static void setTestDataId(String testDataId) {
    if (globalProfiledDataMap.containsKey(GLOBAL_PER_REQUEST_ID.get())) {
      globalProfiledDataMap.get(GLOBAL_PER_REQUEST_ID.get()).getTestData().setId(testDataId);
    } else {
      LOGGER.error(
          "Trying to set test data id against global request id: " + GLOBAL_PER_REQUEST_ID.get()
              + " which is either null or the default value.");
    }
  }

  static synchronized Mode getMode() {
    return globalProfileSetting.getMode();
  }

  static synchronized void setMode(Mode mode) {
    globalProfileSetting.setMode(mode);
  }

  static ProfileSetting getGlobalProfileSetting() {
    return globalProfileSetting;
  }

  static void begin(String globalPerRequestId) {
    if (globalPerRequestId != null) {
      GLOBAL_PER_REQUEST_ID.set(globalPerRequestId);
      if (globalProfiledDataMap.putIfAbsent(globalPerRequestId, new ProfileData()) != null) {
        LOGGER.error(
            "Error beginning profiling/testing since current global_per_request_id is already present in the map.");
        throw new RuntimeException(
            "Error beginning profiling/testing since current global_per_request_id is already present in the map.");
      }
      ProfileRepository.setProfileState(ProfileState.INITIATED);
    } else {
      LOGGER.error("globalPerRequestId cannot be null.");
    }
  }

  static void end() {
    if (globalProfiledDataMap.containsKey(GLOBAL_PER_REQUEST_ID.get())) {
      globalProfiledDataMap.remove(GLOBAL_PER_REQUEST_ID.get());
      clearGlobalPerRequestID();
    }
  }

  static <T extends TestDataType> TestData<TestRequestData<T>, TestResponseData<T>, T> getTestData() {
    if (globalProfiledDataMap.containsKey(GLOBAL_PER_REQUEST_ID.get())) {
      return globalProfiledDataMap.get(GLOBAL_PER_REQUEST_ID.get()).getTestData();
    } else {
      LOGGER.error(
          "Trying to get test data against global request id: " + GLOBAL_PER_REQUEST_ID.get()
              + " which is not found.");
    }
    return null;
  }

  static <T extends TestDataType> void setTestData(
      TestData<TestRequestData<T>, TestResponseData<T>, T> testData) {
    if (globalProfiledDataMap.containsKey(GLOBAL_PER_REQUEST_ID.get())) {
      globalProfiledDataMap.get(GLOBAL_PER_REQUEST_ID.get()).setTestData(testData);
    } else {
      LOGGER.error(
          "Trying to set test data against global request id: " + GLOBAL_PER_REQUEST_ID.get()
              + " which is either null or the default value.");
    }
  }

  static ProfileState getProfileState() {
    if (globalProfiledDataMap.containsKey(GLOBAL_PER_REQUEST_ID.get())) {
      return globalProfiledDataMap.get(GLOBAL_PER_REQUEST_ID.get()).getProfileState();
    } else {
      LOGGER.error(
          "Trying to get request profile state against global request id: " + GLOBAL_PER_REQUEST_ID
              .get() + " which is not found.");
    }
    return ProfileState.NONE;
  }

  static void setProfileState(ProfileState profileState) {
    if (globalProfiledDataMap.containsKey(GLOBAL_PER_REQUEST_ID.get())) {
      globalProfiledDataMap.get(GLOBAL_PER_REQUEST_ID.get()).setProfileState(profileState);
    } else {
      LOGGER.error(
          "Trying to set profile state against global request id: " + GLOBAL_PER_REQUEST_ID.get()
              + " which is not found.");
    }
  }

  static void setRequestData(TestRequestData<? extends TestDataType> requestData) {
    if (globalProfiledDataMap.containsKey(GLOBAL_PER_REQUEST_ID.get())) {
      globalProfiledDataMap.get(GLOBAL_PER_REQUEST_ID.get()).getTestData()
          .setRequestData(requestData);
    } else {
      LOGGER.error(
          "Trying to set request data against global request id: " + GLOBAL_PER_REQUEST_ID.get()
              + " which is not found.");
    }
  }

  static void setResponseData(TestResponseData<? extends TestDataType> responseData) {
    if (globalProfiledDataMap.containsKey(GLOBAL_PER_REQUEST_ID.get())) {
      globalProfiledDataMap.get(GLOBAL_PER_REQUEST_ID.get()).getTestData()
          .setResponseData(responseData);
    } else {
      LOGGER.error(
          "Trying to set response data against global request id: " + GLOBAL_PER_REQUEST_ID.get()
              + " which is not found.");
    }
  }

  static void addInterceptedData(String uniqueMethodIdentifier,
      ConcurrentHashMap<MethodDataType, List<MethodData>> methodDataMap) {
    if (globalProfiledDataMap.containsKey(GLOBAL_PER_REQUEST_ID.get())) {
      ConcurrentHashMap<String, ConcurrentSkipListMap<Long, ConcurrentHashMap<MethodDataType, List<MethodData>>>> recordedMethodDataMap = globalProfiledDataMap
          .get(GLOBAL_PER_REQUEST_ID.get()).getTestData().getMethodDataMap();
      ConcurrentSkipListMap<Long, ConcurrentHashMap<MethodDataType, List<MethodData>>> data = recordedMethodDataMap
          .get(uniqueMethodIdentifier);
      if (data == null) {
        data = new ConcurrentSkipListMap<>();
        // concurrent skip list map has entries sorted in ascending order.
        data.put(System.nanoTime(), methodDataMap);
        ConcurrentSkipListMap<Long, ConcurrentHashMap<MethodDataType, List<MethodData>>> concurrentCheckData = recordedMethodDataMap
            .putIfAbsent(uniqueMethodIdentifier, data);
        if (concurrentCheckData != null) {
          data = recordedMethodDataMap.get(uniqueMethodIdentifier);
          // try 3 times to ensure that concurrency is handled fine.
          Object prevData = null;
          for (int i = 0; i < 3; i++) {
            prevData = data.putIfAbsent(System.nanoTime(), methodDataMap);
            if (prevData == null) {
              break;
            }
          }
          // if it fails even after 3 times, fail the request.
          if (prevData != null) {
            LOGGER.error("Trying to add method intercepted data against global request id: "
                + GLOBAL_PER_REQUEST_ID.get() + " failed.");
            ProfileRepository.setProfileState(ProfileState.FAILED);
            return;
          }
          // nothing gets removed from the app, so no need to worry about repetitive checks
          recordedMethodDataMap.put(uniqueMethodIdentifier, data);
        }
      } else {
        // try 3 times to ensure that concurrency is handled fine.
        Object prevData = null;
        for (int i = 0; i < 3; i++) {
          prevData = data.putIfAbsent(System.nanoTime(), methodDataMap);
          if (prevData == null) {
            break;
          }
        }
        // if it fails even after 3 times, fail the request.
        if (prevData != null) {
          LOGGER.error("Trying to add method intercepted data against global request id: "
              + GLOBAL_PER_REQUEST_ID.get() + " failed.");
          ProfileRepository.setProfileState(ProfileState.FAILED);
          return;
        }
        recordedMethodDataMap.put(uniqueMethodIdentifier, data);
      }
    } else {
      LOGGER.error("Trying to add method intercepted data against global request id: "
          + GLOBAL_PER_REQUEST_ID.get() + " which is not found.");
    }
  }
}
