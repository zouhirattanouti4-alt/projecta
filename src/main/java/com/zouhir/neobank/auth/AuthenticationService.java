package com.zouhir.neobank.auth;

import com.zouhir.neobank.common.exceptions.EmailAlreadyExistsException;
import com.zouhir.neobank.security.JwtService;
import com.zouhir.neobank.user.User;
import com.zouhir.neobank.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponseDto register(RegisterRequestDto request){
        var user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .build();

        if(userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException("Email Already Exists");
        }

        userRepository.save(user);

        var jwt = jwtService.generateToken(user);

        return AuthenticationResponseDto
                .builder()
                .jwt(jwt)
                .build();
    }

    public AuthenticationResponseDto login(LoginRequestDto request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var jwt = jwtService.generateToken(user);

        return AuthenticationResponseDto
                .builder()
                .jwt(jwt)
                .build();
    }
}
