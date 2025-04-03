package org.storkforge.barkr.dto.postDto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record ResponsePost(
        @NotNull @Positive Long id,
        @Size(min=1, max=255) @NotBlank String content,
        @NotNull Long accountId,
        @PastOrPresent LocalDateTime createdAt
) {}
