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

package com.flipkart.compare.diff;

/**
 * DiffType is the list of different types of DiffDetail that can be generated. If expected == null
 * and actual != null then DiffType == ADD If expected != null and actual == null then DiffType ==
 * REMOVE If expected != null and actual != null and expected != actual then DiffType == MODIFY If
 * expected != null and actual != null and expected == actual but they are not in the same
 * order(applies to array, list, set, etc.) then DiffType == MOVE
 */
public enum DiffType {
  ADD, MODIFY, REMOVE, MOVE
}
