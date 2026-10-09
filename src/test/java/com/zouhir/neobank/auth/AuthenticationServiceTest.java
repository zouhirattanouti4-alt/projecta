package com.zouhir.neobank.auth;

import com.zouhir.neobank.auth.Dto.AuthenticationResponseDto;
import com.zouhir.neobank.auth.Dto.LoginRequestDto;
import com.zouhir.neobank.auth.Dto.RegisterRequestDto;
import com.zouhir.neobank.common.exceptions.EmailAlreadyExistsException;
import com.zouhir.neobank.security.JwtService;
import com.zouhir.neobank.user.User;
import com.zouhir.neobank.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Infrastructure Security Test")
class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;



    @Test
    @DisplayName("should register user successfully when email is unique")
    public void should_register_user_successfully(){
        //GIVEN
        RegisterRequestDto registerRequest = RegisterRequestDto
                .builder()
                .email("email@example.com")
                .password("psw_123")
                .fullName("Zouhir ATTANOUTI")
                .build();


        given(userRepository.existsByEmail("email@example.com"))
                .willReturn(false);
        given(passwordEncoder.encode(registerRequest.password()))
                .willReturn("Hashed password");

        User user = User.builder()
                .email(registerRequest.email())
                .passwordHash(passwordEncoder.encode(registerRequest.password()))
                .fullName(registerRequest.fullName())
                .build();

        given(jwtService.generateToken(user))
                .willReturn("fake_jwt_token");

        //WHEN
        AuthenticationResponseDto response = authenticationService.register(registerRequest);

        //THEN
        assertNotNull(response);
        then(userRepository).should().save(any(User.class));
        assertEquals("fake_jwt_token", response.jwt());
    }

}