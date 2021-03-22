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
import com.flipkart.gojira.serde.TestSerdeException;
import com.flipkart.gojira.serde.handlers.TestSerdeHandler;
import com.flipkart.gojira.serde.handlers.TypeParameter;
import com.flipkart.gojira.serde.handlers.json.custom.Deserializers.TestListDeserializer;
import com.flipkart.gojira.serde.handlers.json.custom.Deserializers.TestMapDeserializer;
import com.flipkart.gojira.serde.handlers.json.custom.Serializers.TestListSerializer;
import com.flipkart.gojira.serde.handlers.json.custom.Serializers.TestMapSerializer;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link TestSerdeHandler} with serialization and deserialization support for
 * {@link Map} and {@link List} like classes with any type of elements provided that they are
 * serializable.
 */
public class JsonMapListSerdeHandler extends JsonDefaultTestSerdeHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(JsonMapListSerdeHandler.class);

  /**
   * In this public constructor, custom serializers and deserializers are registered against {@link
   * Map} and {@link List} like type.
   */
  public JsonMapListSerdeHandler() {
    super.registerSerializer(List.class, new TestListSerializer())
        .registerDeSerializer(List.class, new TestListDeserializer())
        .registerSerializer(Map.class, new TestMapSerializer())
        .registerDeSerializer(Map.class, new TestMapDeserializer());
  }

  @Override
  public <T> byte[] serialize(T obj) throws TestSerdeException {
    try {
      return mapper.writeValueAsBytes(obj);
    } catch (JsonProcessingException e) {
      LOGGER.trace("error serializing data. class: " + obj.getClass(), e);
      throw new TestSerdeException("error serializing data. class: " + obj.getClass(), e);
    }
  }

  @Override
  public <T> T deserialize(byte[] bytes, Class<T> clazz) throws TestSerdeException {
    try {
      if (List.class.isAssignableFrom(clazz)) {
        return mapper.readValue(bytes, (Class<T>) List.class);
      }
      if (Map.class.isAssignableFrom(clazz)) {
        return mapper.readValue(bytes, (Class<T>) Map.class);
      }
      return mapper.readValue(bytes, clazz);
    } catch (IOException e) {
      LOGGER.trace("error de-serializing data. class: " + clazz.toGenericString(), e);
      throw new TestSerdeException(
          "error de-serializing data. class: " + clazz.toGenericString(), e);
    }
  }

  @Override
  public <T> T deserialize(byte[] bytes, TypeParameter<T> typeParameter) throws TestSerdeException {
    try {
      return mapper.readValue(bytes, mapper.constructType(typeParameter.getType()));
    } catch (IOException e) {
      LOGGER.error("error de-serializing data. type: " + typeParameter, e);
      throw new TestSerdeException("error de-serializing data. type: " + typeParameter, e);
    }
  }

  @Override
  public <T> void deserializeToInstance(byte[] bytes, T obj) throws TestSerdeException {
    try {
      if (List.class.isAssignableFrom(obj.getClass())) {
        ((List) obj).clear();
        List tmpList = mapper.readValue(bytes, List.class);
        ((List) obj).addAll(tmpList);
      } else if (Map.class.isAssignableFrom(obj.getClass())) {
        ((Map) obj).clear();
        Map tmpMap = mapper.readValue(bytes, Map.class);
        ((Map) obj).putAll(tmpMap);
      } else {
        mapper.readerForUpdating(obj).readValue(bytes);
      }
    } catch (IOException e) {
      LOGGER.trace("error updating object. class: " + obj.getClass(), e);
      throw new TestSerdeException("error updating object. class: " + obj.getClass(), e);
    }
  }
}
