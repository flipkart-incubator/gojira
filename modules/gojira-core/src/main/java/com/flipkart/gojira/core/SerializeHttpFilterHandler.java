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

import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.models.TestResponseData;
import com.flipkart.gojira.requestsampling.RequestSamplingRepository;
import com.google.inject.Inject;
import java.io.IOException;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link HttpFilterHandler} for mode {@link Mode#SERIALIZE}.
 */
public class SerializeHttpFilterHandler extends HttpFilterHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(SerializeHttpFilterHandler.class);

  @Inject
  public SerializeHttpFilterHandler(RequestSamplingRepository requestSamplingRepository) {
    super(requestSamplingRepository);
  }

  /**
   * Gets the test id and validates that it is not null.
   *
   * <p>If the URL is whitelisted, begins execution by calling {@link
   * DefaultProfileOrTestHandler#start(String, TestRequestData, Mode)}
   *
   * <p>returns true if whitelisted else false.
   *
   * @param request wrapped original http request as a {@link
   *     HttpFilter.CustomHttpServletRequestWrapper} object
   * @return true if whitelisted else false
   */
  @Override
  public boolean preFilter(HttpFilter.CustomHttpServletRequestWrapper request) {
    String id = super.getTestId(request);
    if (id == null) {
      LOGGER.error("X-GOJIRA-ID header not present but the service is running in SERIALIZE mode.");
      throw new RuntimeException(
          "X-GOJIRA-ID header not present but the service is running in SERIALIZE mode.");
    }
    boolean whitelisted = isWhitelistedUrl(request.getRequestURI(), request.getMethod());
    if (whitelisted) {
      DefaultProfileOrTestHandler.start(id, null, Mode.SERIALIZE);
    }
    return whitelisted;
  }

  /**
   * Calls {@link DefaultProfileOrTestHandler#end(TestResponseData)} as per {@link Mode} needs and
   * calls {@link javax.servlet.ServletOutputStream#write(byte[])} of {@link
   * javax.servlet.http.HttpServletResponse} by getting byte[] from {@link
   * HttpFilter.TestServletResponseWrapper}.
   *
   * <p>If URL is whitelisted, finishes execution by calling {@link
   * DefaultProfileOrTestHandler#end(TestResponseData)}
   *
   * @param request wrapped original http request as a {@link
   *     HttpFilter.CustomHttpServletRequestWrapper} object
   * @param respWrapper wrapped original http response as a {@link
   *     HttpFilter.TestServletResponseWrapper} object
   * @param response original http response as a {@link HttpFilter.CustomHttpServletRequestWrapper}
   *     object
   * @throws IOException if an input or output exception occurred
   */
  @Override
  protected void postFilter(
      HttpFilter.CustomHttpServletRequestWrapper request,
      HttpFilter.TestServletResponseWrapper respWrapper,
      ServletResponse response)
      throws IOException {
    byte[] outputBuffer = respWrapper.getBuffer();
    response.getOutputStream().write(outputBuffer);
    if (isWhitelistedUrl(request.getRequestURI(), request.getMethod())) {
      DefaultProfileOrTestHandler.end(null);
    }
  }
}
