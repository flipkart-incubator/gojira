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
import com.flipkart.gojira.sample.app.http.ISampleAppHttpHelper;
import com.flipkart.gojira.sample.app.http.SampleAppHttpException;
import com.google.inject.Inject;
import com.squareup.okhttp.Headers;
import java.util.Collections;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This API is used for testing the GET request.
 */
@Path("/github")
@Produces(MediaType.APPLICATION_JSON)
public class SampleAppGithubResource {
  private final ISampleAppHttpHelper httpHelper;
  private final ObjectMapper objectMapper;

  private static final String GITHUB_URL = "https://api.github.com/users/flipkart-incubator";

  @Inject
  public SampleAppGithubResource(ISampleAppHttpHelper httpHelper, ObjectMapper objectMapper) {
    this.httpHelper = httpHelper;
    this.objectMapper = objectMapper;
  }

  /**
   * Sample API which simply calls https://api.github.com/users/flipkart-incubator
   * 1. Calls GET https://api.github.com/users/flipkart-incubator using
   * {@link com.flipkart.gojira.sample.app.http.SampleAppHttpHelper} instance created via Guice
   * to ensure method interception works.
   * 2. Returns  the response.
   * @return returns {@link Response}
   * @throws Exception exception
   */
  @GET
  @Path("/usersFlipkartIncubator")
  public Response getGithubUserMeta() throws Exception {
    try {
      // unit of work. Note that the method is invoked on an instance which is created by Guice
      // which ensures that method interception would work.
      String metaInfo =
          httpHelper.doGet(
              GITHUB_URL,
              Headers.of(Collections.emptyMap()));

      // return response.
      return Response.ok(metaInfo).build();
    } catch (SampleAppHttpException e) {
      throw new Exception("GET from github failed!");
    }
  }
}
