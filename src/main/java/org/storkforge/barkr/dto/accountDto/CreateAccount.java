package org.storkforge.barkr.dto.accountDto;

import jakarta.validation.constraints.NotBlank;

public record CreateAccount(@NotBlank String username, @NotBlank String breed) {}
