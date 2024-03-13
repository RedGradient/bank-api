package com.redgradient.bankapi.controller;

import com.redgradient.bankapi.dto.AdminRequestDto;
import com.redgradient.bankapi.dto.UserDto;
import com.redgradient.bankapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
public class AdminController {
    private final UserService userService;
    @GetMapping
    public void createUserAndBankAccount(@RequestBody AdminRequestDto requestDto) {
        // логин, пароль, изначальную сумму, телефон и email.

        System.out.println("Used admin API to create user, bank account and deposit");
//        var user = createUserWithIncompleteData(
//                requestDto.getUsername(),
//                requestDto.getPassword(),
//                requestDto.getPhoneNumber(),
//                requestDto.getEmail());
//        var bankAccount =;
//        var deposit = Deposit(
//                user.getId()
//        );

    }

    private UserDto createUserWithIncompleteData(String username, String password,
                                                 String phoneNumber, String email) {
//        var user = User.builder()
//                .firstName("Firstname")
//                .lastName("Las")
//                .username(username)
//                .password(password);
        return null;

    }
}
