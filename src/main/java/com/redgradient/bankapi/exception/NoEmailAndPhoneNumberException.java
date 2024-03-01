package com.redgradient.bankapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoEmailAndPhoneNumberException extends RuntimeException {
    public NoEmailAndPhoneNumberException() {
        super("At least one of the phone or email must be filled out");
    }
}
