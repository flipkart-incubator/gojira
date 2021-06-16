package com.flipkart.gojira.external;

import com.flipkart.gojira.external.config.ExternalConfig;

/**
 * Exception to be thrown by {@link Managed#update(String, ExternalConfig)} ()}
 * if we are not able to close and create new connections
 * established as part of {@link Managed#update(String, ExternalConfig)} ()}.
 */
public class UpdateException extends RuntimeException {

  public UpdateException(String message) {
    super(message);
  }

  public UpdateException(String message, Throwable cause) {
    super(message, cause);
  }

}
