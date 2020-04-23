package com.flipkart.gojira.models.rmq;


import com.flipkart.gojira.models.TestDataType;

import static com.flipkart.gojira.core.GojiraConstants.RMQ_TEST_DATA_TYPE;

/**
 * Extends {@link TestDataType} to indicate RMQ data.
 */
public class RMQTestDataType extends TestDataType {
    @Override
    public String getType() {
        return RMQ_TEST_DATA_TYPE;
    }
}
