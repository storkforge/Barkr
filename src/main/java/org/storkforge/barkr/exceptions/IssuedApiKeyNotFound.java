package org.storkforge.barkr.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class IssuedApiKeyNotFound extends RuntimeException {
    public IssuedApiKeyNotFound() {
        super();
    }
    public IssuedApiKeyNotFound(String message) {
        super(message);
    }
}
