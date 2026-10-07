package com.zouhir.neobank.transaction;

import com.zouhir.neobank.account.Account;
import com.zouhir.neobank.account.AccountRepository;
import com.zouhir.neobank.common.exceptions.AccountNotFoundException;
import com.zouhir.neobank.common.exceptions.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("Transaction service tests")
class TransactionServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private TransactionService transactionService;

    private Account sourceAccount;
    private Account destAccount;
    private UUID sourceId;
    private UUID destId;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    @BeforeEach
    public void setUp(){
        sourceId = UUID.randomUUID();
        destId = UUID.randomUUID();

        BigDecimal sourceBalance = new BigDecimal("1000");
        BigDecimal destBalance = new BigDecimal("0");

        sourceAccount = Account.builder().id(sourceId).balance(sourceBalance).build();
        destAccount = Account.builder().id(destId).balance(destBalance).build();
    }

    @Test
    @DisplayName("should transfer successfully when funds are sufficient")
    public void should_transfer_successfully_when_funds_are_sufficient(){
        //Given
        BigDecimal amountToTransfer = new BigDecimal("100");

        given(accountRepository.findById(sourceId)).willReturn(Optional.of(sourceAccount));
        given(accountRepository.findById(destId)).willReturn(Optional.of(destAccount));

        //When
        transactionService.transfer(sourceId, destId, amountToTransfer, "reference_123");

        //Then
        assertEquals(new BigDecimal("900"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("100"), destAccount.getBalance());

        then(accountRepository).should(times(2)).save(any(Account.class));

        then(transactionRepository).should(times(1)).save(transactionCaptor.capture());

        Transaction capturedTransaction = transactionCaptor.getValue();

        assertEquals(amountToTransfer, capturedTransaction.getAmount());
    }

}