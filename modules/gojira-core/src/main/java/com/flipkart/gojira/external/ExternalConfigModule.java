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

import static com.flipkart.gojira.core.GojiraConstants.TEST_DATA_TYPE_STRING_TO_CLASS;

import com.flipkart.gojira.external.config.ExternalConfig;
import com.flipkart.gojira.models.TestDataType;
import com.google.inject.AbstractModule;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Guice module which is used for binding {@link ExternalConfig}.
 */
public class ExternalConfigModule extends AbstractModule {

  private final Map<String, List<ExternalConfig>> clientToListConfigMap;

  public ExternalConfigModule(Map<String, List<ExternalConfig>> clientToListConfigMap) {
    this.clientToListConfigMap = clientToListConfigMap;
  }

  @Override
  protected void configure() {
    ExternalConfigRepositoryImpl externalConfigRepository = new ExternalConfigRepositoryImpl();
    Map<String, Map<Class<? extends TestDataType>, ExternalConfig>> externalConfigMap =
        new HashMap<>();

    clientToListConfigMap.forEach(
        (client, configList) -> {
          externalConfigMap.putIfAbsent(client, new HashMap<>());
          configList.forEach(
              (config) -> {
                if (externalConfigMap
                    .get(client)
                    .containsKey(TEST_DATA_TYPE_STRING_TO_CLASS.get(config.getType()))) {
                  throw new RuntimeException("Only one config per type is allowed. ");
                }
                externalConfigMap
                    .get(client)
                    .put(TEST_DATA_TYPE_STRING_TO_CLASS.get(config.getType()), config);
              });
        });
    externalConfigRepository.setExternalConfig(externalConfigMap);
    bind(ExternalConfigRepository.class).toInstance(externalConfigRepository);
  }
}
