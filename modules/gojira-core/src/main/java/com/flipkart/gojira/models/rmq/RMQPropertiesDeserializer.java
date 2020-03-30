package com.flipkart.gojira.models.rmq;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.inject.Singleton;
import com.rabbitmq.client.AMQP;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class RMQPropertiesDeserializer extends JsonDeserializer<AMQP.BasicProperties> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public AMQP.BasicProperties deserialize(JsonParser jsonParser, DeserializationContext context)
      throws IOException {
    TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
    TreeNode headersTreeNode = treeNode.get("headers");
    String headersAsStr = headersTreeNode.toString();
    TypeFactory typeFactory = objectMapper.getTypeFactory();
    MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Object.class);
    Map<String, Object> headersMap = objectMapper.readValue(headersAsStr, mapType);
    String contentType = getStringValue("contentType", treeNode);
    String contentEncoding = getStringValue("contentEncoding", treeNode);
    Integer deliveryMode = getIntegerValue("deliveryMode", treeNode);
    Integer priority = getIntegerValue("priority", treeNode);
    String correlationId = getStringValue("correlationId", treeNode);
    String replyTo = getStringValue("replyTo", treeNode);
    String expiration = getStringValue("expiration", treeNode);
    String messageId = getStringValue("messageId", treeNode);
    Date timestamp = objectMapper.treeToValue(treeNode.get("timestamp"), Date.class);
    String type = getStringValue("type", treeNode);
    String userId = getStringValue("userId", treeNode);
    String appId = getStringValue("appId", treeNode);
    String clusterId = getStringValue("clusterId", treeNode);
    return new AMQP.BasicProperties(
        contentType,
        contentEncoding,
        headersMap,
        deliveryMode,
        priority,
        correlationId,
        replyTo,
        expiration,
        messageId,
        timestamp,
        type,
        userId,
        appId,
        clusterId);
  }

  private String getStringValue(String propertyName, TreeNode treeNode) throws IOException {
    if (treeNode == null || propertyName == null) {
      return null;
    }
    TreeNode propertyTree = treeNode.get(propertyName);
    if (propertyTree == null) {
      return null;
    }
    return objectMapper.treeToValue(treeNode.get(propertyName), String.class);
  }

  private Integer getIntegerValue(String propertyName, TreeNode treeNode)
      throws JsonProcessingException {
    if (treeNode == null || propertyName == null) {
      return null;
    }
    TreeNode propertyTree = treeNode.get(propertyName);
    if (propertyTree == null) {
      return null;
    }
    return objectMapper.treeToValue(treeNode.get(propertyName), Integer.class);
  }
}
