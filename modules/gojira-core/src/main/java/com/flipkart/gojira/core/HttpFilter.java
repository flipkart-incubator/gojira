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

import static com.flipkart.gojira.core.GojiraConstants.MODE_HEADER;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter implementation to capture http request and response data. Also responsible for starting
 * and ending the recording of data per request-response capture lifecycle.
 *
 * <p>Integrating client application is expected to chain this filter to the list of filters when
 * bootstrapping.
 */
public class HttpFilter implements Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpFilter.class);

  /**
   * Initializes a map of {@link Mode} specific filter handlers.
   */
  private static final Map<Mode, HttpFilterHandler> filterHashMap =
      Collections.unmodifiableMap(
          new HashMap<Mode, HttpFilterHandler>() {
            {
              put(Mode.NONE, new NoneHttpFilterHandler());
              put(Mode.PROFILE, new ProfileHttpFilterHandler());
              put(Mode.TEST, new TestHttpFilterHandler());
              put(Mode.SERIALIZE, new SerializeHttpFilterHandler());
            }
          });

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  /**
   * {@inheritDoc}
   *
   * <p>TODO: Add check for {@link HttpServletRequest}
   * @param request incoming request into a {@link CustomHttpServletRequestWrapper} and calls mode
   *     specific preFilter implementation. If {@link
   *     HttpFilterHandler#preFilter(CustomHttpServletRequestWrapper, Mode)} returns true, wraps the
   *     response object into a {@link TestServletResponseWrapper} before calling {@link
   *     HttpFilterHandler#filter(CustomHttpServletRequestWrapper, TestServletResponseWrapper,
   *     FilterChain)} in a try/finally block where {@link
   *     HttpFilterHandler#postFilter(CustomHttpServletRequestWrapper, TestServletResponseWrapper,
   *     ServletResponse)} is called in the finally block.
   * @param response outgoing response
   * @param chain invocation chain
   * @throws IOException if an input or output exception occurred
   * @throws ServletException If mode specific implementation is registered in the map, then simply
   *     calls {@link FilterChain#doFilter(ServletRequest, ServletResponse)}
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    // Wrapping the ServletRequest to make the input stream N times readable
    CustomHttpServletRequestWrapper requestWrapper =
        new CustomHttpServletRequestWrapper((HttpServletRequest) request);

    Mode requestMode = ProfileRepository.getRequestMode(requestWrapper.getHeader(MODE_HEADER));

    if (filterHashMap.containsKey(requestMode)) {
      if (filterHashMap.get(requestMode).preFilter(requestWrapper, requestMode)) {
        // Wrapping the ServletResponse to make the output stream readable
        TestServletResponseWrapper testServletResponseWrapper =
            new TestServletResponseWrapper((HttpServletResponse) response);
        try {
          filterHashMap
              .get(ProfileRepository.getMode())
              .filter(requestWrapper, testServletResponseWrapper, chain);
        } finally {
          filterHashMap
              .get(ProfileRepository.getMode())
              .postFilter(requestWrapper, testServletResponseWrapper, response);
        }
      }
    } else {
      LOGGER.error(
          "Processing logic not implemented for this mode: " + ProfileRepository.getMode());
      chain.doFilter(request, response);
    }
  }

  @Override
  public void destroy() {}

  /**
   * Wrapper class for {@link HttpServletResponseWrapper} Uses {@link
   * TestBufferedServletOutputStream} for keeping the response buffer.
   */
  public class TestServletResponseWrapper extends HttpServletResponseWrapper {

    private TestBufferedServletOutputStream bufferedServletOut =
        new TestBufferedServletOutputStream();
    private ServletOutputStream outputStream = null;

    /**
     * Created with reference to the original {@link HttpServletResponse} so that response can be
     * flushed into it's output stream.
     *
     * @param servletResponse original http servlet response which was passed in the {@link
     *     FilterChain#doFilter(ServletRequest, ServletResponse)} method.
     */
    public TestServletResponseWrapper(HttpServletResponse servletResponse) {
      super(servletResponse);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {}

    @Override
    public void sendError(int sc) throws IOException {}

    /**
     * Passes the reference of bufferedServletOut stream to outputStream if outputStream is not
     * null.
     *
     * @return output stream
     * @throws IOException if an input or output exception occurred
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
      if (this.outputStream == null) {
        this.outputStream = this.bufferedServletOut;
      }
      return this.outputStream;
    }

    /**
     * Calls {@link ServletOutputStream#flush()} on outputStream if not null.
     *
     * @throws IOException if an input or output exception occurred
     */
    @Override
    public void flushBuffer() throws IOException {
      if (this.outputStream != null) {
        this.outputStream.flush();
      }
    }

    /**
     * Helper method to return the byte[] from bufferedServletOut.
     *
     * @return the byte[] from bufferedServletOut.
     */
    public byte[] getBuffer() {
      return this.bufferedServletOut.getBuffer();
    }
  }

  /**
   * Wrapper class for {@link ServletOutputStream}. Uses a {@link ByteArrayOutputStream} as the
   * underlying {@link java.io.OutputStream}.
   */
  private class TestBufferedServletOutputStream extends ServletOutputStream {

    private ByteArrayOutputStream bos = new ByteArrayOutputStream();

    public void write(int data) {
      this.bos.write(data);
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {}

    /**
     * Helper method to get the stored byte array.
     *
     * @return byte[] from {@value bos} TODO: To be called only once. Need to do {@link
     *     ByteArrayOutputStream#reset()} if it needs to be called multiple times.
     */
    public byte[] getBuffer() {
      return this.bos.toByteArray();
    }
  }

  /**
   * Wrapper class for {@link HttpServletRequestWrapper} Uses {@link ByteArrayInputStream} for
   * keeping request buffer.
   */
  public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    /**
     * Calls {@link IOUtils#toByteArray(java.io.InputStream)} to get a copy of byte[] from the
     * incoming request.
     *
     * @param request original http request instance reference passed from {@link
     *     FilterChain#doFilter(ServletRequest, ServletResponse)}
     */
    public CustomHttpServletRequestWrapper(HttpServletRequest request) {
      super(request);

      try {
        body = IOUtils.toByteArray(super.getInputStream());
      } catch (IOException ex) {
        throw new RuntimeException("Unable to read the stream", ex);
      }
    }

    /**
     * {@inheritDoc}
     *
     * <p>TODO: check isFinished, isReady and setReadListener.
     *
     * @return new instance of {@link ServletInputStream}
     * @throws IOException Uses a {@link ByteArrayInputStream} created with the body. This is used
     *     when {@link InputStream#read()} and {@link InputStream#available()} are called.
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {

      final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);

      ServletInputStream inputStream =
          new ServletInputStream() {
            public int read() throws IOException {
              return byteArrayInputStream.read();
            }

            @Override
            public int available() throws IOException {
              return byteArrayInputStream.available();
            }

            @Override
            public boolean isFinished() {
              return true;
            }

            @Override
            public boolean isReady() {
              return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {}
          };

      return inputStream;
    }
  }
}
