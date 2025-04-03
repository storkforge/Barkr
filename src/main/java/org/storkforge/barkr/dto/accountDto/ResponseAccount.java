package org.storkforge.barkr.dto.accountDto;

import java.time.LocalDateTime;

public record ResponseAccount (
        Long id,
        String username,
        LocalDateTime createdAt
){
}
