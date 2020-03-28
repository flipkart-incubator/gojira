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

package com.flipkart.compare.handlers.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * JsonTestCompareHandlerUtil is a helper class with static methods called by {@link
 * JsonTestCompareHandler}
 */
public final class JsonTestCompareHandlerUtil {

  /**
   * Helper function which given an
   *
   * @param objectNode returns
   * @return string array of keys
   */
  static String[] getObjectKeys(ObjectNode objectNode) {
    List<String> keys = new ArrayList<>();
    if (objectNode.size() != 0) {
      objectNode.fieldNames().forEachRemaining(keys::add);
    }
    return Arrays.copyOf(keys.toArray(), keys.toArray().length, String[].class);
  }

  /**
   * Helper function which checks if all
   *
   * @param expectedKeys string array of expected keys are present in
   * @param actualKeys   string array of actual keys and returns
   * @return boolean value true if found else false
   */
  static boolean allExpectedKeysInActualKeys(String[] expectedKeys, String[] actualKeys) {
    for (String expectedKey : expectedKeys) {
      boolean found = false;
      for (String actualKey : actualKeys) {
        if (actualKey.equals(expectedKey)) {
          found = true;
          break;
        }
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }

  /**
   * Helper function which checks if a
   *
   * @param find input string to be found is present in
   * @param data data string array and returns
   * @return boolean value true if found else false
   */
  static boolean findStringInArray(String find, String[] data) {
    boolean keyFound = false;
    for (String dataElement : data) {
      if (find.equals(dataElement)) {
        keyFound = true;
      }
    }
    return keyFound;
  }
}

