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

package com.flipkart.gojira.external;

import com.flipkart.gojira.external.config.ExternalConfig;
import java.util.Map;

/**
 * Implementation of {@link ExternalConfigRepository}
 */
public class ExternalConfigRepositoryImpl extends ExternalConfigRepository {

  /**
   * @param key clientId
   * @return
   */
  @Override
  public ExternalConfig getExternalConfigFor(String key) {
    return externalConfigHashMap.get(key);
  }

  /**
   * @return
   */
  @Override
  public Map<String, ExternalConfig> getExternalConfig() {
    return externalConfigHashMap;
  }

  /**
   * @param externalConfig config to make external rpc calls
   */
  @Override
  public void setExternalConfig(Map<String, ExternalConfig> externalConfig) {
    for (Map.Entry<String, ExternalConfig> entry : externalConfig.entrySet()) {
      externalConfigHashMap.put(entry.getKey(), entry.getValue());
    }
  }
}
