package com.flipkart.gojira.external.config;

import java.util.List;

public class RMQConfig extends ExternalConfig {
  /** port at which RMQ is hosted */
  private int port;

  /** username of RMQ cluster */
  private String username;

  /** password of RMQ cluster */
  private String password;
  /**
   * The virtual host path in which the exchange exists. Default value is "/". Exchange with same
   * name can exist on the same host on different virtual host path.
   */
  private String virtualHost;

  private boolean automaticRecoveryEnabled;

  /** List of end-points for RMQ test machines. */
  private List<String> endpoints;

  public RMQConfig() {
    super("RMQ");
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getVirtualHost() {
    return virtualHost;
  }

  public void setVirtualHost(String virtualHost) {
    this.virtualHost = virtualHost;
  }

  public List<String> getEndpoints() {
    return endpoints;
  }

  public void setEndpoints(List<String> endpoints) {
    this.endpoints = endpoints;
  }

  public boolean isAutomaticRecoveryEnabled() {
    return automaticRecoveryEnabled;
  }

  public void setAutomaticRecoveryEnabled(boolean automaticRecoveryEnabled) {
    this.automaticRecoveryEnabled = automaticRecoveryEnabled;
  }
}
