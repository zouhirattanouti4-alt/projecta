package com.zouhir.neobank.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record LoginRequestDto(
        @NotBlank
        String email,

        @NotBlank
        String password
)  {
}
