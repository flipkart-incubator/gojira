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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deserializers {
  private static final Logger LOGGER = LoggerFactory.getLogger(Deserializers.class);

  /** Custom Deserializer for List. */
  public static class TestListDeserializer extends JsonDeserializer<List> {

    @Override
    public List deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      ObjectCodec oc = p.getCodec();
      JsonNode jsonNode = oc.readTree(p);
      ArrayNode arrayNode = (ArrayNode) jsonNode;
      String listType = (arrayNode.get(0)).asText();
      List list;
      try {
        list = (List) Class.forName((arrayNode.get(0)).asText()).newInstance();
      } catch (Exception e) {
        LOGGER.error("Error creating new list of type " + listType + " in JsonMapListSerdeHandler. ", e);
        throw new IOException(
            "Error creating new list of type " + listType + " in JsonMapListSerdeHandler. ", e);
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
                    ? oc.treeToValue(arrayNode.get(i), List.class)
                    : Map.class.isAssignableFrom(Class.forName(listItemType))
                        ? oc.treeToValue(arrayNode.get(i), Map.class)
                        : oc.treeToValue(arrayNode.get(i), Class.forName(listItemType)));
            itemType = true;
          }
        }
        return list;
      } catch (ClassNotFoundException e) {
        LOGGER.error("ClassNotFoundException exception", e);
        throw new IOException("ClassNotFoundException exception", e);
      }
    }
  }

  /** Custom Deserializer for Map. */
  public static class TestMapDeserializer extends JsonDeserializer<Map> {

    @Override
    public Map deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
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
        LOGGER.error("Error creating new map of type " + mapType + " in JsonMapListSerdeHandler. ", e);
        throw new IOException(
            "Error creating new map of type " + mapType + " in JsonMapListSerdeHandler. ", e);
      }
      if (arrayNode.size() == 0) {
        return map;
      }

      try {
        Iterator<JsonNode> elements = arrayNode.elements();
        String mapKeyType = null;
        String mapValueType = null;
        String type = null;
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
            List<Object> keyValuePair = new ArrayList<>();
            while (keys.hasNext()) {
              String key = keys.next();
              if (type == null) {
                type = mapKeyType;
              } else {
                type = mapValueType;
              }
              Object keyOrValue =
                  Map.class.isAssignableFrom(Class.forName(type))
                      ? oc.treeToValue(element.get(key), Map.class)
                      : List.class.isAssignableFrom(Class.forName(type))
                          ? oc.treeToValue(element.get(key), List.class)
                          : oc.treeToValue(element.get(key), Class.forName(type));
              keyValuePair.add(keyOrValue);
            }
            map.put(keyValuePair.get(0), keyValuePair.get(1));
            keyValuePair.clear();
            mapKey = true;
            type = null;
          }
        }
        return map;
      } catch (ClassNotFoundException e) {
        LOGGER.error("class cast exception", e);
        throw new IOException("class cast exception", e);
      }
    }
  }
}
