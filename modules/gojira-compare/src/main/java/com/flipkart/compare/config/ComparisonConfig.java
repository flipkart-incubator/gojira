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

package com.flipkart.compare.config;

import com.flipkart.compare.diff.DiffIgnoreRepository;
import com.flipkart.compare.handlers.TestCompareHandler;
import java.util.List;
import java.util.Map;

/**
 * ComparisonConfig holds required config for comparing. It contains a defaultCompareHandler for
 * comparing objects and diffIgnoreMap to enable known patterns that can be ignored.
 */
public class ComparisonConfig {

  /**
   * Map of DiffType vs DiffPatterns for diffIgnorePatterns in {@link DiffIgnoreRepository}
   */
  private Map<String, List<String>> diffIgnoreMap = null;
  /**
   * Default compare handler that can be used for comparing two byte[]
   */
  private TestCompareHandler defaultCompareHandler;

  public Map<String, List<String>> getDiffIgnoreMap() {
    return diffIgnoreMap;
  }

  protected void setDiffIgnoreMap(Map<String, List<String>> diffIgnoreMap) {
    this.diffIgnoreMap = diffIgnoreMap;
  }

  public TestCompareHandler getDefaultCompareHandler() {
    return defaultCompareHandler;
  }

  protected void setDefaultCompareHandler(TestCompareHandler defaultCompareHandler) {
    this.defaultCompareHandler = defaultCompareHandler;
  }
}
