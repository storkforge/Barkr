package org.storkforge.barkr.dto.apiKeyDto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;

public record GenerateApiKeyRequest(
        @NotBlank String apiKeyName ,
        LocalDateTime expiresAt) implements Serializable {
}
