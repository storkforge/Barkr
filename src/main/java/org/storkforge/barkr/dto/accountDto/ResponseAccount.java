package org.storkforge.barkr.dto.accountDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

public record ResponseAccount (
        Long id,
        @NotBlank String username,
        @PastOrPresent LocalDateTime createdAt
){
}
