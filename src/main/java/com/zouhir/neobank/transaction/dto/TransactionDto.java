package com.zouhir.neobank.transaction.dto;

import com.zouhir.neobank.account.Account;
import com.zouhir.neobank.transaction.Status;
import com.zouhir.neobank.transaction.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record TransactionDto (
     UUID id,

     UUID fromAccountId,

     UUID toAccountId,

     BigDecimal amount
)
{}
