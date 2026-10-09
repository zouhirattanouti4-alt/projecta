package com.zouhir.neobank.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("Account Service Tests")
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private UUID id;

    @BeforeEach
    public void setUp(){
        id = UUID.randomUUID();
    }

    @Test
    @DisplayName("should return true when user owns account")
    public void should_return_true_when_user_owns_account(){
        //GIVEN
        UUID userId = UUID.randomUUID();
        given(accountRepository.existsByIdAndUser_Id(id,userId))
                .willReturn(true);

        //WHEN and THEN
        assertTrue(accountService.verifyAccountOwnership(id, userId));
    }

    @Test
    @DisplayName("should return false when user does not own account")
    public void should_return_false_when_user_does_not_own_account(){
        //GIVEN
        UUID userId = UUID.randomUUID();
        given(accountRepository.existsByIdAndUser_Id(id,userId))
                .willReturn(false);

        //WHEN and THEN
        assertFalse(accountService.verifyAccountOwnership(id, userId));
    }



}