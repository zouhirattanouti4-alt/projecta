package com.zouhir.neobank.auth.Dto;

import lombok.Builder;

@Builder
public record AuthenticationResponseDto(
        String jwt
) {
}
