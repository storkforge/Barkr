package org.storkforge.barkr.dto.accountDto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccount (@NotBlank String username)
{}
