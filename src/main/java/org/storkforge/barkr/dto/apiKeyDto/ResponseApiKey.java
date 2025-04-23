package org.storkforge.barkr.dto.apiKeyDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

public record ResponseApiKey(
        @NotBlank String issuedAt,
        @NotBlank String expiresAt,
        @NotBlank String lastUsedAt,
        @NotBlank String apiKeyName,
        @NotNull Boolean revoked,
        @NotNull UUID referenceId

        ) implements Serializable {
}
