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

import static com.flipkart.gojira.core.GojiraConstants.TEST_HEADER;

import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.models.TestResponseData;
import com.flipkart.gojira.requestsampling.RequestSamplingRepository;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is expected to provide an interface for implementing different logic for different
 * {@link Mode} during request and response capture.
 */
public abstract class HttpFilterHandler {

  private RequestSamplingRepository requestSamplingRepository;

  HttpFilterHandler(RequestSamplingRepository requestSamplingRepository) {
    this.requestSamplingRepository = requestSamplingRepository;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpFilterHandler.class);

  /**
   * Expected to call {@link DefaultProfileOrTestHandler#start(String, TestRequestData)} as per
   * {@link Mode} needs.
   *
   * <p>TODO: Can the call to {@link DefaultProfileOrTestHandler#start(String, TestRequestData)} be
   * made here itself?
   *
   * @param request wrapped original http request as a {@link
   *     HttpFilter.CustomHttpServletRequestWrapper} object
   * @return true if {@link FilterChain#doFilter(ServletRequest, ServletResponse)} should be called,
   *     else false.
   */
  protected abstract boolean preFilter(HttpFilter.CustomHttpServletRequestWrapper request);

  /**
   * Wrapper method for {@link FilterChain#doFilter(ServletRequest, ServletResponse)} internally.
   *
   * @param request wrapped original http request as a {@link
   *     HttpFilter.CustomHttpServletRequestWrapper} object
   * @param response wrapped original http response as a {@link
   *     HttpFilter.CustomHttpServletRequestWrapper} object
   * @param chain original {@link FilterChain} object
   * @throws IOException if an input or output exception occurred
   * @throws ServletException Simply invokes {@link FilterChain#doFilter(ServletRequest,
   *     ServletResponse)}
   */
  protected final void filter(
      HttpFilter.CustomHttpServletRequestWrapper request,
      HttpFilter.TestServletResponseWrapper response,
      FilterChain chain)
      throws IOException, ServletException {
    chain.doFilter(request, response);
  }

  /**
   * Executes the logic post getting the response from the subsequent channel. Expected to call
   * {@link DefaultProfileOrTestHandler#end(TestResponseData)} as per {@link Mode} needs and call
   * {@link javax.servlet.ServletOutputStream#write(byte[])} of {@link
   * javax.servlet.http.HttpServletResponse} by getting byte[] from {@link
   * HttpFilter.TestServletResponseWrapper} TODO: Can the below two be taken care of here itself?
   *
   * @param request wrapped original http request as a {@link
   *     HttpFilter.CustomHttpServletRequestWrapper} object
   * @param respWrapper wrapped original http response as a {@link
   *     HttpFilter.TestServletResponseWrapper} object
   * @param response original http response as a {@link HttpFilter.CustomHttpServletRequestWrapper}
   *     object
   * @throws IOException if an input or output exception occurred
   */
  protected abstract void postFilter(
      HttpFilter.CustomHttpServletRequestWrapper request,
      HttpFilter.TestServletResponseWrapper respWrapper,
      ServletResponse response)
      throws IOException;

  /**
   * Helper method to get headers.
   *
   * @param resWrapper wrapped original http response as a {@link
   *     HttpFilter.TestServletResponseWrapper} object
   * @return map of key-value pairs of headers which are part of the request.
   */
  protected final Map<String, String> getHeaders(HttpFilter.TestServletResponseWrapper resWrapper) {
    Collection<String> headerNames = resWrapper.getHeaderNames();
    Map<String, String> headersMap = new HashMap<>();
    if (headerNames != null) {
      for (String headerName : headerNames) {
        headersMap.put(headerName, resWrapper.getHeader(headerName));
      }
    }
    return headersMap;
  }

  /**
   * Extracts the test-id for gojira.
   *
   * @param request wrapped original http response as a {@link
   *     HttpFilter.TestServletResponseWrapper} object
   * @return test-id for gojira
   */
  protected final String getTestId(HttpFilter.CustomHttpServletRequestWrapper request) {
    return request.getHeader(TEST_HEADER);
  }

  /**
   * Uses the sampling configuration to determine if the URL is whitelisted or not for running in
   * various {@link Mode}.
   *
   * @param uri incoming http request uri
   * @param method incoming http request method.
   * @return true if url is among whitelisted url list else false.
   */
  protected final boolean isWhitelistedUrl(String uri, String method) {
    List<Pattern> whitelistedUris = requestSamplingRepository.getWhitelist();
    for (Pattern whitelistedUri : whitelistedUris) {
      if (whitelistedUri.matcher(method + " " + uri).matches()) {
        return true;
      }
    }
    LOGGER.info(
        String.format(
            "uri: %s and method: %s is not whitelisted for Gojira... Hence ignoring!",
            uri, method));
    return false;
  }
}
