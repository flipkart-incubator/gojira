package com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.http.HttpHelper;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.http.IHttpHelper;
import com.google.inject.AbstractModule;

/**
 * @author narendra.vardi
 */
public class ServiceSampleModule extends AbstractModule {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void configure() {
        bind(IHttpHelper.class).to(HttpHelper.class).asEagerSingleton();
        bind(ObjectMapper.class).toInstance(mapper);
        install(new GojiraModule(mapper));
    }
}
