package com.redgradient.bankapi.controller;

import com.redgradient.bankapi.dto.UserDto;
import com.redgradient.bankapi.dto.UserUpdateDto;
import com.redgradient.bankapi.dto.security.UserLoginDto;
import com.redgradient.bankapi.dto.security.UserRegistrationDto;
import com.redgradient.bankapi.model.User;
import com.redgradient.bankapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {
    private final UserService userService;
    private static final String ID = "/{id}";
    private static final String AUTHENTICATED = "isAuthenticated()";
    private static final String ONLY_OWNER_BY_ID = """
        @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.name
        """;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Iterable<User> getUsers() {
        return userService.getUsers();
    }

//    @PreAuthorize(AUTHENTICATED + " and " + ONLY_OWNER_BY_ID)
    @PutMapping(ID)
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserUpdateDto userDto) {
        var updatedUser = userService.updateUser(id, userDto);
        return userService.toDto(updatedUser);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto userDto) {
        userService.registerUser(userDto);
        return ResponseEntity.ok("User registered successfully");
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto userDTO) {
        // Perform authentication logic
        // Return appropriate response or token
        return ResponseEntity.ok("Login successful");
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Perform logout logic
        // Invalidate token or session
        return ResponseEntity.ok("Logout successful");
    }
}
