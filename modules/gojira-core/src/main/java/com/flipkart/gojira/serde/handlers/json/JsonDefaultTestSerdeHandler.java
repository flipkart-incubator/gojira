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

package com.flipkart.gojira.serde.handlers.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;
import com.flipkart.gojira.serde.TestSerdeException;
import com.flipkart.gojira.serde.handlers.TestSerdeHandler;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic Implementation of {@link TestSerdeHandler}
 */
public class JsonDefaultTestSerdeHandler implements TestSerdeHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonDefaultTestSerdeHandler.class);
  private static final ObjectMapper mapper = new ObjectMapper()
          .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
          .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
          .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
          .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
          .setSubtypeResolver(new StdSubtypeResolver());

  /**
   * @param obj object to be serialized
   * @param <T>
   * @return
   * @throws TestSerdeException
   */
  @Override
  public <T> byte[] serialize(T obj) throws TestSerdeException {
    try {
      return mapper.writeValueAsBytes(obj);
    } catch (JsonProcessingException e) {
      LOGGER.error("error serializing data.", e);
      throw new TestSerdeException("error serializing data.", e);
    }
  }

  /**
   * @param bytes serialized byte[] to be de-serialized.
   * @param clazz class to de-serialize
   * @param <T>
   * @return
   * @throws TestSerdeException
   */
  @Override
  public <T> T deserialize(byte[] bytes, Class<T> clazz) throws TestSerdeException {
    try {
      return mapper.readValue(bytes, clazz);
    } catch (IOException e) {
      LOGGER.error("error de-serializing data.", e);
      throw new TestSerdeException("error de-serializing data.", e);
    }
  }

  /**
   * @param bytes serialized byte[] to be de-serialized
   * @param obj   object which needs to be updated with the above byte[]
   * @param <T>
   * @throws TestSerdeException
   */
  @Override
  public <T> void deserializeToInstance(byte[] bytes, T obj) throws TestSerdeException {
    try {
      mapper.readerForUpdating(obj).readValue(bytes);
    } catch (IOException e) {
      throw new TestSerdeException("error updating object.", e);
    }
  }
}
