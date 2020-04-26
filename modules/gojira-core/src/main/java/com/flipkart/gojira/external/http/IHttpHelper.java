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

import java.util.Map;
import org.asynchttpclient.Response;

/**
 * Interface definition for HTTP calls. This interface is for async-http-client.
 */
public interface IHttpHelper {

  /**
   * Initiates a GET call with the below parameters.
   *
   * @param clientId identifier to fetch externalConfig
   * @param urlWithQueryParams http uri and query params
   * @param header headers as {@link Map}
   * @return the {@link Response} object.
   * @throws HttpCallException if we are not able to initiate execution
   */
  Response doGet(String clientId, String urlWithQueryParams, Map<String, String> header)
      throws HttpCallException;

  /**
   * Initiates a POST call with the below parameters.
   *
   * @param clientId identifier to fetch externalConfig
   * @param urlWithQueryParams http uri and query params
   * @param header headers as {@link Map}
   * @param payload headers as {@link Map}
   * @return the {@link Response} object.
   * @throws HttpCallException if we are not able to initiate execution
   */
  Response doPost(
      String clientId, String urlWithQueryParams, Map<String, String> header, byte[] payload)
      throws HttpCallException;

  /**
   * Initiates a PUT call with the below parameters.
   *
   * @param clientId identifier to fetch externalConfig
   * @param urlWithQueryParams http uri and query params
   * @param header headers as {@link Map}
   * @param payload headers as {@link Map}
   * @return the {@link Response} object.
   * @throws HttpCallException if we are not able to initiate execution
   */
  Response doPut(
      String clientId, String urlWithQueryParams, Map<String, String> header, byte[] payload)
      throws HttpCallException;

  /**
   * Initiates a DELETE call with the below parameters.
   *
   * @param clientId identifier to fetch externalConfig
   * @param urlWithQueryParams http uri and query params
   * @param header headers as {@link Map}
   * @return the {@link Response} object.
   * @throws HttpCallException if we are not able to initiate execution
   */
  Response doDelete(String clientId, String urlWithQueryParams, Map<String, String> header)
      throws HttpCallException;
}
