package com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.http;

import java.util.Map;

/**
 * @author narendra.vardi
 * Interface which can implemented in order to make external client calls.
 * Ignoring headers and params since this is a sample project
 */
public interface IHttpHelper {
    String doGet(String url) throws HttpException;

    String doPost(String url, String payload) throws HttpException;

    String doPut(String url, String payload) throws HttpException;

    String doDelete(String url) throws HttpException;
}
