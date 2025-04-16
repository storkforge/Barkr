package org.storkforge.barkr.dto.postDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record CreatePost(
        @Size(min=1, max=255) @NotBlank String content,
        @NotNull @Positive Long accountId
) implements Serializable {}
