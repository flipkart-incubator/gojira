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

import com.flipkart.gojira.models.TestDataType;
import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.models.TestResponseData;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This handler is responsible for invoking {@link Mode} specific implementation of {@link
 * StartEndTestHandler} TODO: Refactor this class.
 */
public class DefaultProfileOrTestHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProfileOrTestHandler.class);

  /**
   * Is an {@link java.util.Collections.UnmodifiableMap} of all handlers per {@link Mode}
   */
  private static final Map<Mode, StartEndTestHandler> startTestHandlerHashMap = Collections
      .unmodifiableMap(
          new HashMap<Mode, StartEndTestHandler>() {{
            put(Mode.PROFILE, new ProfileStartEndTestHandler());
            put(Mode.TEST, new TestStartEndTestHandler());
            put(Mode.NONE, new NoneStartEndTestHandler());
            put(Mode.SERIALIZE, new SerializeStartEndTestHandler());
          }}
      );


  /**
   * Simply gets the respective handler for {@link Mode} and calls {@link
   * StartEndTestHandler#start(String, TestRequestData)} method.
   * <p>
   * If mode is not registered, logs an error.
   *
   * @param id          id for co-ordinating execution
   * @param requestData request data at the start of execution
   */
  public static void start(String id, TestRequestData<? extends TestDataType> requestData) {
    if (startTestHandlerHashMap.containsKey(ProfileRepository.getMode())) {
      startTestHandlerHashMap.get(ProfileRepository.getMode()).start(id, requestData);
      return;
    }
    LOGGER.error("Processing logic not implemented for this mode: " + ProfileRepository.getMode());
  }

  /**
   * Simply gets the respective handler for {@link Mode} and calls {@link
   * StartEndTestHandler#end(TestResponseData)} method.
   * <p>
   * If mode is not registered, logs an error.
   *
   * @param responseData response data at the end of execution
   */
  public static void end(TestResponseData<? extends TestDataType> responseData) {
    if (startTestHandlerHashMap.containsKey(ProfileRepository.getMode())) {
      startTestHandlerHashMap.get(ProfileRepository.getMode()).end(responseData);
      return;
    }
    LOGGER.error("Processing logic not implemented for this mode: " + ProfileRepository.getMode());
  }
}
