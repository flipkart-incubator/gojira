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

package com.flipkart.gojira.models.http;

import com.flipkart.gojira.models.TestResponseData;
import java.util.Arrays;
import java.util.Map;

/**
 * Extends {@link TestResponseData} for {@link HttpTestDataType}. Captures all information required
 * for comparing a http resposne.
 */
public class HttpTestResponseData extends TestResponseData<HttpTestDataType> {

  /**
   * http status code
   */
  private int statusCode;

  /**
   * http response headers
   */
  private Map<String, String> headers;

  /**
   * http response body
   */
  private byte[] body;

  private HttpTestResponseData() {
    super(new HttpTestDataType());
  }

  public static Builder builder() {
    return new Builder();
  }

  public int getStatusCode() {
    return statusCode;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public byte[] getBody() {
    return body;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    HttpTestResponseData that = (HttpTestResponseData) o;

    if (statusCode != that.statusCode) {
      return false;
    }
    if (!headers.equals(that.headers)) {
      return false;
    }
    return Arrays.equals(body, that.body);

  }

  @Override
  public int hashCode() {
    int result = statusCode;
    result = 31 * result + headers.hashCode();
    result = 31 * result + Arrays.hashCode(body);
    return result;
  }

  @Override
  public String toString() {
    return "HttpTestResponseData{" +
        "statusCode=" + statusCode +
        ", headers=" + headers +
        ", body=" + Arrays.toString(body) +
        '}';
  }

  public static class Builder {

    private HttpTestResponseData httpTestResponseDataToBuild;

    private Builder() {
      this.httpTestResponseDataToBuild = new HttpTestResponseData();
    }

    public HttpTestResponseData build() {
      return this.httpTestResponseDataToBuild;
    }

    public Builder setBody(byte[] body) {
      this.httpTestResponseDataToBuild.body = body;
      return this;
    }

    public Builder setHeaders(Map<String, String> headers) {
      this.httpTestResponseDataToBuild.headers = headers;
      return this;
    }

    public Builder setStatusCode(int statusCode) {
      this.httpTestResponseDataToBuild.statusCode = statusCode;
      return this;
    }

  }
}
