package com.flipkart.gojira.external.config;

public class KafkaConfig extends ExternalConfig {

  private String hostNamePort;

  public KafkaConfig() {
    super("KAFKA");
  }

  public String getHostNamePort() {
    return hostNamePort;
  }

  public void setHostNamePort(String hostNamePort) {
    this.hostNamePort = hostNamePort;
  }
}
