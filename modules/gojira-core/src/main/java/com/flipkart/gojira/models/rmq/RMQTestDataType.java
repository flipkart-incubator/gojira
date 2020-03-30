package com.flipkart.gojira.models.rmq;


import com.flipkart.gojira.models.TestDataType;

/**
 * Extends {@link TestDataType} to indicate RMQ data.
 */
public class RMQTestDataType extends TestDataType {
    @Override
    public String getType() {
        return "RMQ";
    }
}
