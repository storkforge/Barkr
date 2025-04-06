package org.storkforge.barkr.dto.postDto;

import jakarta.validation.constraints.*;
import org.storkforge.barkr.domain.entity.Account;

import java.time.LocalDateTime;

public record ResponsePost(
        @NotNull @Positive Long id,
        @Size(min=1, max=255) @NotBlank String content,
        @NotNull Account account,
        @PastOrPresent LocalDateTime createdAt
) {}
