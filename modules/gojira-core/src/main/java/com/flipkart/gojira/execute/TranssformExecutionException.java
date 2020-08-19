package com.flipkart.gojira.execute;

import com.flipkart.gojira.core.Mode;

/**
 * Exception thrown during execution in mode {@link Mode#TRANSFORM}.
 */
public class TranssformExecutionException extends Exception {

    public TranssformExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TranssformExecutionException(String message) {
        super(message);
    }
}
