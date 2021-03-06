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

package com.flipkart.gojira.compare.config;

import com.flipkart.compare.config.ComparisonConfig;
import com.flipkart.compare.handlers.TestCompareHandler;
import com.flipkart.compare.handlers.json.JsonTestCompareHandler;
import java.util.List;
import java.util.Map;

/**
 * Extension of {@link ComparisonConfig} with additional handlers for comparing response object.
 */
public class GojiraComparisonConfig extends ComparisonConfig {

  /**
   * Implementation for comparing response data.
   */
  private TestCompareHandler responseDataCompareHandler = new JsonTestCompareHandler();

  public static Builder builder() {
    return new Builder();
  }

  public TestCompareHandler getResponseDataCompareHandler() {
    return responseDataCompareHandler;
  }

  public static class Builder {

    private GojiraComparisonConfig gojiraComparisonConfigToBuild;

    private Builder() {
      this.gojiraComparisonConfigToBuild = new GojiraComparisonConfig();
    }

    public GojiraComparisonConfig build() {
      return this.gojiraComparisonConfigToBuild;
    }

    public Builder setDefaultCompareHandler(TestCompareHandler defaultCompareHandler) {
      this.gojiraComparisonConfigToBuild.setDefaultCompareHandler(defaultCompareHandler);
      return this;
    }

    public Builder setResponseDataCompareHandler(TestCompareHandler responseDataCompareHandler) {
      this.gojiraComparisonConfigToBuild.responseDataCompareHandler = responseDataCompareHandler;
      return this;
    }

    public Builder setDiffIgnoreMap(Map<String, List<String>> diffIgnoreMap) {
      this.gojiraComparisonConfigToBuild.setDiffIgnoreMap(diffIgnoreMap);
      return this;
    }
  }

}
