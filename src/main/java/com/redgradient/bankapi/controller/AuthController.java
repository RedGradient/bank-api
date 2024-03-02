package com.redgradient.bankapi.controller;

import com.redgradient.bankapi.dto.security.JwtAuthenticationResponse;
import com.redgradient.bankapi.dto.security.UserLoginDto;
import com.redgradient.bankapi.dto.security.UserRegistrationDto;
import com.redgradient.bankapi.service.security.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid UserRegistrationDto registrationDto) {
        return authenticationService.signUp(registrationDto);
    }

    @PostMapping("/login")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid UserLoginDto loginDto) {
        return authenticationService.signIn(loginDto);
    }
}