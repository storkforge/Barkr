package org.storkforge.barkr.dto.apiKeyDto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ResponseApiKey(
        @PastOrPresent LocalDateTime issuedAt,
        @FutureOrPresent LocalDateTime expiresAt,
        @FutureOrPresent LocalDateTime lastUsedAt,
        @NotBlank String apiKeyName,
        @NotNull Boolean revoked

        ) implements Serializable {
}
