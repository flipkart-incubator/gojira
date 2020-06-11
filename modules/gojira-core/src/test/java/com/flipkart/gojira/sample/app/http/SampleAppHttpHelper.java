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

public class SampleAppHttpHelper implements ISampleAppHttpHelper {
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  private final OkHttpClient client;

  @Inject
  public SampleAppHttpHelper(final OkHttpClient client) {
    this.client = client;
  }

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
