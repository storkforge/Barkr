package org.storkforge.barkr.dto.apiKeyDto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record CreateApiKey(
        String hashedApiKey,
        LocalDateTime issuedAt,
        LocalDateTime expiresAt,
        Boolean revoked,
        LocalDateTime lastUsedAt,
        String apiKeyName
) implements Serializable {
}
