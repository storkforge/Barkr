package org.storkforge.barkr.dto.postDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePost(
        @Size(min=1, max=255) @NotBlank String content
) {}
