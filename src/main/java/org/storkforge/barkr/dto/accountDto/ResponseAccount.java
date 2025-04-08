package org.storkforge.barkr.dto.accountDto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record ResponseAccount (
        @NotNull @Positive Long id,
        @NotBlank String username,
        @PastOrPresent LocalDateTime createdAt,
        @NotBlank String breed,
        byte[] image
){
}
