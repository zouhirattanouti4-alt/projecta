package com.zouhir.neobank.user.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDto (
        UUID id,
        String email,
        String fullName
){
}
