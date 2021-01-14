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

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link DiffIgnoreRepository}.
 */
public class DiffIgnoreRepositoryImpl extends DiffIgnoreRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(DiffIgnoreRepositoryImpl.class);

  @Override
  public Map<DiffType, List<Pattern>> getDiffIgnorePatterns() {
    return DIFF_IGNORE_PATTERNS;
  }

  @Override
  public synchronized void setupDiffIgnorePatterns(Map<String, List<String>> jsonDiffIgnoreMap) {
    if (jsonDiffIgnoreMap != null) {
      for (Map.Entry<String, List<String>> entry : jsonDiffIgnoreMap.entrySet()) {
        DiffType diffType = Enum.valueOf(DiffType.class, entry.getKey());
        List<Pattern> ignorePatternList =
            entry.getValue().stream()
                .map(ignorePattern -> Pattern.compile(ignorePattern, Pattern.CASE_INSENSITIVE))
                .collect(Collectors.toList());
        DIFF_IGNORE_PATTERNS.put(diffType, ignorePatternList);
        LOGGER.info(diffType.toString() + " type added for paths: " + ignorePatternList.toString());
      }
    }
  }
}
