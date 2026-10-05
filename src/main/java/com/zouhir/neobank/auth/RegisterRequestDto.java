package com.zouhir.neobank.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RegisterRequestDto(
        @NotBlank
        String email,

        @NotBlank
        String password,

        @NotBlank
        String fullName
        ) {
}
