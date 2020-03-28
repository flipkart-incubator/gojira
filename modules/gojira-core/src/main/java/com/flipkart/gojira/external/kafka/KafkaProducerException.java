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

package com.flipkart.gojira.external.kafka;

import com.flipkart.gojira.execute.TestExecutionException;
import com.flipkart.gojira.models.kafka.KafkaTestDataType;

/**
 * Exception thrown if we are not able initiate execution for {@link KafkaTestDataType}
 */
public class KafkaProducerException extends TestExecutionException {

  public KafkaProducerException(String message) {
    super(message);
  }

  public KafkaProducerException(String message, Throwable cause) {
    super(message, cause);
  }
}
