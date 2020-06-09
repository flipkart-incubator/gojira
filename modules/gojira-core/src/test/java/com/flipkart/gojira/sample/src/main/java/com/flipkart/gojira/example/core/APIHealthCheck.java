package com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.core;

import com.codahale.metrics.health.HealthCheck;

public class APIHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
