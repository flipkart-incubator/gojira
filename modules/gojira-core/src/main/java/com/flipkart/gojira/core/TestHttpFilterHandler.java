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
import com.flipkart.gojira.models.http.HttpTestResponseData;
import java.io.IOException;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link HttpFilterHandler} for mode {@link Mode#TEST}.
 */
public class TestHttpFilterHandler extends HttpFilterHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestHttpFilterHandler.class);

  @Override
  public boolean preFilter(HttpFilter.CustomHttpServletRequestWrapper request) {
    String id = super.getTestId(request);
    if (id == null) {
      LOGGER.error("X-GOJIRA-ID header not present but the service is running in TEST mode.");
      throw new RuntimeException(
          "X-GOJIRA-ID header not present but the service is running in TEST mode.");
    }
    boolean whitelisted = isWhitelistedUrl(request.getRequestURI(), request.getMethod());
    if (whitelisted) {
      DefaultProfileOrTestHandler.start(id, null);
    }
    return whitelisted;
  }

  /**
   * Calls {@link DefaultProfileOrTestHandler#end(TestResponseData)} as per {@link Mode} needs and
   * calls {@link javax.servlet.ServletOutputStream#write(byte[])} of {@link
   * javax.servlet.http.HttpServletResponse} by getting byte[] from {@link
   * HttpFilter.TestServletResponseWrapper}.
   *
   * <p>If whitelisted, adds HTTP response data required for comparing to {@link
   * HttpTestResponseData}. On error, marks {@link ProfileData#profileState} as {@link
   * ProfileState#FAILED} and throws {@link RuntimeException}
   *
   * <p>In finally block {@link DefaultProfileOrTestHandler#end(TestResponseData)} is called to
   * complete test execution.
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
      HttpTestResponseData responseData = null;
      try {
        responseData =
            HttpTestResponseData.builder()
                .setHeaders(getHeaders(respWrapper))
                .setBody(outputBuffer)
                .setStatusCode(respWrapper.getStatus())
                .build();
      } catch (Exception e) {
        ProfileRepository.setProfileState(ProfileState.FAILED);
        LOGGER.error("error creating HttpTestResponseData.", e);
        throw new RuntimeException("error creating HttpTestResponseData.", e);
      } finally {
        DefaultProfileOrTestHandler.end(responseData);
      }
    }
  }
}
