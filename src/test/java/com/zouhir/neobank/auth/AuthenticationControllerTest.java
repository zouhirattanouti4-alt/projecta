package com.zouhir.neobank.auth;

import com.zouhir.neobank.auth.Dto.AuthenticationResponseDto;
import com.zouhir.neobank.auth.Dto.RegisterRequestDto;
import com.zouhir.neobank.security.JwtService;
import com.zouhir.neobank.user.User;
import com.zouhir.neobank.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class)
@DisplayName("Authentication Controller Test")
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;



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
    @DisplayName("should successfully register and return 201 CREATED and JWT token when valid infos are valid")
    public void should_register_when_valid_infos_are_valid() throws Exception{
        //GIVEN
        AuthenticationResponseDto response = AuthenticationResponseDto
                .builder()
                .jwt("fake_jwt_token")
                .build();

        RegisterRequestDto request = RegisterRequestDto
                .builder()
                .email("zouhir@example.com")
                .password("psw")
                .fullName("zouhir attanouti")
                .build();

        given(userRepository.existsByEmail("zouhir@example.com"))
                .willReturn(false);

        given(jwtService.generateToken(any()))
                .willReturn("fake_jwt_token");

        given(authenticationService.register(request))
                .willReturn(response);

        String jsonRequest = jsonMapper.writeValueAsString(request);

        //WHEN and THEN
        mockMvc.perform(post("/api/v1/auth/register")
                .with(authentication(auth))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jwt").value("fake_jwt_token"));
    }

    @Test
    @DisplayName("should reject registering user and return 400 BAD REQUEST when given email is not valid")
    public void should_reject_registering_when_email_is_not_valid() throws Exception{
        //GIVEN
        RegisterRequestDto request = RegisterRequestDto
                .builder()
                .email(null)
                .password("psw")
                .fullName("Zouhir")
                .build();


        given(userRepository.existsByEmail(any()))
                .willReturn(false);
        given(jwtService.generateToken(principal))
                .willReturn("fake_jwt_token");

        String jsonRequest = jsonMapper.writeValueAsString(request);

        //WHEN and THEN
        mockMvc.perform(post("/api/v1/auth/register")
                .with(authentication(auth))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


}