package org.storkforge.barkr.dto.accountDto;


import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ResponseAccountList(@NotBlank List<ResponseAccount> accounts) {
}
