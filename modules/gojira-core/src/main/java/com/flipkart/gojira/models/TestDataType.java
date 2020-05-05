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

package com.flipkart.gojira.models;

import static com.flipkart.gojira.core.GojiraConstants.HTTP_TEST_DATA_TYPE;
import static com.flipkart.gojira.core.GojiraConstants.KAFKA_TEST_DATA_TYPE;
import static com.flipkart.gojira.core.GojiraConstants.RMQ_TEST_DATA_TYPE;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.flipkart.gojira.models.http.HttpTestDataType;
import com.flipkart.gojira.models.kafka.KafkaTestDataType;
import com.flipkart.gojira.models.rmq.RmqTestDataType;

/**
 * Base class for different types of {@link TestData}.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = HttpTestDataType.class, name = HTTP_TEST_DATA_TYPE),
    @JsonSubTypes.Type(value = KafkaTestDataType.class, name = KAFKA_TEST_DATA_TYPE),
    @JsonSubTypes.Type(value = RmqTestDataType.class, name = RMQ_TEST_DATA_TYPE)
})
public abstract class TestDataType {

  public abstract String getType();
}
