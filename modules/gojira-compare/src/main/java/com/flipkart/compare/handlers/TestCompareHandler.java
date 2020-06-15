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

package com.flipkart.compare.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.compare.TestCompareException;
import com.flipkart.compare.diff.DiffDetail;
import com.flipkart.compare.diff.DiffIgnoreRepository;
import com.flipkart.compare.diff.DiffType;
import com.google.inject.Inject;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TestCompareHandler is the abstract class that can be extended to provide specific comparison
 * logic as required depending on the object and/or it's serialization format.
 */
public abstract class TestCompareHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestCompareHandler.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * diffIgnoreRepository is used for ignoring generated {@link DiffDetail}.
   */
  @Inject private static DiffIgnoreRepository diffIgnoreRepository;

  /**
   * This is non extensible implementation of compare logic. invokes doCompare on the concrete
   * class. Does basic logging of non-ignored {@link DiffDetail} objects before throwing the
   * exception back again.
   *
   * @param profiledData data to be compared against
   * @param testData data generated during current execution
   * @throws TestCompareException if there is a non-ignorable {@link DiffDetail}
   */
  public final void compare(byte[] profiledData, byte[] testData) throws TestCompareException {
    try {
      doCompare(profiledData, testData);
    } catch (TestCompareException e) {
      List<DiffDetail> diffDetails = e.getDiffs();
      if (diffDetails != null && !diffDetails.isEmpty()) {
        for (DiffDetail diffDetail : diffDetails) {
          String diffPathForIgnoreRule = diffDetail.getDiffPath();
          LOGGER.error(
              "not ignored DiffDetail | "
                  + diffDetail.getDiffType()
                  + ":"
                  + diffPathForIgnoreRule
                  + "suggested ignore pattern | "
                  + diffPathForIgnoreRule.replaceAll(", \"[a-zA-Z0-9_-]*\",", ", (.*),"));
        }
      }

      String diffsString = null;
      try {
        diffsString = OBJECT_MAPPER.writeValueAsString(diffDetails);
      } catch (JsonProcessingException e1) {
        LOGGER.error("error serializing diffs.");
      }
      LOGGER.error("non-ignorable diff present: " + diffsString, diffDetails);
      throw new TestCompareException("non-ignorable diff present: " + diffsString, diffDetails);
    }
  }

  /**
   * This method is invoked by {@link TestCompareHandler#compare(byte[], byte[])} method. Specific
   * implementation instantiated by the client would be invoked.
   *
   * @param profiledData data to be compared against
   * @param testData data generated during current execution
   * @throws TestCompareException if there is a non-ignorable {@link DiffDetail}
   */
  protected abstract void doCompare(byte[] profiledData, byte[] testData)
      throws TestCompareException;

  /**
   * Given a {@link DiffDetail}, this method used data from diffIgnoreRepository to check if it can
   * be ignored.
   *
   * @param diffDetail {@link DiffDetail} generated by the specific implementation of {@link
   *     TestCompareHandler}
   * @return true if {@link DiffDetail} can be ignored
   */
  public final boolean canBeIgnored(DiffDetail diffDetail) {
    String diffPathForIgnoreRule = diffDetail.getDiffPath();

    if (diffDetail.getDiffType().equals(DiffType.ADD)
        && diffIgnoreRepository.getDiffIgnorePatterns().containsKey(DiffType.ADD)) {
      for (Pattern pattern : diffIgnoreRepository.getDiffIgnorePatterns().get(DiffType.ADD)) {
        if (pattern.matcher(diffPathForIgnoreRule).matches()) {
          return true;
        }
      }
    }

    if (diffDetail.getDiffType().equals(DiffType.MOVE)
        && diffIgnoreRepository.getDiffIgnorePatterns().containsKey(DiffType.MOVE)) {
      for (Pattern pattern : diffIgnoreRepository.getDiffIgnorePatterns().get(DiffType.MOVE)) {
        if (pattern.matcher(diffPathForIgnoreRule).matches()) {
          return true;
        }
      }
    }

    if (diffDetail.getDiffType().equals(DiffType.REMOVE)
        && diffIgnoreRepository.getDiffIgnorePatterns().containsKey(DiffType.REMOVE)) {
      for (Pattern pattern : diffIgnoreRepository.getDiffIgnorePatterns().get(DiffType.REMOVE)) {
        if (pattern.matcher(diffPathForIgnoreRule).matches()) {
          return true;
        }
      }
    }

    if (diffDetail.getDiffType().equals(DiffType.MODIFY)
        && diffIgnoreRepository.getDiffIgnorePatterns().containsKey(DiffType.MODIFY)) {
      for (Pattern pattern : diffIgnoreRepository.getDiffIgnorePatterns().get(DiffType.MODIFY)) {
        if (pattern.matcher(diffPathForIgnoreRule).matches()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Function to create {@link DiffDetail}.
   *
   * @param diffKey path to the node where diff has been detected
   * @param expected the expected value, currently not being used, though values are set
   * @param actual the actual value, currently not being used, though values are set
   * @param diffType the diff type: MODIFY or MOVE constructs the DiffDetail object and calls {@link
   *     TestCompareHandler#canBeIgnored(DiffDetail)} to check if the {@link DiffDetail} can be
   *     ignored. and returns
   * @return null or {@link DiffDetail} object accordingly
   */
  public final DiffDetail getDiffDetail(
      String diffKey, Object expected, Object actual, DiffType diffType) {
    DiffDetail.Builder diffDetailBuilder =
        DiffDetail.builder()
            .setDiffPath(diffKey)
            .setActualValue(actual)
            .setExpectedValue(expected)
            .setDiffType(diffType);

    // take care of REMOVE
    if (actual == null && expected != null) {
      diffDetailBuilder.setDiffType(DiffType.REMOVE);
    }

    // take care of ADD
    if (actual != null && expected == null) {
      diffDetailBuilder.setDiffType(DiffType.ADD);
    }

    DiffDetail diffDetail = diffDetailBuilder.build();

    // return null if diff can be ignored
    if (canBeIgnored(diffDetail)) {
      return null;
    }

    return diffDetail;
  }
}
