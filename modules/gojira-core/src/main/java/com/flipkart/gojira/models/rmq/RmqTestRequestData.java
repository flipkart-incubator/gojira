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

package com.flipkart.gojira.models.rmq;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flipkart.gojira.models.TestRequestData;
import com.rabbitmq.client.AMQP;

/**
 * Extends {@link com.flipkart.gojira.models.TestRequestData} for {@link RmqTestDataType}. Captures
 * all information required for initiating a rmq request.
 */
public class RmqTestRequestData extends TestRequestData<RmqTestDataType> {

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
  @JsonDeserialize(using = RmqPropertiesDeserializer.class)
  private AMQP.BasicProperties properties;

  private RmqTestRequestData() {
    super(new RmqTestDataType());
  }

  public static RmqTestRequestData.Builder builder() {
    return new RmqTestRequestData.Builder();
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

    private RmqTestRequestData rmqTestRequestData;

    private Builder() {
      this.rmqTestRequestData = new RmqTestRequestData();
    }

    public RmqTestRequestData build() {
      return this.rmqTestRequestData;
    }

    public RmqTestRequestData.Builder setExchangeName(String exchangeName) {
      this.rmqTestRequestData.exchangeName = exchangeName;
      return this;
    }

    public RmqTestRequestData.Builder setRoutingKey(byte[] routingKey) {
      this.rmqTestRequestData.routingKey = routingKey;
      return this;
    }

    public RmqTestRequestData.Builder setData(byte[] data) {
      this.rmqTestRequestData.data = data;
      return this;
    }

    public RmqTestRequestData.Builder setProperties(AMQP.BasicProperties basicProperties) {
      this.rmqTestRequestData.properties = basicProperties;
      return this;
    }

    public RmqTestRequestData.Builder setMandatory(boolean mandatory) {
      this.rmqTestRequestData.mandatory = mandatory;
      return this;
    }
  }
}
