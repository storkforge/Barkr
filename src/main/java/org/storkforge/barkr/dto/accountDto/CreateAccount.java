package org.storkforge.barkr.dto.accountDto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record CreateAccount(@NotBlank String username, @NotBlank String breed) implements Serializable {}
