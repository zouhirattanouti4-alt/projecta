package com.zouhir.neobank.user;

import com.zouhir.neobank.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto profile(UUID userId){
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not Found"));

        return UserDto
                .builder()
                .id(userId)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
}
