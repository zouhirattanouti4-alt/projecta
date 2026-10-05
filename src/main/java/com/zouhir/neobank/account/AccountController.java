package com.zouhir.neobank.account;

import com.zouhir.neobank.account.dto.AccountBalanceDto;
import com.zouhir.neobank.account.dto.AccountDto;
import com.zouhir.neobank.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{account_id}/balance")
    public ResponseEntity<AccountBalanceDto> balance(
            @PathVariable("account_id") UUID accountId,
            @AuthenticationPrincipal User authenticatedUser
    ){
        boolean isOwner = accountService.verifyAccountOwnership(accountId, authenticatedUser.getId());
        if(!isOwner){
            throw new AccessDeniedException("You are not authorized for this Request");
        }

        return ResponseEntity.status(HttpStatus.OK).body(accountService.balance(accountId));
    }

    @GetMapping("/{account_id}/account")
    public ResponseEntity<AccountDto> account(
            @PathVariable("account_id") UUID accountId,
            @AuthenticationPrincipal User authenticatedUser
    ){
        boolean isOwner = accountService.verifyAccountOwnership(accountId, authenticatedUser.getId());
        if(!isOwner){
            throw new AccessDeniedException("You are not authorized for this Request");
        }

        return ResponseEntity.status(HttpStatus.OK).body(accountService.account(accountId));
    }

}
