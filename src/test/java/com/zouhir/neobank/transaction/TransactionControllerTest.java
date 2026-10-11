package com.zouhir.neobank.transaction;

import com.zouhir.neobank.common.exceptions.AccountNotFoundException;
import com.zouhir.neobank.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import tools.jackson.databind.json.JsonMapper;
import com.zouhir.neobank.account.AccountService;
import com.zouhir.neobank.security.JwtService;
import com.zouhir.neobank.transaction.dto.TransactionRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
@DisplayName("Transaction Controller Tests")
class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private JwtService jwtService;

    private UUID fromId;
    private UUID toId;

    private User principal;
    private Authentication auth;

    @BeforeEach
    public void setUp(){
        fromId = UUID.randomUUID();
        toId = UUID.randomUUID();

        principal = User
                .builder()
                .id(UUID.randomUUID())
                .email("zouhir@example.com")
                .passwordHash("pwd")
                .build();

        auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

    }

    @Test
    @DisplayName("Should successfully transfer funds and return 200 OK when balance is sufficient")
    public void should_transfer_funds_when_balance_is_sufficient() throws Exception{
        //GIVEN

        TransactionRequestDto request = TransactionRequestDto
                .builder()
                .fromAccountId(fromId)
                .toAccountId(toId)
                .amount(new BigDecimal("100"))
                .reference("reference")
                .build();

        String jsonRequest = jsonMapper.writeValueAsString(request);

        given(accountService.verifyAccountOwnership(fromId, principal.getId()))
                .willReturn(true);

        //WHEN and THEN
        mockMvc.perform(post("/api/v1/transaction/transfer")
                .with(authentication(auth))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                )
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Should return 400 Bad Request when the transfer amount is negative")
    public void should_fail_transfer_when_amount_is_negative() throws Exception{
        //GIVEN

        TransactionRequestDto request = TransactionRequestDto
                .builder()
                .fromAccountId(fromId)
                .toAccountId(toId)
                .amount(new BigDecimal("-100"))
                .reference("reference")
                .build();

        String jsonRequest = jsonMapper.writeValueAsString(request);

        given(accountService.verifyAccountOwnership(fromId, principal.getId()))
                .willReturn(true);

        //WHEN and THEN
        mockMvc.perform(post("/api/v1/transaction/transfer")
                        .with(authentication(auth))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isBadRequest());


    }

    @Test
    @DisplayName("Should return 400 Bad Request when the transfer amount is null")
    public void should_fail_transfer_when_amount_is_null() throws Exception{
        //GIVEN

        TransactionRequestDto request = TransactionRequestDto
                .builder()
                .fromAccountId(fromId)
                .toAccountId(toId)
                .amount(null)
                .reference("reference")
                .build();


        String jsonRequest = jsonMapper.writeValueAsString(request);

        given(accountService.verifyAccountOwnership(fromId, principal.getId()))
                .willReturn(true);

        //WHEN and THEN
        mockMvc.perform(post("/api/v1/transaction/transfer")
                        .with(authentication(auth))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return RFC 7807 format when account not found")
    public void should_return_rfc7807_when_account_not_found() throws Exception{
        //GIVEN
        TransactionRequestDto request = TransactionRequestDto
                .builder()
                .fromAccountId(fromId)
                .toAccountId(toId)
                .amount(new BigDecimal("100"))
                .reference("reference")
                .build();


        given(transactionService.transfer(any(), any(), any(), any()))
                .willThrow(new AccountNotFoundException("Error : The source account does not exist"));
        String jsonRequest = jsonMapper.writeValueAsString(request);

        given(accountService.verifyAccountOwnership(fromId, principal.getId()))
                .willReturn(true);

        //WHEN and THEN
        mockMvc.perform(post("/api/v1/transaction/transfer")
                        .with(authentication(auth))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Error : The source account does not exist"))
                .andExpect(jsonPath("$.title").value("Account Not Found"));


    }

}