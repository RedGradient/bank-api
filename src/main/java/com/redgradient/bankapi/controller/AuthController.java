package com.redgradient.bankapi.controller;

import com.redgradient.bankapi.dto.UserDto;
import com.redgradient.bankapi.dto.security.JwtAuthenticationResponse;
import com.redgradient.bankapi.dto.security.UserLoginDto;
import com.redgradient.bankapi.dto.security.UserRegistrationDto;
import com.redgradient.bankapi.exception.EmailExistsException;
import com.redgradient.bankapi.exception.NoEmailAndPhoneNumberException;
import com.redgradient.bankapi.exception.UsernameExistsException;
import com.redgradient.bankapi.service.security.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "User registration")
    @ApiResponse(responseCode = "200", description = "Token generated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtAuthenticationResponse.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @PostMapping("/register")
    public ResponseEntity<?> signUp(@RequestBody @Valid UserRegistrationDto registrationDto) {
        try {
            return ResponseEntity.ok(authenticationService.signUp(registrationDto));
        } catch (UsernameExistsException ex) {
            return ResponseEntity.badRequest().body("Username already exists");
        } catch (EmailExistsException ex) {
            return ResponseEntity.badRequest().body("Email already exists");
        } catch (NoEmailAndPhoneNumberException ex) {
            return ResponseEntity.badRequest().body("No both email and phone number");
        }
    }

    @Operation(summary = "User authentication")
    @ApiResponse(responseCode = "200", description = "Token generated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtAuthenticationResponse.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @PostMapping("/login")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid UserLoginDto loginDto) {
        return authenticationService.signIn(loginDto);
    }
}