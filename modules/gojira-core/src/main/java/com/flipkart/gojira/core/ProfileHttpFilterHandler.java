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

package com.flipkart.gojira.core;

import com.flipkart.gojira.models.ProfileData;
import com.flipkart.gojira.models.TestResponseData;
import com.flipkart.gojira.models.http.HttpTestRequestData;
import com.flipkart.gojira.models.http.HttpTestResponseData;
import com.flipkart.gojira.serde.TestSerdeException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.flipkart.gojira.core.FilterConstants.TEST_HEADER;

/** Implementation of {@link HttpFilterHandler} for mode {@link Mode#PROFILE} */
public class ProfileHttpFilterHandler extends HttpFilterHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProfileHttpFilterHandler.class);

  /**
   * Helper method to get request headers from
   *
   * @param request request wrapped original http request as a {@link
   *     HttpFilter.CustomHttpServletRequestWrapper} object
   * @return headers as a map with key as string and value as string
   * @throws TestSerdeException
   */
  private static Map<String, String> getHeaders(HttpFilter.CustomHttpServletRequestWrapper request)
      throws TestSerdeException {
    Enumeration<String> headerNames = request.getHeaderNames();
    Map<String, String> headersMap = new HashMap<>();
    if (headerNames != null) {
      while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        headersMap.put(headerName, request.getHeader(headerName));
      }
    }
    return headersMap;
  }

  /**
   * Gets the test-id from the request to check that it is null.
   *
   * <p>Checks if URI is whitelisted by calling {@link HttpFilterHandler#isWhitelistedURL(String,
   * String)}
   *
   * <p>If whitelisted, makes a copy of {@link
   * HttpFilter.CustomHttpServletRequestWrapper#getInputStream()}. On error, marks {@link
   * ProfileData#} as {@link ProfileState#FAILED} and returns true enable {@link HttpFilter} to call
   * {@link javax.servlet.FilterChain#doFilter(ServletRequest, ServletResponse)}.
   *
   * <p>If successful, adds the required HTTP parameters for executing a call later and adds them to
   * {@link HttpTestRequestData}.
   *
   * @param request wrapped original http request as a {@link
   *     HttpFilter.CustomHttpServletRequestWrapper} object
   * @return boolean true if {@link FilterChain#doFilter(ServletRequest, ServletResponse)} should be
   *     called, else false.
   */
  @Override
  public boolean preFilter(HttpFilter.CustomHttpServletRequestWrapper request) {
    String id = getTestId(request);
    if (id != null) {
      LOGGER.error(
          "Header with name: "
              + TEST_HEADER
              + " present. But service is running in "
              + ProfileRepository.getMode()
              + " mode.");
      throw new RuntimeException(
          "Header with name: "
              + TEST_HEADER
              + " present. But service is running in "
              + ProfileRepository.getMode()
              + " mode.");
    }
    if (isWhitelistedURL(request.getRequestURI(), request.getMethod())) {
      byte[] body;
      try {
        body = IOUtils.toByteArray(request.getInputStream());
      } catch (IOException ex) {
        LOGGER.error("Unable to read the stream", ex);
        return true;
      }
      try {
        HttpTestRequestData requestData =
            HttpTestRequestData.builder()
                .setBody(body)
                .setHeaders(getHeaders(request))
                .setMethod(request.getMethod())
                .setQueryParams(request.getQueryString())
                .setUri(request.getRequestURI())
                .build();
        id = String.valueOf(System.nanoTime()) + Thread.currentThread().getId();
        LOGGER.info(
            String.format(
                "Gojira generated testId %s for the API call: %s", id, request.getRequestURI()));
        DefaultProfileOrTestHandler.start(id, requestData);
      } catch (Exception e) {
        LOGGER.error("Error trying to construct servelet request");
      }
    }
    return true;
  }

  /**
   * Calls {@link DefaultProfileOrTestHandler#end(TestResponseData)} as per {@link Mode} needs and
   * calls {@link javax.servlet.ServletOutputStream#write(byte[])} of {@link
   * javax.servlet.http.HttpServletResponse} by getting byte[] from {@link
   * HttpFilter.TestServletResponseWrapper}
   *
   * <p>If URL is whitelisted, adds the HTTP response data needed for comparison later during
   * execution and adds them to {@link HttpTestResponseData}.
   *
   * <p>On failure, marks {@link ProfileData#getProfileState()} as {@link ProfileState#FAILED}
   *
   * <p>In finally block, {@link DefaultProfileOrTestHandler#end(TestResponseData)} is called.
   *
   * @param request wrapped original http request as a {@link
   *     HttpFilter.CustomHttpServletRequestWrapper} object
   * @param respWrapper wrapped original http response as a {@link
   *     HttpFilter.TestServletResponseWrapper} object
   * @param response original http response as a {@link HttpFilter.CustomHttpServletRequestWrapper}
   *     object
   * @throws IOException
   */
  @Override
  protected void postFilter(
      HttpFilter.CustomHttpServletRequestWrapper request,
      HttpFilter.TestServletResponseWrapper respWrapper,
      ServletResponse response)
      throws IOException {
    byte[] outputBuffer = respWrapper.getBuffer();
    response.getOutputStream().write(outputBuffer);
    HttpTestResponseData responseData = null;
    try {
      if (isWhitelistedURL(request.getRequestURI(), request.getMethod())) {
        responseData =
            HttpTestResponseData.builder()
                .setBody(outputBuffer)
                .setHeaders(getHeaders(respWrapper))
                .setStatusCode(respWrapper.getStatus())
                .build();
      }
    } catch (Exception e) {
      ProfileRepository.setProfileState(ProfileState.FAILED);
      LOGGER.warn("error creating HttpTestRequestData.", e);
    } finally {
      try {
        DefaultProfileOrTestHandler.end(responseData);
      } catch (Exception e) {
        LOGGER.warn("error ending profiling.", e);
      }
    }
  }
}
