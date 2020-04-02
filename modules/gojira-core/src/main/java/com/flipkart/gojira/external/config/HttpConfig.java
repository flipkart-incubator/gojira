package com.flipkart.gojira.external.config;

public class HttpConfig extends ExternalConfig {

  private String hostNamePort;
  private int maxConnections;
  private int connectionTimeout;
  private int operationTimeout;

  public HttpConfig() {
    super("HTTP");
  }

  public String getHostNamePort() {
    return hostNamePort;
  }

  public void setHostNamePort(String hostNamePort) {
    this.hostNamePort = hostNamePort;
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public int getOperationTimeout() {
    return operationTimeout;
  }

  public void setOperationTimeout(int operationTimeout) {
    this.operationTimeout = operationTimeout;
  }
}
