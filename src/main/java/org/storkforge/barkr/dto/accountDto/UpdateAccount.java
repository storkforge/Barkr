package org.storkforge.barkr.dto.accountDto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record UpdateAccount (@NotBlank String username, @NotBlank String breed) implements Serializable
{}
