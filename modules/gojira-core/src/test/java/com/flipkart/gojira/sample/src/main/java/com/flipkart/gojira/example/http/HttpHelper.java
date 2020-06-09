package com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.http;

import com.flipkart.gojira.core.annotations.ProfileOrTest;
import com.squareup.okhttp.*;

import javax.inject.Inject;
import java.io.IOException;

/**
 * @author narendra.vardi
 */
public class HttpHelper implements IHttpHelper {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;

    @Inject
    public HttpHelper(final OkHttpClient client) {
        this.client = client;
    }

    @Override
    @ProfileOrTest
    public String doGet(String url) throws HttpException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            throw new HttpException();
        }
    }

    @Override
    @ProfileOrTest
    public String doPost(String url, String payload) throws HttpException {
        RequestBody body = RequestBody.create(JSON, payload);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            throw new HttpException();
        }

    }

    @Override
    public String doPut(String url, String payload) throws HttpException {
        return null; // not implementing these methods because this is a sample project
    }

    @Override
    public String doDelete(String url) throws HttpException {
        return null; // not implementing these methods because this is a sample project
    }
}
