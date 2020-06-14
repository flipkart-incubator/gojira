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

package com.flipkart.gojira.execute.http;

import com.flipkart.gojira.core.GlobalConstants;
import com.flipkart.gojira.core.Mode;
import com.flipkart.gojira.execute.TestExecutor;
import com.flipkart.gojira.external.http.HttpCallException;
import com.flipkart.gojira.external.http.IHttpHelper;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.http.HttpTestDataType;
import com.flipkart.gojira.models.http.HttpTestRequestData;
import com.flipkart.gojira.models.http.HttpTestResponseData;
import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link TestExecutor} for {@link HttpTestDataType}.
 */
public class DefaultHttpTestExecutor
    implements TestExecutor<TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHttpTestExecutor.class);

  private static final String queryParamDelimiter = "?";
  private static final String contentLengthHeader = "Content-Length";
  private final IHttpHelper httpHelper;

  @Inject
  public DefaultHttpTestExecutor(final IHttpHelper httpHelper) {
    this.httpHelper = httpHelper;
  }

  /**
   * When executing adds testId to http headers.
   *
   * @param testData testData which is used for invoking execution
   * @param clientId identifier to indicate which system hit
   * @throws HttpCallException if HttpCall fails.
   */
  @Override
  public void execute(
      TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> testData,
      String clientId)
      throws HttpCallException {
    HttpTestRequestData requestData = testData.getRequestData();
    String testId = testData.getId();
    LOGGER.debug(new StringBuffer().append("key :").append(testId).toString());
    // url with query params
    String requestUri = requestData.getUri();
    String queryParamsWithDelimiter =
        requestData.getQueryParams() == null
            ? new String()
            : queryParamDelimiter + requestData.getQueryParams();
    String urlWithQueryParams =
        new StringBuffer().append(requestUri).append(queryParamsWithDelimiter).toString();

    // headers
    Map<String, String> headers =
        requestData.getHeaders() != null ? requestData.getHeaders() : new HashMap<>();
    headers.remove(contentLengthHeader);
    headers.put(GlobalConstants.TEST_HEADER, testId);
    headers.put(GlobalConstants.MODE_HEADER, Mode.TEST.name());

    // body & method
    String httpMethod = requestData.getMethod().toUpperCase();

    Response response = null;
    switch (httpMethod) {
      case "GET":
        response = httpHelper.doGet(clientId, urlWithQueryParams, headers);
        break;
      case "POST":
        response = httpHelper.doPost(clientId, urlWithQueryParams, headers, requestData.getBody());
        break;
      case "PUT":
        response = httpHelper.doPut(clientId, urlWithQueryParams, headers, requestData.getBody());
        break;
      case "DELETE":
        response = httpHelper.doDelete(clientId, urlWithQueryParams, headers);
        break;
      default:
        throw new IllegalStateException("Unsupported Http Method: " + httpMethod);
    }
    logExternalCall(response, urlWithQueryParams, clientId, testId);
  }

  private void logExternalCall(
      Response response, String urlWithQueryParams, String clientId, String testId) {
    LOGGER.info(
        String.format(
            "made an external call to uri: %s with clientId: %s for testId: %s. "
                + "Response received: %d",
            urlWithQueryParams,
            clientId,
            testId,
            response != null ? response.getStatusCode() : -1));
  }
}
