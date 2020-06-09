package com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.http.HttpException;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.http.IHttpHelper;
import com.google.inject.Inject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author narendra.vardi
 * This API is used for testing the GET request
 */
@Path("/github")
@Produces(MediaType.APPLICATION_JSON)
public class GithubResource {
    private final IHttpHelper httpHelper;
    private final ObjectMapper objectMapper;

    @Inject
    public GithubResource(IHttpHelper httpHelper, ObjectMapper objectMapper) {
        this.httpHelper = httpHelper;
        this.objectMapper = objectMapper;
    }

    @GET
    @Path("/{name}")
    public Response getGithubUserMeta(@PathParam("name") String name) throws Exception {
        try {
            String metaInfo = httpHelper.doGet("https://api.github.com/users/flipkart-incubator");
            return Response.ok(metaInfo).build();
        } catch (HttpException e) {
            throw new Exception("Get to github failed!");
        }
    }
}
