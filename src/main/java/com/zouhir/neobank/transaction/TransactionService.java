package com.zouhir.neobank.transaction;

import com.zouhir.neobank.account.Account;
import com.zouhir.neobank.account.AccountRepository;
import com.zouhir.neobank.common.exceptions.AccountNotFoundException;
import com.zouhir.neobank.transaction.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionDto transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount, String reference){
        Account sourceAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Error : The source account does not exist"));
        Account destAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Error : The destination account does not exist"));

        sourceAccount.debit(amount);
        destAccount.credit(amount);

        boolean fromAccountIsFirst = fromAccountId.compareTo(toAccountId) < 0;
        Account firstAccount = fromAccountIsFirst ? sourceAccount : destAccount;
        Account secondAccount = fromAccountIsFirst ? destAccount : sourceAccount;

        accountRepository.save(firstAccount);
        accountRepository.save(secondAccount);

        Transaction transaction = Transaction.builder().fromAccount(sourceAccount).toAccount(destAccount).amount(amount).type(Type.TRANSFER).status(Status.COMPLETED).reference(reference).build();
        transactionRepository.save(transaction);

        return TransactionDto.builder().id(transaction.getId()).fromAccountId(fromAccountId).toAccountId(toAccountId).amount(amount).build();
    }
}
