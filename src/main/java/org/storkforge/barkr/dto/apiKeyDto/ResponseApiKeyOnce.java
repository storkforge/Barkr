package org.storkforge.barkr.dto.apiKeyDto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record ResponseApiKeyOnce(
        @NotBlank String key,
        @NotBlank String value,
        @NotBlank String message) implements Serializable {
}
