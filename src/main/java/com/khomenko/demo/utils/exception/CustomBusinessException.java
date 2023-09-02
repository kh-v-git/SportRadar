package com.khomenko.demo.utils.exception;


/**
 * Custom exception class for handling business-related exceptions.
 */
public class CustomBusinessException extends RuntimeException {

    /**
     * Constructs a new CustomBusinessException with no message.
     */
    public CustomBusinessException() {
        super();
    }

    /**
     * Constructs a new CustomBusinessException with the specified detail message.
     *
     * @param message The detail message.
     */
    public CustomBusinessException(String message) {
        super(message);
    }

    /**
     * Constructs a new CustomBusinessException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public CustomBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new CustomBusinessException with the specified cause.
     *
     * @param cause The cause of the exception.
     */
    public CustomBusinessException(Throwable cause) {
        super(cause);
    }
}
