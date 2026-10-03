package com.zouhir.neobank.transaction;

import com.zouhir.neobank.account.Account;
import com.zouhir.neobank.account.AccountRepository;
import com.zouhir.neobank.common.exceptions.AccountNotFoundException;
import com.zouhir.neobank.transaction.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public boolean verifyAccountOwnership(UUID id, UUID userId){
        return accountRepository.existsByIdAndUserId(id,userId);
    }

    public Page<TransactionDto> history(UUID id, Pageable pageable){
        Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending());

        Page<Transaction> page = transactionRepository.findByFromAccount_IdOrToAccount_Id(id, id, pageRequest);

        return page.map(tx -> TransactionDto.builder()
                .id(tx.getId())
                .fromAccountId(tx.getFromAccount() != null ? tx.getFromAccount().getId() : null)
                .toAccountId(tx.getToAccount() != null ? tx.getToAccount().getId() : null)
                .amount(tx.getAmount())
                .build()
        );
    }
}
