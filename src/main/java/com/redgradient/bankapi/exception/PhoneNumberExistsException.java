package com.redgradient.bankapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PhoneNumberExistsException extends RuntimeException {
    public PhoneNumberExistsException(String phoneNumber) {
        super(String.format("User with this phone number %s already exists", phoneNumber));
    }
}
