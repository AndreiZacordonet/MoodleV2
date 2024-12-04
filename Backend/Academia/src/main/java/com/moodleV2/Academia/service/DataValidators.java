package com.moodleV2.Academia.service;

import com.moodleV2.Academia.exceptions.InvalidFieldException;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;

import java.util.function.Supplier;

public class DataValidators {

    public static void numeValidator(String nume, int minLen, int maxLen, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (nume != null && (nume.length() > maxLen || nume.length() < minLen || nume.isBlank() || !nume.matches("[a-zA-Z ]+"))) {
            throw exceptionSupplier.get();
        }
    }

    public static void emailValidator(String email, Supplier<? extends RuntimeException> exceptionSupplier) {
        EmailValidator emailValidator = new EmailValidator();
        if (email != null && (email.isBlank() || email.length() > 50 || !emailValidator.isValid(email, null))) {
            throw exceptionSupplier.get();
        }
    }
}
