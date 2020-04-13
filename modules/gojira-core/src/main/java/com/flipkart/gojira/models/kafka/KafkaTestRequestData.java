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

import com.flipkart.gojira.models.TestRequestData;
import java.util.Map;

/**
 * Extends {@link TestRequestData} for {@link KafkaTestDataType}. Captures all information required
 * for initiating a kafka request.
 */
public class KafkaTestRequestData extends TestRequestData<KafkaTestDataType> {

  /**
   * topic from which kafka request was captured.
   */
  private String topicName;

  /**
   * key with which kafka message was produced.
   */
  private byte[] key;

  /**
   * value with which kafka message was produced.
   */
  private byte[] value;

  /**
   * headers with which kafka message was produced.
   */
  private Map<String, byte[]> headers;

  private KafkaTestRequestData() {
    super(new KafkaTestDataType());
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getTopicName() {
    return topicName;
  }

  public byte[] getKey() {
    return key;
  }

  public byte[] getValue() {
    return value;
  }

  public Map<String, byte[]> getHeaders() {
    return headers;
  }

  public static class Builder {

    private KafkaTestRequestData kafkaTestRequestDataToBuild;

    private Builder() {
      this.kafkaTestRequestDataToBuild = new KafkaTestRequestData();
    }

    public KafkaTestRequestData build() {
      return this.kafkaTestRequestDataToBuild;
    }

    public Builder setTopicName(String topicName) {
      this.kafkaTestRequestDataToBuild.topicName = topicName;
      return this;
    }

    public Builder setKey(byte[] key) {
      this.kafkaTestRequestDataToBuild.key = key;
      return this;
    }

    public Builder setValue(byte[] value) {
      this.kafkaTestRequestDataToBuild.value = value;
      return this;
    }

    public Builder setHeaders(Map<String, byte[]> headers) {
      this.kafkaTestRequestDataToBuild.headers = headers;
      return this;
    }
  }
}
