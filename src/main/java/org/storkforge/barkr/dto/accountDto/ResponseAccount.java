package org.storkforge.barkr.dto.accountDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record ResponseAccount (
        @NotNull @Positive Long id,
        @NotBlank String username,
        @PastOrPresent LocalDateTime createdAt
){
}
