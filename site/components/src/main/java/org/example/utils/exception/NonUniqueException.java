package org.example.utils.exception;

/**
 * Created by chinnku on Nov, 2021
 */
public class NonUniqueException extends Exception {

    public NonUniqueException() {
    }

    public NonUniqueException(String message) {
        super(message);
    }

    public NonUniqueException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public NonUniqueException(Throwable rootCause) {
        super(rootCause);
    }
}
