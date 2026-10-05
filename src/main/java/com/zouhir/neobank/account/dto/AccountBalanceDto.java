package com.zouhir.neobank.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountBalanceDto(
        @NotNull
        BigDecimal balance
) {
}
