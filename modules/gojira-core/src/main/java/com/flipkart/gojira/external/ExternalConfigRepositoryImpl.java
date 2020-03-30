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
import com.flipkart.gojira.models.TestDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/** Implementation of {@link ExternalConfigRepository} */
public class ExternalConfigRepositoryImpl extends ExternalConfigRepository {

  private Logger logger = LoggerFactory.getLogger(ExternalConfigRepositoryImpl.class);

  /** @param externalConfig config to make external rpc calls */
  @Override
  public void setExternalConfig(Map<String, Map<TestDataType, ExternalConfig>> externalConfig) {
    for (Map.Entry<String, Map<TestDataType, ExternalConfig>> entry : externalConfig.entrySet()) {
      externalConfigHashMap.put(entry.getKey(), entry.getValue());
    }
  }

  /**
   * @param testDataType
   * @return config by client Map
   */
  @Override
  public Map<String, ExternalConfig> getExternalConfigByType(TestDataType testDataType) {

    Map<String, ExternalConfig> clientMap = new HashMap<>();
    if (!externalConfigHashMap.isEmpty()) {
      for (Map.Entry<String, Map<TestDataType, ExternalConfig>> entry :
          externalConfigHashMap.entrySet()) {
        String clientId = entry.getKey();
        Map<TestDataType, ExternalConfig> externalConfigByType = entry.getValue();
        if (externalConfigByType != null && !externalConfigByType.isEmpty()) {
          ExternalConfig config = externalConfigByType.get(testDataType);
          if (config != null) {
            clientMap.put(clientId, config);
          }
        }
      }
    }
    return clientMap;
  }

  /**
   * @param clientId clientId
   * @return
   */
  @Override
  public ExternalConfig getExternalConfigFor(String clientId, TestDataType testDataType) {
    Map<TestDataType, ExternalConfig> testTypeExternalConfigMap =
        externalConfigHashMap.get(clientId);
    if (testTypeExternalConfigMap != null) {
      return testTypeExternalConfigMap.get(testDataType);
    }
    logger.error("External Config not found client :{} and sourceType :{}", clientId, testDataType);
    return null;
  }

  /** @return */
  @Override
  public Map<String, Map<TestDataType, ExternalConfig>> getExternalConfig() {
    return externalConfigHashMap;
  }
}
