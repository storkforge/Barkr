package org.storkforge.barkr.dto.apiKeyDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record UpdateApiKey(
        @NotBlank String apiKeyTarget,
        @NotBlank String apiKeyName,
        @NotNull Boolean revoke) implements Serializable {
}
