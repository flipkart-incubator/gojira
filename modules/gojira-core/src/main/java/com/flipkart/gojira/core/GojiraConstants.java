package com.flipkart.gojira.core;

import com.flipkart.gojira.models.TestDataType;
import com.flipkart.gojira.models.http.HttpTestDataType;
import com.flipkart.gojira.models.kafka.KafkaTestDataType;
import com.flipkart.gojira.models.rmq.RMQTestDataType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author parth.shyara, file created on 03/04/20.
 */
public class GojiraConstants {
    public static final String TEST_HEADER = "X-GOJIRA-ID";
    public static final String HTTP_TEST_DATA_TYPE = "HTTP";
    public static final String KAFKA_TEST_DATA_TYPE = "KAFKA";
    public static final String RMQ_TEST_DATA_TYPE = "RMQ";

    public static final Map<String, Class<? extends TestDataType>> TEST_DATA_TYPE_STRING_TO_CLASS = Collections.unmodifiableMap(
            new HashMap<String, Class<? extends TestDataType>>(){{
                put(HTTP_TEST_DATA_TYPE, HttpTestDataType.class);
                put(KAFKA_TEST_DATA_TYPE, KafkaTestDataType.class);
                put(RMQ_TEST_DATA_TYPE, RMQTestDataType.class);
            }}
    );
}
