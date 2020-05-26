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

package com.flipkart.gojira.serde.handlers.json.custom;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Serializers {
  /** Custom Serializer for List. */
  public static class TestListSerializer extends JsonSerializer<List> {

    @Override
    public void serialize(List value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      String listType = value.getClass().getName();
      gen.writeStartArray();
      gen.writeString(listType);
      if (!value.isEmpty()) {
        for (Object element : value) {
          if (element != null) {
            gen.writeObject(element.getClass().getName());
            gen.writeObject(element);
          }
        }
      }
      gen.writeEndArray();
    }
  }

  /** Custom Serializer for Map. */
  public static class TestMapSerializer extends JsonSerializer<Map> {

    @Override
    public void serialize(Map value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      String mapType = value.getClass().getName();
      gen.writeStartArray();
      gen.writeStartObject();
      gen.writeStringField("TestMapSerializer|mapType", mapType);
      gen.writeEndObject();
      if (!value.isEmpty()) {
        int size = value.size();
        for (int i = 0; i < size; i++) {
          if (value.keySet().toArray()[i] != null
              && value.get(value.keySet().toArray()[i]) != null) {
            gen.writeStartObject();
            gen.writeStringField(
                "TestMapSerializer|mapElementKeyType",
                value.keySet().toArray()[i].getClass().getName());
            gen.writeEndObject();
            gen.writeStartObject();
            gen.writeStringField(
                "TestMapSerializer|mapElementValueType",
                value.get(value.keySet().toArray()[i]).getClass().getName());
            gen.writeEndObject();
            gen.writeStartObject();
            gen.writeObjectField("mapElementKey", value.keySet().toArray()[i]);
            gen.writeObjectField("mapElementValue", value.get(value.keySet().toArray()[i]));
            gen.writeEndObject();
          }
        }
      }
      gen.writeEndArray();
    }
  }
}
