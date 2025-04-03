package org.storkforge.barkr.dto.postDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ResponsePost(
        Long id,
        @Size(min=1, max=255) @NotBlank String content,
        @NotNull Long accountId,
        @PastOrPresent LocalDateTime createdAt
) {}
