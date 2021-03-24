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

package com.flipkart.gojira.core;

import com.flipkart.gojira.models.TestDataType;
import com.flipkart.gojira.models.http.HttpTestDataType;
import com.flipkart.gojira.models.kafka.KafkaTestDataType;
import com.flipkart.gojira.models.rmq.RmqTestDataType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GlobalConstants {
  // empty sting
  public static final String EMPTY_STRING = "";
  public static final String NULL_ENTRY_STRING = "NULL_ENTRY";

  // test header constants
  public static final String TEST_HEADER = "X-GOJIRA-ID";
  public static final String MODE_HEADER = "X-GOJIRA-MODE";

  // test data type constants
  public static final String HTTP_TEST_DATA_TYPE = "HTTP";
  public static final String KAFKA_TEST_DATA_TYPE = "KAFKA";
  public static final String RMQ_TEST_DATA_TYPE = "RMQ";
  public static final Map<String, Class<? extends TestDataType>> TEST_DATA_TYPE_STRING_TO_CLASS =
      Collections.unmodifiableMap(
          new HashMap<String, Class<? extends TestDataType>>() {
            {
              put(HTTP_TEST_DATA_TYPE, HttpTestDataType.class);
              put(KAFKA_TEST_DATA_TYPE, KafkaTestDataType.class);
              put(RMQ_TEST_DATA_TYPE, RmqTestDataType.class);
            }
          });

  // test result constants
  public static final String RESULT_SUCCESS = "SUCCESS";
  public static final String NON_EMPTY_METHOD_DATA_MAP = "NON_EMPTY_METHOD_DATA_MAP";
  public static final String READ_FAILURE = "READ_FAILED";
  public static final String COMPARE_FAILED = "COMPARE_FAILED";
  public static final String UNKNOWN_FAILED = "UNKNOWN_FAILED";

  // Http constants
  public static final String QUERY_PARAMS_DELIMITER = "?";
  public static final String HEADER_CONTENT_LENGTH = "Content-Length";
}
