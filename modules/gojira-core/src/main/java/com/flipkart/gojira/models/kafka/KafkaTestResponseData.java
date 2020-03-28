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

package com.flipkart.gojira.models.kafka;

import com.flipkart.gojira.models.TestResponseData;

/**
 * Extends {@link TestResponseData} for {@link KafkaTestDataType}. Captures all information required
 * for comparing a kafka response.
 */

public class KafkaTestResponseData extends TestResponseData<KafkaTestDataType> {

  /**
   * response data in bytes. in most cases this is expected to be null.
   */
  private byte[] respondData;

  private KafkaTestResponseData() {
    super(new KafkaTestDataType());
  }

  public static Builder builder() {
    return new Builder();
  }

  public byte[] getRespondData() {
    return respondData;
  }

  public static class Builder {

    private KafkaTestResponseData kafkaTestResponseDataToBuild;

    private Builder() {
      this.kafkaTestResponseDataToBuild = new KafkaTestResponseData();
    }

    public KafkaTestResponseData build() {
      return this.kafkaTestResponseDataToBuild;
    }

    public Builder setRespondData(byte[] respondData) {
      this.kafkaTestResponseDataToBuild.respondData = respondData;
      return this;
    }

  }
}
