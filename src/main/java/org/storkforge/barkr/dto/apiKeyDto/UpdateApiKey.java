package org.storkforge.barkr.dto.apiKeyDto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateApiKey(
        @NotNull UUID referenceId,
        String apiKeyName,
        Boolean revoke,
        LocalDateTime lastUsedAt) implements Serializable {
}
