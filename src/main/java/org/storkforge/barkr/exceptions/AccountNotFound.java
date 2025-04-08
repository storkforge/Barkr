package org.storkforge.barkr.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountNotFound extends RuntimeException {
    public AccountNotFound() {
        super();
    }

    public AccountNotFound(String message) {
        super(message);
    }
}
