package com.zouhir.neobank.transaction;

import com.zouhir.neobank.transaction.dto.TransactionDto;
import com.zouhir.neobank.transaction.dto.TransactionRequestDto;
import com.zouhir.neobank.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("transfer")
    public ResponseEntity<TransactionDto> transfer(
            @RequestBody TransactionRequestDto request,
            @AuthenticationPrincipal User authenticatedUser
            )
    {
        boolean isOwner = transactionService.verifyAccountOwnership(request.fromAccountId(),authenticatedUser.getId());
        if(!isOwner) throw new AccessDeniedException("You are not autorized for this transaction");

        TransactionDto transaction = transactionService.transfer(
                request.fromAccountId(),
                request.toAccountId(),
                request.amount(),
                request.reference()
        );

        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @GetMapping("history/{account_id}")
    public ResponseEntity<Page<TransactionDto>> history(
            @PathVariable("account_id") UUID id,
            @AuthenticationPrincipal User authenticatedUser,
            Pageable pageable
            )
    {
        boolean isOwner = transactionService.verifyAccountOwnership(id, authenticatedUser.getId());
        if(!isOwner) throw new AccessDeniedException("You are not autorized for this transaction");

        Page<TransactionDto> transactions = transactionService.history(id, pageable);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }


}
