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

/**
 * Helper class for Gojira Execution Mode.
 */
public class ModeHelper {

  /**
   * Takes string input and returns appropriate Gojira Execution Mode.
   *
   * @param requestMode String Gojira header.
   * @return gojira mode
   */
  public static Mode getRequestMode(String requestMode) {
    if (Mode.DYNAMIC.equals(ProfileRepository.getGlobalProfileSetting().getMode())) {
      try {
        if (null == requestMode
                || requestMode.isEmpty()
                || Mode.DYNAMIC.name().equals(requestMode)) {
          return Mode.NONE;
        } else {
          return Mode.valueOf(requestMode);
        }
      } catch (Exception e) {
        return Mode.NONE;
      }
    } else {
      return ProfileRepository.getGlobalProfileSetting().getMode();
    }
  }
}

