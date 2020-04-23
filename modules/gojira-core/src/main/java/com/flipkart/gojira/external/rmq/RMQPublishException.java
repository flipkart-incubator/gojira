package com.flipkart.gojira.external.rmq;

import com.flipkart.gojira.execute.TestExecutionException;
import com.flipkart.gojira.models.rmq.RMQTestDataType;

/** Exception thrown if we are not able initiate execution for {@link RMQTestDataType} */
public class RMQPublishException extends TestExecutionException {

  public RMQPublishException(String message) {
    super(message);
  }

  public RMQPublishException(String message, Throwable cause) {
    super(message, cause);
  }
}
