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

package com.flipkart.gojira.external.http;

import com.flipkart.gojira.core.injectors.TestExecutionInjector;
import com.flipkart.gojira.external.ExternalConfigRepository;
import com.flipkart.gojira.external.config.ExternalConfig;
import com.flipkart.gojira.external.config.HttpConfig;
import com.flipkart.gojira.models.http.HttpTestDataType;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.asynchttpclient.util.HttpConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/** Helper class which implements {@link IHttpHelper} */
public class HttpHelper implements IHttpHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpHelper.class);

  /**
   * @param clientId identifier to fetch externalConfig
   * @param urlWithQueryParams http uri and query params
   * @param header headers as {@link Map}
   * @return returns the {@link Response} object.
   * @throws HttpCallException exception thrown if we are not able to initiate execution
   */
  @Override
  public Response doGet(String clientId, String urlWithQueryParams, Map<String, String> header)
      throws HttpCallException {
    RequestBuilder requestBuilder = new RequestBuilder().setMethod(HttpConstants.Methods.GET);

    for (Map.Entry<String, String> entry : header.entrySet()) {
      requestBuilder.addHeader(entry.getKey(), entry.getValue());
    }

    return execute(clientId, requestBuilder, urlWithQueryParams);
  }

  /**
   * @param clientId identifier to fetch externalConfig
   * @param urlWithQueryParams http uri and query params
   * @param header headers as {@link Map}
   * @param payload headers as {@link Map}
   * @return returns the {@link Response} object.
   * @throws HttpCallException exception thrown if we are not able to initiate execution
   */
  @Override
  public Response doPost(
      String clientId, String urlWithQueryParams, Map<String, String> header, byte[] payload)
      throws HttpCallException {
    RequestBuilder requestBuilder =
        new RequestBuilder().setMethod(HttpConstants.Methods.POST).setBody(payload);

    for (Map.Entry<String, String> entry : header.entrySet()) {
      requestBuilder.addHeader(entry.getKey(), entry.getValue());
    }

    return execute(clientId, requestBuilder, urlWithQueryParams);
  }

  /**
   * @param clientId identifier to fetch externalConfig
   * @param urlWithQueryParams http uri and query params
   * @param header headers as {@link Map}
   * @param payload headers as {@link Map}
   * @return returns the {@link Response} object.
   * @throws HttpCallException exception thrown if we are not able to initiate execution
   */
  @Override
  public Response doPut(
      String clientId, String urlWithQueryParams, Map<String, String> header, byte[] payload)
      throws HttpCallException {
    RequestBuilder requestBuilder =
        new RequestBuilder().setMethod(HttpConstants.Methods.PUT).setBody(payload);

    for (Map.Entry<String, String> entry : header.entrySet()) {
      requestBuilder.addHeader(entry.getKey(), entry.getValue());
    }

    return execute(clientId, requestBuilder, urlWithQueryParams);
  }

  /**
   * @param clientId identifier to fetch externalConfig
   * @param urlWithQueryParams http uri and query params
   * @param header headers as {@link Map}
   * @return returns the {@link Response} object.
   * @throws HttpCallException exception thrown if we are not able to initiate execution
   */
  @Override
  public Response doDelete(String clientId, String urlWithQueryParams, Map<String, String> header)
      throws HttpCallException {
    RequestBuilder requestBuilder = new RequestBuilder().setMethod(HttpConstants.Methods.DELETE);

    for (Map.Entry<String, String> entry : header.entrySet()) {
      requestBuilder.addHeader(entry.getKey(), entry.getValue());
    }

    return execute(clientId, requestBuilder, urlWithQueryParams);
  }

  /**
   * Gets an instance of {@link DefaultAsyncHttpClient} and {@link ExternalConfig} for the given
   * clientId. Builds the complete http url with protocol, hostname, port, uri and queryParams. Then
   * invokes {@link DefaultAsyncHttpClient#executeRequest(RequestBuilder)} and calls {@link
   * ListenableFuture#get()}
   *
   * <p>On error, throws a {@link HttpCallException}, else returns {@link Response}
   *
   * @param clientId identifier to fetch externalConfig
   * @param requestBuilder requestBuilder object
   * @param urlWithQueryParams http uri and query params
   * @return returns the {@link Response} object.
   * @throws HttpCallException exception thrown if we are not able to initiate execution
   */
  private Response execute(
      String clientId, RequestBuilder requestBuilder, String urlWithQueryParams)
      throws HttpCallException {
    DefaultAsyncHttpClient defaultAsyncHttpClient =
        TestExecutionInjector.getInjector().getInstance(IHttpManager.class).getClient(clientId);
    ExternalConfig clientConfig =
        TestExecutionInjector.getInjector()
            .getInstance(ExternalConfigRepository.class)
            .getExternalConfigFor(clientId, new HttpTestDataType());

    HttpConfig httpConfig = (HttpConfig) clientConfig;
    String externalCallUrl =
        new StringBuffer("http://")
            .append(httpConfig.getHostNamePort())
            .append(urlWithQueryParams)
            .toString();
    requestBuilder.setUrl(externalCallUrl);
    LOGGER.info(
        String.format(
            "making an external call to uri: %s with clientId: %s.", externalCallUrl, clientId));

    ListenableFuture<Response> responseListenableFuture =
        defaultAsyncHttpClient.executeRequest(requestBuilder);
    try {
      Response response = responseListenableFuture.get();
      return response;
    } catch (InterruptedException | ExecutionException e) {
      LOGGER.error("error executing http request: " + e);
      throw new HttpCallException("error executing http request.", e);
    }
  }
}
