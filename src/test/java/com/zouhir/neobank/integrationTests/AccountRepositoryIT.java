package com.zouhir.neobank.integrationTests;

import com.zouhir.neobank.account.Account;
import com.zouhir.neobank.account.AccountRepository;
import com.zouhir.neobank.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Transactional
public class AccountRepositoryIT {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TestEntityManager em;

    private static User createUser(){
        return User.builder().email("user-"+UUID.randomUUID()+"@gmail.com").passwordHash("hashed_password").fullName("zouhir attanouti").build();
    }
    private static Account createAccount(User user, BigDecimal balance){
        return Account.builder().user(user).accountNo("123").balance(balance).build();
    }


    @Test
    void accountRepositoryShouldNotBeNull(){
        assertNotNull(accountRepository);
    }

    @Test
    void shouldPersistAndFindAccountWithCorrectUuidAndBigDecimal(){
        //GIVEN
        User user = createUser();
        User savedUser = em.persistAndFlush(user);
        Account account = createAccount(savedUser, new BigDecimal("100"));
        Account savedAccount = em.persistAndFlush(account);
        em.clear();

        //WHEN
        Account fetchedAccount = accountRepository.findById(savedAccount.getId()).orElseThrow();

        //THEN
        assertEquals(savedAccount.getId(), fetchedAccount.getId());
        assertThat(fetchedAccount.getBalance()).isEqualByComparingTo(new BigDecimal("100"));
        assertEquals(0, fetchedAccount.getVersion());
    }

    @Test
    void shouldReturnTrueWhenAccountBelongsToUser(){
        //GIVEN
        User user = createUser();
        User savedUser = em.persistAndFlush(user);
        Account account = createAccount(savedUser, new BigDecimal("100"));
        Account savedAccount = em.persistAndFlush(account);
        em.clear();

        //WHEN
        boolean exists = accountRepository.existsByIdAndUser_Id(savedAccount.getId(), savedUser.getId());

        //Then
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenAccountBelongsToDifferentUser(){
        //GIVEN
        User userA = createUser();
        User savedUserA = em.persistAndFlush(userA);
        User userB = createUser();
        User savedUserB = em.persistAndFlush(userB);
        Account accountA = createAccount(savedUserA, new BigDecimal("100"));
        Account savedAccountA = em.persistAndFlush(accountA);

        //WHEN
        boolean exists = accountRepository.existsByIdAndUser_Id(savedAccountA.getId(), savedUserB.getId());

        //THEN
        assertFalse(exists);
    }
}
