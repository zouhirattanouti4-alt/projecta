package com.zouhir.neobank.auth;

import lombok.Builder;

@Builder
public record AuthenticationResponseDto(
        String jwt
) {
}
