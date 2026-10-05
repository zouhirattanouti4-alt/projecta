package com.zouhir.neobank.account;

import com.zouhir.neobank.account.dto.AccountBalanceDto;
import com.zouhir.neobank.account.dto.AccountDto;
import com.zouhir.neobank.common.exceptions.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountBalanceDto balance(UUID id){
        var account = accountRepository.findById(id);

        if(account.isEmpty()){
            throw new AccountNotFoundException("Account with id : "+id+" does not exist");
        }
        return AccountBalanceDto
                .builder()
                .balance(account.get().getBalance())
                .build();
    }

    public AccountDto account(UUID id){
        var account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with id : "+id+" does not exist"));

        return AccountDto
                .builder()
                .id(id).
                accountNo(account.getAccountNo())
                .balance(account.getBalance())
                .build();
    }

    public boolean verifyAccountOwnership(UUID id, UUID userId){
        return accountRepository.existsByIdAndUser_Id(id,userId);
    }

}
