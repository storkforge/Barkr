package org.storkforge.barkr.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.storkforge.barkr.domain.entity.Post;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFound extends RuntimeException {
    public PostNotFound() {
        super();
    }

    public PostNotFound(String message) {
        super(message);
    }
}
