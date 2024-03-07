package com.redgradient.bankapi.service;

import com.redgradient.bankapi.dto.UserDto;
import com.redgradient.bankapi.dto.UserUpdateDto;
import com.redgradient.bankapi.dto.security.UserRegistrationDto;
import com.redgradient.bankapi.exception.EmailAlreadyExistsException;
import com.redgradient.bankapi.exception.NoEmailAndPhoneNumberException;
import com.redgradient.bankapi.exception.PhoneNumberAlreadyExistsException;
import com.redgradient.bankapi.model.User;
import com.redgradient.bankapi.repository.DepositRepository;
import com.redgradient.bankapi.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final DepositRepository depositRepository;

    public UserService(UserRepository userRepository,
                       DepositRepository depositRepository) {
        this.userRepository = userRepository;
        this.depositRepository = depositRepository;
    }
    public List<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public User updateUser(Long id, UserUpdateDto userDto) {
        validateEmailAndPhoneNumber(userDto.getEmail(), userDto.getPhoneNumber());

        var userToUpdate = userRepository.findById(id).orElseThrow();

        if (userDto.getEmail() != null) {
            userToUpdate.setEmail(userDto.getEmail());
        }
        if (userDto.getPhoneNumber() != null) {
            userToUpdate.setPhoneNumber(userDto.getPhoneNumber());
        }

        return userRepository.save(userToUpdate);
    }

    public User registerUser(UserRegistrationDto userRegistrationDto) {
        var user = new User();
        user.setEmail(userRegistrationDto.getEmail());
        return user;
    }

    public void deleteUser(Long id) {
        var user = depositRepository.findById(id);
        user.ifPresent(depositRepository::delete);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("User with this username already exists");
        }

        validateEmailAndPhoneNumber(user.getEmail(), user.getPhoneNumber());

        return save(user);
    }

    public void validateEmailAndPhoneNumber(String email, String phoneNumber) {
        var emailEmptyOrNull = (email == null) || email.isEmpty();
        var phoneNumberEmptyOrNull = (phoneNumber == null) || phoneNumber.isEmpty();

        if (emailEmptyOrNull && phoneNumberEmptyOrNull) {
            throw new NoEmailAndPhoneNumberException();
        }

        if (!emailEmptyOrNull && userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        if (!phoneNumberEmptyOrNull && userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new PhoneNumberAlreadyExistsException(phoneNumber);
        }
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    public UserDto toDto(User user) {
        return UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }
}
