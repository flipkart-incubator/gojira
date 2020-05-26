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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.gojira.serde.TestSerdeException;
import com.flipkart.gojira.serde.handlers.TestSerdeHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link TestSerdeHandler}.
 *
 * <p>Please use {@link JsonDefaultTestSerdeHandler} if required.
 */
@Deprecated
public class JsonTestSerdeHandler implements TestSerdeHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonTestSerdeHandler.class);

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final SimpleModule module = new SimpleModule();

  static {
    setUpMapperProperties(mapper);

    registerSerializer(List.class, new TestListSerializer());
    registerDeSerializer(List.class, new TestListDeserializer());

    registerSerializer(Map.class, new TestMapSerializer());
    registerDeSerializer(Map.class, new TestMapDeserializer());

    mapper.registerModule(module);
  }

  /**
   * Registers a JsonSerializer instance against a mentioned type.
   */
  public static synchronized <T> void registerSerializer(Class<T> type, JsonSerializer<T> ser) {
    module.addSerializer(type, ser);
    mapper.registerModule(module);

    TestMapSerializer.registerSerializer(type, ser);

    TestListDeserializer.registerSerializer(type, ser);
    TestMapDeserializer.registerSerializer(type, ser);
  }

  /**
   * Registers a JsonDeserializer instance against a mentioned type.
   */
  public static synchronized <T> void registerDeSerializer(
      Class<T> type, JsonDeserializer<T> deser) {
    module.addDeserializer(type, deser);
    mapper.registerModule(module);

    TestMapSerializer.registerDeserializer(type, deser);

    TestListDeserializer.registerDeserializer(type, deser);
    TestMapDeserializer.registerDeserializer(type, deser);
  }

  private static void setUpMapperProperties(ObjectMapper mapper) {
    mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    mapper.setSubtypeResolver(new StdSubtypeResolver());
  }

  @Override
  public <T> byte[] serialize(T obj) throws TestSerdeException {
    try {
      return mapper.writeValueAsBytes(obj);
    } catch (JsonProcessingException e) {
      LOGGER.error("error serializing data.", e);
      throw new TestSerdeException("error serializing data.", e);
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
      LOGGER.error("error de-serializing data. class: " + clazz.toGenericString(), e);
      throw new TestSerdeException(
          "error de-serializing data. class: " + clazz.toGenericString(), e);
    }
  }

  @Override
  public <T> void deserializeToInstance(byte[] bytes, T obj) throws TestSerdeException {
    try {
      mapper.readerForUpdating(obj).readValue(bytes);
    } catch (IOException e) {
      throw new TestSerdeException("error updating object.", e);
    }
  }

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

  public static class TestListDeserializer extends JsonDeserializer<List> {

    private static ObjectMapper mapper = new ObjectMapper();
    private static SimpleModule module = new SimpleModule();

    private static ObjectMapper recursiveMapper = new ObjectMapper();
    private static SimpleModule recursiveModule = new SimpleModule();

    static {
      setUpMapperProperties(mapper);
      setUpMapperProperties(recursiveMapper);

      module.addSerializer(Map.class, new TestMapSerializer());
      module.addDeserializer(Map.class, new TestMapDeserializer());

      // to support List<List<>>
      recursiveModule.addSerializer(List.class, new TestListSerializer());
      recursiveModule.addDeserializer(List.class, new TestListDeserializer());

      recursiveModule.addSerializer(Map.class, new TestMapSerializer());
      recursiveModule.addDeserializer(Map.class, new TestMapDeserializer());

      mapper.registerModule(module);
      recursiveMapper.registerModule(recursiveModule);
    }

    static synchronized <T> void registerSerializer(Class<T> type, JsonSerializer<T> ser) {
      module.addSerializer(type, ser);
      mapper.registerModule(module);

      recursiveModule.addSerializer(type, ser);
      mapper.registerModule(recursiveModule);
    }

    static synchronized <T> void registerDeserializer(Class<T> type, JsonDeserializer<T> deser) {
      module.addDeserializer(type, deser);
      mapper.registerModule(module);

      recursiveModule.addDeserializer(type, deser);
      mapper.registerModule(recursiveModule);
    }

    @Override
    public List deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      ObjectCodec oc = p.getCodec();
      JsonNode jsonNode = oc.readTree(p);
      ArrayNode arrayNode = (ArrayNode) jsonNode;
      String listType = (arrayNode.get(0)).asText();
      List list;
      try {
        list = (List) Class.forName((arrayNode.get(0)).asText()).newInstance();
      } catch (Exception e) {
        LOGGER.error(
            "Error creating new list of type " + listType + " in JsonTestSerdeHandler. ", e);
        throw new IOException("Error creating new list instance in JsonTestSerdeHandler. ", e);
      }
      if (arrayNode.get(1) == null) {
        return list;
      }

      arrayNode.remove(0);

      try {
        boolean itemType = true;
        String listItemType = null;
        for (int i = 0; i < arrayNode.size(); i++) {
          if (itemType) {
            listItemType = arrayNode.get(i).asText();
            itemType = false;
          } else {
            list.add(
                List.class.isAssignableFrom(Class.forName(listItemType))
                    ? recursiveMapper.readValue(arrayNode.get(i).toString().getBytes(), List.class)
                    : mapper.readValue(
                        arrayNode.get(i).toString().getBytes(), Class.forName(listItemType)));
            itemType = true;
          }
        }
        return list;
      } catch (ClassNotFoundException e) {
        throw new IOException("ClassNotFoundException exception", e);
      }
    }
  }

  public static class TestMapSerializer extends JsonSerializer<Map> {

    private static ObjectMapper mapper = new ObjectMapper();
    private static SimpleModule module = new SimpleModule();

    static {
      setUpMapperProperties(mapper);

      module.addSerializer(List.class, new TestListSerializer());
      module.addDeserializer(List.class, new TestListDeserializer());

      mapper.registerModule(module);
    }

    public static synchronized <T> void registerSerializer(Class<T> type, JsonSerializer<T> ser) {
      module.addSerializer(type, ser);
      mapper.registerModule(module);
    }

    public static synchronized <T> void registerDeserializer(
        Class<T> type, JsonDeserializer<T> deser) {
      module.addDeserializer(type, deser);
      mapper.registerModule(module);
    }

    @Override
    public void serialize(Map value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException, JsonProcessingException {
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
            gen.writeObjectField(
                mapper.writeValueAsString(value.keySet().toArray()[i]),
                value.get(value.keySet().toArray()[i]));
            gen.writeEndObject();
          }
        }
      }
      gen.writeEndArray();
    }
  }

  public static class TestMapDeserializer extends JsonDeserializer<Map> {

    private static ObjectMapper mapper = new ObjectMapper();
    private static SimpleModule module = new SimpleModule();

    private static ObjectMapper recursiveMapper = new ObjectMapper();
    private static SimpleModule recursiveModule = new SimpleModule();

    static {
      setUpMapperProperties(mapper);
      setUpMapperProperties(recursiveMapper);

      module.addSerializer(List.class, new TestListSerializer());
      module.addDeserializer(List.class, new TestListDeserializer());

      recursiveModule.addSerializer(List.class, new TestListSerializer());
      recursiveModule.addDeserializer(List.class, new TestListDeserializer());

      // to support Map<Map<>>
      recursiveModule.addSerializer(Map.class, new TestMapSerializer());
      recursiveModule.addDeserializer(Map.class, new TestMapDeserializer());

      mapper.registerModule(module);
      recursiveMapper.registerModule(recursiveModule);
    }

    static synchronized <T> void registerSerializer(Class<T> type, JsonSerializer<T> ser) {
      module.addSerializer(type, ser);
      mapper.registerModule(module);

      recursiveModule.addSerializer(type, ser);
      mapper.registerModule(recursiveModule);
    }

    static synchronized <T> void registerDeserializer(Class<T> type, JsonDeserializer<T> deser) {
      module.addDeserializer(type, deser);
      mapper.registerModule(module);

      recursiveModule.addDeserializer(type, deser);
      mapper.registerModule(recursiveModule);
    }

    @Override
    public Map deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      // maps get serialized as ArrayNode of ObjectNodes.
      ObjectCodec oc = p.getCodec();
      ArrayNode arrayNode = oc.readTree(p);
      String mapType =
          arrayNode.get(0) != null
              ? arrayNode.get(0).get("TestMapSerializer|mapType").asText()
              : null;
      if (mapType == null) {
        return new HashMap<>();
      }
      arrayNode.remove(0);
      Map map;
      try {
        map = (Map) Class.forName(mapType).newInstance();
      } catch (Exception e) {
        LOGGER.error("Error creating new map of type " + mapType + " in JsonTestSerdeHandler. ", e);
        throw new IOException("Error creating new map instance in JsonTestSerdeHandler. ", e);
      }
      if (arrayNode.size() == 0) {
        return map;
      }

      try {
        Iterator<JsonNode> elements = arrayNode.elements();
        String mapKeyType = null;
        String mapValueType = null;
        boolean mapKey = true;
        boolean mapValue = false;
        while (elements.hasNext()) {
          ObjectNode element = (ObjectNode) elements.next();
          Iterator<String> keys = element.fieldNames();
          if (mapKey || mapValue) {
            if (mapKey) {
              mapKeyType = element.get("TestMapSerializer|mapElementKeyType").asText();
              mapKey = false;
              mapValue = true;
            } else {
              mapValueType = element.get("TestMapSerializer|mapElementValueType").asText();
              mapValue = false;
            }

          } else {
            while (keys.hasNext()) {
              String key = keys.next();
              map.put(
                  Map.class.isAssignableFrom(Class.forName(mapKeyType))
                      ? recursiveMapper.readValue(key.getBytes(), Map.class)
                      : mapper.readValue(key.getBytes(), Class.forName(mapKeyType)),
                  Map.class.isAssignableFrom(Class.forName(mapValueType))
                      ? recursiveMapper.readValue(element.get(key).toString().getBytes(), Map.class)
                      : mapper.readValue(
                          element.get(key).toString().getBytes(), Class.forName(mapValueType)));
            }
            mapKey = true;
          }
        }
        return map;
      } catch (ClassNotFoundException e) {
        throw new IOException("class cast exception", e);
      }
    }
  }
}
