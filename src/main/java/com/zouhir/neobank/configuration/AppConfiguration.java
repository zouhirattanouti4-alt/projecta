package com.zouhir.neobank.configuration;


import com.zouhir.neobank.user.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
public class AppConfiguration {
    private final UserRepository userRepository;

    @Bean
    UserDetailsService userDetailsService(){
        return username -> userRepository.findByEmail(username)
                .orElseThrow( () -> new UsernameNotFoundException("User not found"));
    }
}
