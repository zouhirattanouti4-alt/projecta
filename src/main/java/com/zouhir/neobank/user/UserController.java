package com.zouhir.neobank.user;

import com.zouhir.neobank.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> profile(@AuthenticationPrincipal User authenticatedUser){
        return ResponseEntity.status(HttpStatus.OK).body(userService.profile(authenticatedUser.getId()));
    }
}
