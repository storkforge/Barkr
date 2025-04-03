package org.storkforge.barkr.dto.postDto;

import java.time.LocalDateTime;

public record ResponsePost(
        Long id,
        String content,
        Long accountId,
        LocalDateTime createdAt
) {}
