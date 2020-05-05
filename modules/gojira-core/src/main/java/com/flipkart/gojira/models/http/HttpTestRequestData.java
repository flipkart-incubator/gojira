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

import com.flipkart.gojira.models.TestRequestData;
import java.util.Arrays;
import java.util.Map;

/**
 * Extends {@link TestRequestData} for {@link HttpTestDataType}. Captures all information required
 * for initiating a http request.
 */
public class HttpTestRequestData extends TestRequestData<HttpTestDataType> {

  /**
   * body in bytes.
   */
  private byte[] body;

  /**
   * http request headers.
   */
  private Map<String, String> headers;

  /**
   * http query parameters.
   */
  private String queryParams;

  /**
   * http uri.
   */
  private String uri;

  /**
   * http method.
   */
  private String method;

  private HttpTestRequestData() {
    super(new HttpTestDataType());
  }

  public static Builder builder() {
    return new Builder();
  }

  public byte[] getBody() {
    return body;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public String getQueryParams() {
    return queryParams;
  }

  public String getUri() {
    return uri;
  }

  public String getMethod() {
    return method;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    HttpTestRequestData that = (HttpTestRequestData) o;

    if (!Arrays.equals(body, that.body)) {
      return false;
    }
    if (!headers.equals(that.headers)) {
      return false;
    }
    if (!queryParams.equals(that.queryParams)) {
      return false;
    }
    if (!uri.equals(that.uri)) {
      return false;
    }
    return method.equals(that.method);

  }

  @Override
  public int hashCode() {
    int result = Arrays.hashCode(body);
    result = 31 * result + headers.hashCode();
    result = 31 * result + queryParams.hashCode();
    result = 31 * result + uri.hashCode();
    result = 31 * result + method.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "HttpTestRequestData{"
        + "body="
        + Arrays.toString(body)
        + ", headers="
        + headers
        + ", queryParams="
        + queryParams
        + ", uri="
        + uri
        + ", method="
        + method
        + '}';
  }

  public static class Builder {

    private HttpTestRequestData httpTestRequestDataToBuild;

    private Builder() {
      this.httpTestRequestDataToBuild = new HttpTestRequestData();
    }

    public HttpTestRequestData build() {
      return this.httpTestRequestDataToBuild;
    }

    public Builder setBody(byte[] body) {
      this.httpTestRequestDataToBuild.body = body;
      return this;
    }

    public Builder setHeaders(Map<String, String> headers) {
      this.httpTestRequestDataToBuild.headers = headers;
      return this;
    }

    public Builder setQueryParams(String queryParams) {
      this.httpTestRequestDataToBuild.queryParams = queryParams;
      return this;
    }

    public Builder setUri(String uri) {
      this.httpTestRequestDataToBuild.uri = uri;
      return this;
    }

    public Builder setMethod(String method) {
      this.httpTestRequestDataToBuild.method = method;
      return this;
    }
  }
}
