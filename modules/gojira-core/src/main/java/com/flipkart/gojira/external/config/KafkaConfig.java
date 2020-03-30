package com.flipkart.gojira.external.config;

public class KafkaConfig extends ExternalConfig {

  private String hostNamePort;

  public String getHostNamePort() {
    return hostNamePort;
  }

  public void setHostNamePort(String hostNamePort) {
    this.hostNamePort = hostNamePort;
  }
}
