package com.redgradient.bankapi.controller;

import com.redgradient.bankapi.dto.UserDto;
import com.redgradient.bankapi.dto.UserUpdateDto;
import com.redgradient.bankapi.model.User;
import com.redgradient.bankapi.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {
    private final UserService userService;
    private static final int PAGE_SIZE = 10;
    private static final String ID = "/{id}";
    private static final String PAGE = "/{page}";
    private static final String AUTHENTICATED = "isAuthenticated()";
    private static final String ONLY_OWNER = """
        @userRepository.findById(#id).get().getUsername() == authentication.name
        """;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(PAGE)
    public List<UserDto> getUsers(@PathVariable int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return userService.getUsers(pageable).stream()
                .map(userService::toDto)
                .toList();
    }

    @PreAuthorize(AUTHENTICATED + " and " + ONLY_OWNER)
    @PutMapping(ID)
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserUpdateDto userDto) {
        var updatedUser = userService.updateUser(id, userDto);
        return userService.toDto(updatedUser);
    }
}
