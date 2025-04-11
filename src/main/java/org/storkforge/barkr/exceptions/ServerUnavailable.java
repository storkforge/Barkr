package org.storkforge.barkr.exceptions;

public class ServerUnavailable extends RuntimeException {
    public ServerUnavailable() {
        super();

    }
    public ServerUnavailable(String message) {
        super(message);
    }

    public ServerUnavailable(String message, Throwable cause) {
        super(message, cause);
    }
}
