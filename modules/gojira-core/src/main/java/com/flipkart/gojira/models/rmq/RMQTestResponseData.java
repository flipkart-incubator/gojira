package com.flipkart.gojira.models.rmq;

import com.flipkart.gojira.models.TestResponseData;

/**
 * Extends {@link TestResponseData} for {@link RMQTestDataType}. Captures all information required
 * for comparing a rmq response.
 */
public class RMQTestResponseData extends TestResponseData<RMQTestDataType> {

  /** response data in bytes. in most cases this is expected to be null. */
  private byte[] respondData;

  private RMQTestResponseData() {
    super(new RMQTestDataType());
  }

  public static RMQTestResponseData.Builder builder() {
    return new RMQTestResponseData.Builder();
  }

  public byte[] getRespondData() {
    return respondData;
  }

  public static class Builder {

    private RMQTestResponseData rmqTestResponseData;

    private Builder() {
      this.rmqTestResponseData = new RMQTestResponseData();
    }

    public RMQTestResponseData build() {
      return this.rmqTestResponseData;
    }

    public RMQTestResponseData.Builder setRespondData(byte[] respondData) {
      this.rmqTestResponseData.respondData = respondData;
      return this;
    }
  }
}
