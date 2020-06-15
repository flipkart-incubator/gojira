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

package com.flipkart.gojira.sample.app.http;

import com.flipkart.gojira.core.annotations.ProfileOrTest;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;

/**
 * Helper class for making external HTTP calls.
 */
public class SampleAppHttpHelper implements ISampleAppHttpHelper {
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  private final OkHttpClient client;

  @Inject
  public SampleAppHttpHelper(final OkHttpClient client) {
    this.client = client;
  }

  /**
   * Method to make GET calls. * Note that this method has {@link ProfileOrTest} annotation
   * added. So if this method is called on an object which is created using Guice/AspectJ and
   * profiling was initiated for this, then this method would be recorded or replayed.
   *
   * @param url external url to be called.
   * @param headers headers to be passed.
   * @return return data.
   * @throws SampleAppHttpException exception thrown in case of n/w call failure.
   */
  @Override
  @ProfileOrTest
  public String doGet(String url, Headers headers) throws SampleAppHttpException {
    Request request = new Request.Builder().url(url).headers(headers).build();
    try {
      Response response = client.newCall(request).execute();
      return response.body().string();
    } catch (IOException e) {
      throw new SampleAppHttpException();
    }
  }

  /**
   * Method to make POST calls.
   * Note that this method has {@link ProfileOrTest} annotation added. So if this method is called
   * on an object which is created using Guice/AspectJ and profiling was initiated for this, then
   * this method would be recorded or replayed.
   *
   * @param url external url to be called.
   * @param payload payload to be passed.
   * @param headers headers to be passed.
   * @return return data.
   * @throws SampleAppHttpException exception thrown in case of n/w call failure.
   */
  @Override
  @ProfileOrTest
  public String doPost(String url, String payload, Headers headers) throws SampleAppHttpException {
    RequestBody body = RequestBody.create(JSON, payload);
    Request request = new Request.Builder().url(url).post(body).headers(headers).build();
    try {
      Response response = client.newCall(request).execute();
      return response.body().string();
    } catch (IOException e) {
      throw new SampleAppHttpException();
    }
  }

  @Override
  public String doPut(String url, String payload, Headers headers) throws SampleAppHttpException {
    return null; // not implementing these methods because this is a sample project
  }

  @Override
  public String doDelete(String url, Headers headers) throws SampleAppHttpException {
    return null; // not implementing these methods because this is a sample project
  }
}
