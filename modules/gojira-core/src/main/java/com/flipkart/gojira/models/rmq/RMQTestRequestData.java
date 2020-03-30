package com.flipkart.gojira.models.rmq;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flipkart.gojira.models.TestRequestData;
import com.rabbitmq.client.AMQP;

/**
 * Extends {@link com.flipkart.gojira.models.TestRequestData} for {@link RMQTestDataType}. Captures
 * all information required for initiating a rmq request.
 */
public class RMQTestRequestData extends TestRequestData<RMQTestDataType> {

  /** The exchange to which data will be published. */
  private String exchangeName;

  /** The key based on which data will be routed to one of the queues. */
  private byte[] routingKey;

  /**
   * The mandatory flag tells RabbitMq that the message must be routable., i.e, there must be one or
   * more bound queues that will receive the message.
   */
  private boolean mandatory;

  /** The actual data that needs to be transmitted over the channel. */
  private byte[] data;

  /**
   * content-type : let consumers know how to interpret / deserialize the message body
   * content-encoding : to indicate compression of message body in some special way i.e gzip / UTF-8
   * message-id & correlationId : to uniquely identify messages and message responses through your
   * workflow. Only for application's purpose. timestamp : when message was created. expiration:
   * tells RMQ when it should expire messages, if it has not been consumed. delivery-mode: to tell
   * RabbitMQ for disk or memory backup of messages app-id and user-id : to track publishers, to
   * perform consumer side validation. headers : free-form property definition and RabbitMQ routing.
   */
  @JsonDeserialize(using = RMQPropertiesDeserializer.class)
  private AMQP.BasicProperties properties;

  public RMQTestRequestData() {
    super(new RMQTestDataType());
  }

  public static RMQTestRequestData.Builder builder() {
    return new RMQTestRequestData.Builder();
  }

  public String getExchangeName() {
    return exchangeName;
  }

  public byte[] getRoutingKey() {
    return routingKey;
  }

  public boolean isMandatory() {
    return mandatory;
  }

  public byte[] getData() {
    return data;
  }

  public AMQP.BasicProperties getProperties() {
    return properties;
  }

  public static class Builder {

    private RMQTestRequestData rmqTestRequestData;

    private Builder() {
      this.rmqTestRequestData = new RMQTestRequestData();
    }

    public RMQTestRequestData build() {
      return this.rmqTestRequestData;
    }

    public RMQTestRequestData.Builder setExchangeName(String exchangeName) {
      this.rmqTestRequestData.exchangeName = exchangeName;
      return this;
    }

    public RMQTestRequestData.Builder setRoutingKey(byte[] routingKey) {
      this.rmqTestRequestData.routingKey = routingKey;
      return this;
    }

    public RMQTestRequestData.Builder setData(byte[] data) {
      this.rmqTestRequestData.data = data;
      return this;
    }

    public RMQTestRequestData.Builder setProperties(AMQP.BasicProperties basicProperties) {
      this.rmqTestRequestData.properties = basicProperties;
      return this;
    }

    public RMQTestRequestData.Builder setMandatory(boolean mandatory) {
      this.rmqTestRequestData.mandatory = mandatory;
      return this;
    }
  }
}
