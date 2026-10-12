package com.zouhir.neobank.account;

import com.zouhir.neobank.account.dto.AccountBalanceDto;
import com.zouhir.neobank.security.JwtService;
import com.zouhir.neobank.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
@DisplayName("Account Controller Tests")
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private JwtService jwtService;

    private User principal;
    private Authentication auth;

    @BeforeEach
    public void setUp(){
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
    @DisplayName("Should return account balance when the authenticated user is the owner")
    public void should_return_balance_when_user_is_account_owner() throws Exception{
        //GIVNE
        UUID id = UUID.randomUUID();
        Account account = Account
                .builder()
                .id(id)
                .user(principal)
                .balance(new BigDecimal("100"))
                .build();
        AccountBalanceDto balance = AccountBalanceDto
                .builder()
                .balance(new BigDecimal("100"))
                .build();

        String jsonAccount = jsonMapper.writeValueAsString(account);

        given(accountService.verifyAccountOwnership(any(), any()))
                .willReturn(true);

        given(accountService.balance(id))
                .willReturn(balance);

        //WHEN and THEN
        mockMvc.perform(get("/api/v1/accounts/"+id+"/balance")
                .with(authentication(auth))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonAccount)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(new BigDecimal("100")));
    }

    @Test
    @DisplayName("Should return 403 Forbidden when the authenticated user does not own the account")
    public void should_return_forbidden_when_user_is_not_account_owner() throws Exception{
        //GIVNE
        UUID id = UUID.randomUUID();
        Account account = Account
                .builder()
                .id(id)
                .user(principal)
                .balance(new BigDecimal("100"))
                .build();

        String jsonAccount = jsonMapper.writeValueAsString(account);

        given(accountService.verifyAccountOwnership(any(), any()))
                .willReturn(false);

        //WHEN and THEN
        mockMvc.perform(get("/api/v1/accounts/"+id+"/balance")
                        .with(authentication(auth))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAccount)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}