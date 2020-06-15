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

package com.flipkart.gojira.sample.app.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gojira.core.ProfileRepository;
import com.flipkart.gojira.sample.app.http.ISampleAppHttpHelper;
import com.flipkart.gojira.sample.app.http.SampleAppHttpException;
import com.google.inject.Inject;
import com.squareup.okhttp.Headers;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This API is used for testing the POST request.
 * */
@Path("/httpbin")
@Produces(MediaType.APPLICATION_JSON)
public class SampleAppHttpBinPostResource {
  private final ISampleAppHttpHelper httpHelper;
  private final ObjectMapper objectMapper;

  private static final String HTTPBIN_DATA_URL = "https://httpbin.org/post";

  @Inject
  public SampleAppHttpBinPostResource(ISampleAppHttpHelper httpHelper, ObjectMapper objectMapper) {
    this.httpHelper = httpHelper;
    this.objectMapper = objectMapper;
  }

  /**
   * Sample API which simply calls https://httpbin.org/post
   * 1. Calls POST https://httpbin.org/post using instance of
   * {@link com.flipkart.gojira.sample.app.http.SampleAppHttpHelper} created via Guice for method
   * interception to work.
   * 2. Returns  the response.
   * @return returns {@link Response}
   * @throws Exception exception
   */
  @POST
  @Path("/post")
  public Response postHttpBinData() throws Exception {
    try {
      // setting id to API method name for ensuring same id in the test class and here.
      ProfileRepository.setTestDataId("postHttpBinData");

      // construct a complex object with some variation and do a POST request
      Map<Object, Object> httpPostSampleData = new HashMap<>();
      httpPostSampleData.put("hello", "world");
      Map<Object, Object> timeDetails = new HashMap<>();
      timeDetails.put("epochTime", System.currentTimeMillis());
      httpPostSampleData.put("timeDetails", timeDetails);

      // Note that the method is invoked on an instance which is created by Guice
      // which ensures that method interception would work.
      String responseData =
          httpHelper.doPost(
              HTTPBIN_DATA_URL,
              objectMapper.writeValueAsString(httpPostSampleData),
              Headers.of(Collections.emptyMap()));

      // return response
      return Response.ok(responseData).build();
    } catch (SampleAppHttpException e) {
      throw new Exception("POST to http bin failed!");
    }
  }
}
