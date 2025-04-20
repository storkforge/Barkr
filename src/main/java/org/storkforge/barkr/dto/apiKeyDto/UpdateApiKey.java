package org.storkforge.barkr.dto.apiKeyDto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

public record UpdateApiKey(
        @NotNull UUID referenceId,
        String apiKeyName,
        Boolean revoke) implements Serializable {
}
