package com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.http.HttpException;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.http.IHttpHelper;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.models.HttpPostSampleData;
import com.google.inject.Inject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author narendra.vardi
 * This API is used for testing the GET request
 */
@Path("/httpbin")
@Produces(MediaType.APPLICATION_JSON)
public class HttpBinPostResource {
    private final IHttpHelper httpHelper;
    private final ObjectMapper objectMapper;

    @Inject
    public HttpBinPostResource(IHttpHelper httpHelper, ObjectMapper objectMapper) {
        this.httpHelper = httpHelper;
        this.objectMapper = objectMapper;
    }

    @POST
    @Path("/post")
    public Response getGithubUserMeta() throws Exception {
        try {
            String postData = objectMapper.writeValueAsString(new HttpPostSampleData());
            String responseData = httpHelper.doPost("https://httpbin.org/post", postData);
            return Response.ok(responseData).build();
        } catch (HttpException e) {
            throw new Exception("Post to http bin failed!");
        }
    }
}
