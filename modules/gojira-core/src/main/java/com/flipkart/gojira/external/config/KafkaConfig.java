package com.flipkart.gojira.external.config;

import static com.flipkart.gojira.core.GojiraConstants.KAFKA_TEST_DATA_TYPE;

public class KafkaConfig extends ExternalConfig {

  private String hostNamePort;

  public KafkaConfig() {
    super(KAFKA_TEST_DATA_TYPE);
  }

  public String getHostNamePort() {
    return hostNamePort;
  }

  public void setHostNamePort(String hostNamePort) {
    this.hostNamePort = hostNamePort;
  }
}
