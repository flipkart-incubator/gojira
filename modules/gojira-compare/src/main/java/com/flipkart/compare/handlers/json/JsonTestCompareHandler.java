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

import static com.flipkart.compare.handlers.json.JsonTestCompareHandlerUtil.findStringInArray;
import static com.flipkart.compare.handlers.json.JsonTestCompareHandlerUtil.getObjectKeys;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.compare.TestCompareException;
import com.flipkart.compare.diff.DiffDetail;
import com.flipkart.compare.diff.DiffType;
import com.flipkart.compare.handlers.TestCompareHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link TestCompareHandler} for comparing byte[] that can be de-serialized to
 * {@link JsonNode}.
 */
public class JsonTestCompareHandler extends TestCompareHandler {

  private static final Logger logger = LoggerFactory.getLogger(JsonTestCompareHandler.class);
  private static final ObjectMapper mapper = new ObjectMapper();

  /**
   * This method de-serializes byte[] to {@link com.fasterxml.jackson.databind.JsonNode} Depending
   * on whether the object is an {@link ObjectNode} or {@link ArrayNode} or {@link Object} invokes
   * the respective overloaded method.
   *
   * @param profiledData expected data in bytes
   * @param testData actual data in bytes
   * @throws TestCompareException exception thrown when there is a diff
   */
  @Override
  protected void doCompare(byte[] profiledData, byte[] testData) throws TestCompareException {
    try {
      // return if both profiledData and testData are null or empty.
      if ((profiledData == null && testData == null)
          || (profiledData != null && profiledData.length == 0 && testData.length == 0)) {
        return;
      }

      // if either of them are null throw TestCompareException if it cannot be ignored.
      if (profiledData == null || testData == null) {
        DiffDetail diffDetail =
            getDiffDetail(
                null,
                profiledData == null ? null : new String(profiledData),
                testData == null ? null : new String(testData),
                DiffType.MODIFY);
        if (diffDetail != null) {
          throw new TestCompareException(Arrays.asList(diffDetail));
        }
        return;
      }

      // de-serialize to JsonNode.
      JsonNode expectedNode = mapper.readTree(profiledData);
      JsonNode actualNode = mapper.readTree(testData);

      // call compute(ArrayNode) if expectedNode is ArrayNode
      if (expectedNode.isArray() && actualNode.isArray()) {
        List<DiffDetail> diffs = new ArrayList<>();
        compute((ArrayNode) expectedNode, (ArrayNode) actualNode, diffs, "/");

        for (DiffDetail diffDetail : diffs) {
          logger.info(
              "diff type: "
                  + diffDetail.getDiffType()
                  + ", diff path: "
                  + diffDetail.getDiffPath());
        }
        if (diffs.size() > 0) {
          throw new TestCompareException(diffs);
        }

      } else if (expectedNode.isObject()
          && actualNode.isObject()) {
        // calling compute(ObjectNode) if expectedNode & arrayNode are ObjectNode instances
        List<DiffDetail> diffs = new ArrayList<>();
        compute((ObjectNode) expectedNode, (ObjectNode) actualNode, diffs, "/");

        for (DiffDetail diffDetail : diffs) {
          logger.info(
              "diff type: "
                  + diffDetail.getDiffType()
                  + ", diff path: "
                  + diffDetail.getDiffPath());
        }
        if (diffs.size() > 0) {
          throw new TestCompareException(diffs);
        }

      } else {
        // do byte[] comparison if expectedNode & arrayNode are neither ArrayNode or ObjectNode
        // instances
        if (!Arrays.equals(profiledData, testData)) {
          DiffDetail diffDetail =
              getDiffDetail("/", new String(profiledData), new String(testData), DiffType.MODIFY);
          if (diffDetail != null) {
            throw new TestCompareException(Arrays.asList(diffDetail));
          }
        }
      }
    } catch (IOException e) {
      // if we are not able to de-serialize to JsonNode, do byte[] comparison
      if (!Arrays.equals(profiledData, testData)) {
        DiffDetail diffDetail =
            getDiffDetail("/", new String(profiledData), new String(testData), DiffType.MODIFY);
        if (diffDetail != null) {
          throw new TestCompareException(Arrays.asList(diffDetail));
        }
      }
    }
  }

  /**
   * This method does ArrayNode to ArrayNode comparison.
   *
   * @param expectedArray expected data as ArrayNode
   * @param actualArray actual data as ArrayNode
   * @param diffs previously generated diffs before this invocation
   * @param diffKey nested path for this current node TODO: Refactor this method.
   */
  private void compute(
      ArrayNode expectedArray, ArrayNode actualArray, List<DiffDetail> diffs, String diffKey) {
    // Maintain 2 maps to identify ADD & REMOVE
    Map<Integer, Integer> mappedExpectedToActual = new HashMap<>();
    Map<Integer, Integer> mappedActualToExpected = new HashMap<>();

    // Match all elements in expected elements in JSONArray
    for (int i = 0; i < expectedArray.size(); i++) {
      for (int j = 0; j < actualArray.size(); j++) {
        // if this element in actualArray is already mapped to another element in expected, skip
        if (mappedActualToExpected.containsKey(j)) {
          continue;
        }

        Object expectedObject = expectedArray.get(i);
        Object actualObject = actualArray.get(j);

        // create temporary newDiffs array list to get initial match against any element in list
        List<DiffDetail> newDiffs = new ArrayList<>();
        compute(expectedObject, actualObject, newDiffs, diffKey + ", (.*)");
        if (newDiffs.size() > 0) {
          continue;
        }

        // update mappedExpectedToActual and mappedActualToExpected
        mappedExpectedToActual.put(i, j);
        mappedActualToExpected.put(j, i);

        // create a diff of type move if i does not match j
        if (i != j) {
          DiffDetail diff =
              getDiffDetail(diffKey + ", (.*), /", expectedObject, actualObject, DiffType.MOVE);
          if (diff != null) {
            diffs.add(diff);
          }
        }
        break;
      }
    }

    List<Integer> notMappedExpectedObjectNodesIndexList = new ArrayList<>();
    List<Integer> notMappedActualObjectNodesIndexList = new ArrayList<>();
    Map<Integer, List<Integer>> notMappedExpectedToPotentialActualObjectNodesMapForDiffComp =
        new HashMap<>();
    for (int i = 0; i < expectedArray.size(); i++) {
      if (!mappedExpectedToActual.containsKey(i) && (expectedArray.get(i).isObject())) {
        notMappedExpectedObjectNodesIndexList.add(i);
      }
    }

    for (int i = 0; i < actualArray.size(); i++) {
      if (!mappedActualToExpected.containsKey(i) && (actualArray.get(i).isObject())) {
        notMappedActualObjectNodesIndexList.add(i);
      }
    }

    for (Integer notMappedExpectedObjectNodeIndex : notMappedExpectedObjectNodesIndexList) {
      String[] expectedKeys =
          JsonTestCompareHandlerUtil.getObjectKeys(
              (ObjectNode) (expectedArray.get(notMappedExpectedObjectNodeIndex)));
      for (Integer notMappedActualObjectNodeIndex : notMappedActualObjectNodesIndexList) {
        String[] actualKeys =
            JsonTestCompareHandlerUtil.getObjectKeys(
                (ObjectNode) (actualArray.get(notMappedActualObjectNodeIndex)));
        if (JsonTestCompareHandlerUtil.allExpectedKeysInActualKeys(expectedKeys, actualKeys)) {
          if (notMappedExpectedToPotentialActualObjectNodesMapForDiffComp.containsKey(
              notMappedExpectedObjectNodeIndex)) {
            notMappedExpectedToPotentialActualObjectNodesMapForDiffComp
                .get(notMappedExpectedObjectNodeIndex)
                .add(notMappedActualObjectNodeIndex);
          } else {
            List<Integer> potentialActualNodeIndexList = new ArrayList<>();
            potentialActualNodeIndexList.add(notMappedActualObjectNodeIndex);
            notMappedExpectedToPotentialActualObjectNodesMapForDiffComp.put(
                notMappedExpectedObjectNodeIndex, potentialActualNodeIndexList);
          }
        }
      }
    }

    for (int i = 0; i < expectedArray.size(); i++) {
      if (mappedExpectedToActual.containsKey(i)) {
        continue;
      }

      Map<Integer, List<DiffDetail>> mapOfAllDiffDetails = new HashMap<>();
      for (int j = 0; j < actualArray.size(); j++) {
        if (mappedActualToExpected.containsKey(j)) {
          continue;
        }

        if (notMappedExpectedToPotentialActualObjectNodesMapForDiffComp.containsKey(i)
            && !notMappedExpectedToPotentialActualObjectNodesMapForDiffComp.get(i).contains(j)) {
          continue;
        }

        Object actualObject = actualArray.get(j);
        Object expectedObject = expectedArray.get(i);

        List<DiffDetail> newDiffs = new ArrayList<>();
        compute(expectedObject, actualObject, newDiffs, diffKey + ", (.*)");
        if (newDiffs.size() > 0) {
          mapOfAllDiffDetails.put(j, newDiffs);
          continue;
        }

        mappedExpectedToActual.put(i, j);
        mappedActualToExpected.put(j, i);
      }

      if (mapOfAllDiffDetails.size() > 0) {
        List<DiffDetail> minDiffDetail =
            mapOfAllDiffDetails.entrySet().iterator().next().getValue();
        Integer j = mapOfAllDiffDetails.entrySet().iterator().next().getKey();
        for (Map.Entry<Integer, List<DiffDetail>> diffDetailsEntry :
            mapOfAllDiffDetails.entrySet()) {
          if (diffDetailsEntry.getValue().size() < minDiffDetail.size()) {
            minDiffDetail = diffDetailsEntry.getValue();
            j = diffDetailsEntry.getKey();
          }
        }
        diffs.addAll(minDiffDetail);
        mappedActualToExpected.put(j, i);
        mappedExpectedToActual.put(i, j);
      }
    }

    // all actual elements but not expected elements in JSONArray
    for (int j = 0; j < actualArray.size(); j++) {
      if (mappedActualToExpected.containsKey(j)) {
        continue;
      }

      DiffDetail diffDetail =
          getDiffDetail(diffKey + ", (.*), /", null, actualArray.get(j), DiffType.MODIFY);
      if (diffDetail != null) {
        diffs.add(diffDetail);
      }
    }

    // all expected elements but not actual elements in JSONArray
    for (int i = 0; i < expectedArray.size(); i++) {
      if (mappedExpectedToActual.containsKey(i)) {
        continue;
      }

      DiffDetail diffDetail =
          getDiffDetail(diffKey + ", (.*), /", expectedArray.get(i), null, DiffType.MODIFY);
      if (diffDetail != null) {
        diffs.add(diffDetail);
      }
    }
  }

  /**
   * This method does ObjectNode to ObjectNode comparison.
   *
   * @param expectedObject expected data as ObjectNode
   * @param actualObject actual data as ObjectNode
   * @param diffs previously generated diffs before this invocation
   * @param diffKey nested path for this current node
   */
  private void compute(
      ObjectNode expectedObject, ObjectNode actualObject, List<DiffDetail> diffs, String diffKey) {
    // get list of keys for expected and actual objects
    String[] expectedKeys = getObjectKeys(expectedObject);
    String[] actualKeys = getObjectKeys(actualObject);

    // compare all expected objects in actual
    if (expectedKeys.length > 0) {
      for (String expectedKey : expectedKeys) {
        compute(
            expectedObject.get(expectedKey),
            actualObject.has(expectedKey) ? actualObject.get(expectedKey) : null,
            diffs,
            diffKey + ", " + expectedKey);
      }
    }

    // compare additional actual objects which are not present in expected
    if (actualKeys.length > 0) {
      for (String actualKey : actualKeys) {
        // ignore expected objects which are already compared against actual objects
        if (!findStringInArray(actualKey, expectedKeys)) {
          compute(null, actualObject.get(actualKey), diffs, diffKey + ", " + actualKey);
        }
      }
    }
  }

  /**
   * This method does Object to Object comparison. Depending on whether the object is an {@link
   * ObjectNode} or {@link ArrayNode} invokes the respective overloaded method.
   *
   * @param expected expected data as Object
   * @param actual actual data as Object
   * @param diffs previously generated diffs before this invocation
   * @param diffKey nested path for this current node
   */
  private void compute(Object expected, Object actual, List<DiffDetail> diffs, String diffKey) {
    // base checks for null
    if (expected == null && actual == null) {
      return;
    }

    if (expected == null || actual == null) {
      DiffDetail diffDetail = getDiffDetail(diffKey + ", /", expected, actual, DiffType.MODIFY);
      if (diffDetail != null) {
        diffs.add(diffDetail);
      }
      return;
    }

    // call compute(ArrayNode) if both expected & actual are ArrayNode instances
    if (expected instanceof ArrayNode && actual instanceof ArrayNode) {
      compute((ArrayNode) expected, (ArrayNode) actual, diffs, diffKey);
    } else if (expected instanceof ObjectNode && actual instanceof ObjectNode) {
      // call compute(ObjectNode) if both expected & actual are ObjectNode instances
      compute((ObjectNode) expected, (ObjectNode) actual, diffs, diffKey);
    } else if (!expected.equals(actual)) {
      // compare them as object by calling equals. This should be most likely called only on
      // primitive types.
      // TODO: Add warning log by mapping JsonNodeType to primitive and flag non-primitive types
      DiffDetail diffDetail = getDiffDetail(diffKey + ", /", expected, actual, DiffType.MODIFY);
      if (diffDetail != null) {
        diffs.add(diffDetail);
      }
    }
    // TODO: What if expected and actual are of different types?
  }
}
