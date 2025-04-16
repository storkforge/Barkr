package org.storkforge.barkr.dto.postDto;

import jakarta.validation.constraints.*;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ResponsePost(
        @NotNull @Positive Long id,
        @Size(min=1, max=255) @NotBlank String content,
        @NotNull ResponseAccount account,
        @PastOrPresent LocalDateTime createdAt
) implements Serializable {}
