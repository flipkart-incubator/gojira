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

import java.util.HashMap;
import java.util.Map;

/** This class acts as repository interface for {@link ExternalConfig} */
public abstract class ExternalConfigRepository {

  protected static final Map<String, Map<Class<? extends TestDataType>, ExternalConfig>>
      externalConfigHashMap = new HashMap<>();
  /**
   * @param clientId clientId
   * @return {@link ExternalConfig} instance for the clientId. null if it is not present.
   */
  public abstract ExternalConfig getExternalConfigFor(
      String clientId, Class<? extends TestDataType> testDataType);

  /** @return */
  public abstract Map<String, Map<Class<? extends TestDataType>, ExternalConfig>>
      getExternalConfig();

  /** @param externalConfig config to make external rpc calls */
  public abstract void setExternalConfig(
      Map<String, Map<Class<? extends TestDataType>, ExternalConfig>> externalConfig);

  /**
   * @param testDataType
   * @return config by client map
   */
  public abstract Map<String, ExternalConfig> getExternalConfigByType(
      Class<? extends TestDataType> testDataType);
}
