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

import com.squareup.okhttp.Headers;

/**
 * Interface which can implemented in order to make external client calls. Ignoring headers and
 * params since this is a sample project
 */
public interface ISampleAppHttpHelper {
  String doGet(String url, Headers headers) throws SampleAppHttpException;

  String doPost(String url, String payload, Headers headers) throws SampleAppHttpException;

  String doPut(String url, String payload, Headers headers) throws SampleAppHttpException;

  String doDelete(String url, Headers headers) throws SampleAppHttpException;
}
