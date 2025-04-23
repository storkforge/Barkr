package org.storkforge.barkr.dto.apiKeyDto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.storkforge.barkr.domain.entity.GoogleAccountApiKeyLink;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateApiKey(
        @NotBlank String hashedApiKey,
        @PastOrPresent LocalDateTime issuedAt,
        @FutureOrPresent LocalDateTime expiresAt,
        @NotNull Boolean revoked,
        @PastOrPresent LocalDateTime lastUsedAt,
        @NotBlank String apiKeyName,
        @NotNull GoogleAccountApiKeyLink googleAccountApiKeyLink,
        @NotNull UUID referenceId) implements Serializable {
}
