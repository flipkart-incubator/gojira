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

package com.flipkart.gojira.external.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.flipkart.gojira.models.TestDataType;

/**
 * This class holds all config required for making external rpc calls. This needs to be provided by
 * the client application. // TODO: sub-calls it for different types of external calls like HTTP
 * etc.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = HttpConfig.class, name = "HTTP"),
  @JsonSubTypes.Type(value = KafkaConfig.class, name = "KAFKA"),
  @JsonSubTypes.Type(value = RMQConfig.class, name = "RMQ")
})
public abstract class ExternalConfig {
  private TestDataType type;

  public TestDataType getType() {
    return type;
  }

  public void setType(TestDataType type) {
    this.type = type;
  }
}
