package com.zouhir.neobank.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequestDto(
        @NotNull(message = "Source account is required")
        UUID fromAccountId,

        @NotNull(message = "Destination account is required")
        UUID toAccountId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount should be positive")
        BigDecimal amount,

        @NotBlank(message = "Reference cannot be blank for idempotency")
        String reference
) { }
