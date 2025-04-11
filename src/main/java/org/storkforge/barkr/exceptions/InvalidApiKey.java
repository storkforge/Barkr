package org.storkforge.barkr.exceptions;

public class InvalidApiKey extends RuntimeException {
    public InvalidApiKey() {
        super();
    }

    public InvalidApiKey(String message) {
        super(message);
    }

    public InvalidApiKey(String message, Throwable cause) {
        super(message, cause);
    }
}
