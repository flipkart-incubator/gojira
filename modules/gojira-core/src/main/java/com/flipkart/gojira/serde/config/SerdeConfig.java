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

package com.flipkart.gojira.serde.config;

import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.models.TestResponseData;
import com.flipkart.gojira.serde.handlers.TestSerdeHandler;
import com.flipkart.gojira.serde.handlers.json.JsonDefaultTestSerdeHandler;

/**
 * Configuration class for various serialization handlers. This is to be provided by the client
 * application.
 */
public class SerdeConfig {

  /**
   * Implementation for serializing/de-serializing annotated method argument(s), return type and
   * exception for a method. Unless overridden, this would be used.
   */
  private TestSerdeHandler defaultSerdeHandler = new JsonDefaultTestSerdeHandler();

  /**
   * Implementation for serializing/de-serializing {@link TestRequestData} and {@link
   * TestResponseData}
   */
  private TestSerdeHandler reqRespDataSerdeHandler = new JsonDefaultTestSerdeHandler();

  /**
   * Implementation for serializing/de-serializing {@link TestData}
   * <p>
   * //TODO: This should not be exposed to client.
   */
  private TestSerdeHandler testDataSerdeHandler = new JsonDefaultTestSerdeHandler();

  private SerdeConfig() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public TestSerdeHandler getDefaultSerdeHandler() {
    return defaultSerdeHandler;
  }

  public TestSerdeHandler getReqRespDataSerdeHandler() {
    return reqRespDataSerdeHandler;
  }

  public TestSerdeHandler getTestDataSerdeHandler() {
    return testDataSerdeHandler;
  }

  public static class Builder {

    private SerdeConfig serdeConfigToBuild;

    private Builder() {
      this.serdeConfigToBuild = new SerdeConfig();
    }

    public SerdeConfig build() {
      return this.serdeConfigToBuild;
    }

    public Builder setDefaultSerdeHandler(TestSerdeHandler testSerdeHandler) {
      this.serdeConfigToBuild.defaultSerdeHandler = testSerdeHandler;
      return this;
    }

    //external implementation not required for the time being
    private Builder setReqRespDataSerdeHandler(TestSerdeHandler testSerdeHandler) {
      this.serdeConfigToBuild.reqRespDataSerdeHandler = testSerdeHandler;
      return this;
    }

    //external implementation not required for the time being
    private Builder setTestDataSerdeHandler(TestSerdeHandler testSerdeHandler) {
      this.serdeConfigToBuild.testDataSerdeHandler = testSerdeHandler;
      return this;
    }
  }
}
