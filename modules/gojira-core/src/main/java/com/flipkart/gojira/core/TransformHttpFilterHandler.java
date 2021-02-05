package com.flipkart.gojira.core;

import com.flipkart.gojira.models.ExecutionData;
import com.flipkart.gojira.models.TestResponseData;
import com.flipkart.gojira.models.http.HttpTestRequestData;
import com.flipkart.gojira.models.http.HttpTestResponseData;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformHttpFilterHandler extends HttpFilterHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransformHttpFilterHandler.class);

  /**
   * Helper method to get request headers.
   *
   * @param request request wrapped original http request as a {@link
   *     HttpFilter.CustomHttpServletRequestWrapper} object
   * @return headers as a map with key as string and value as string
   */
  private static Map<String, String> getHeaders(
      HttpFilter.CustomHttpServletRequestWrapper request) {
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
   * <p>Checks if URI is whitelisted by calling {@link HttpFilterHandler#isWhitelistedUrl(String,
   * String)}
   *
   * <p>If whitelisted, makes a copy of {@link
   * HttpFilter.CustomHttpServletRequestWrapper#getInputStream()}. On error, marks {@link
   * ExecutionData#getProfileState()} as {@link ProfileState#FAILED} and returns true enable {@link
   * HttpFilter} to call {@link javax.servlet.FilterChain#doFilter(ServletRequest,
   * ServletResponse)}.
   *
   * <p>If successful, adds the required HTTP parameters for executing a call later and adds them to
   * {@link HttpTestRequestData}.
   *
   * @param request wrapped original http request as a {@link
   *     HttpFilter.CustomHttpServletRequestWrapper} object
   * @return true if {@link FilterChain#doFilter(ServletRequest, ServletResponse)} should be called,
   *     else false.
   */
  @Override
  public boolean preFilter(HttpFilter.CustomHttpServletRequestWrapper request) {
    String id = super.getTestId(request);
    if (id == null) {
      LOGGER.error("X-GOJIRA-ID header not present but the service is running in TRANSFORM mode.");
      throw new RuntimeException(
          "X-GOJIRA-ID header not present but the service is running in TRANSFORM mode.");
    }

    if (isWhitelistedUrl(request.getRequestURI(), request.getMethod())) {
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
        DefaultProfileOrTestHandler.start(id, requestData, Mode.TRANSFORM);
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
   * <p>On failure, marks {@link ExecutionData#getProfileState()} as {@link ProfileState#FAILED}
   *
   * <p>In finally block, {@link DefaultProfileOrTestHandler#end(TestResponseData)} is called.
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
    HttpTestResponseData responseData = null;
    try {
      if (isWhitelistedUrl(request.getRequestURI(), request.getMethod())) {
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
        LOGGER.warn("error ending transformation.", e);
      }
    }
  }
}
