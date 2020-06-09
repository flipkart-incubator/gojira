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

import com.flipkart.gojira.models.TestResponseData;
import com.flipkart.gojira.requestsampling.RequestSamplingRepository;
import com.google.inject.Inject;
import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link HttpFilterHandler} for mode {@link Mode#NONE}.
 */
public class NoneHttpFilterHandler extends HttpFilterHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(NoneHttpFilterHandler.class);

  // todo: assisted inject?
  @Inject
  public NoneHttpFilterHandler(RequestSamplingRepository requestSamplingRepository) {
    super(requestSamplingRepository);
  }

  /**
   * Get's the test-id and throws an exception if test-header is present.
   *
   * @param request wrapped original http request as a {@link
   *     HttpFilter.CustomHttpServletRequestWrapper} object
   * @return true if {@link javax.servlet.FilterChain#doFilter(ServletRequest, ServletResponse)}
   *     needs to be called, else false.
   */
  @Override
  public boolean preFilter(HttpFilter.CustomHttpServletRequestWrapper request) {
    String id = super.getTestId(request);
    if (id != null) {
      LOGGER.error(
          "Header with name: "
              + TEST_HEADER
              + " present. But service is running in "
              + " NONE mode.");
      throw new RuntimeException(
          "Header with name: "
              + TEST_HEADER
              + " present. But service is running in "
              + " NONE mode.");
    }
    // TODO: Check if this needs to be done.
    DefaultProfileOrTestHandler.start(null, null, Mode.NONE);
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Calls {@link DefaultProfileOrTestHandler#end(TestResponseData)} as per {@link Mode} needs
   * and calls {@link javax.servlet.ServletOutputStream#write(byte[])} of {@link
   * javax.servlet.http.HttpServletResponse} by getting byte[] from {@link
   * HttpFilter.TestServletResponseWrapper}
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
    // TODO: Check if this needs to be done.
    DefaultProfileOrTestHandler.end(null);
  }
}
