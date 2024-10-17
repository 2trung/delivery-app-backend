package com.delivery.delivery_app.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final String PHONE_NUMBER_REGEX = "^(84|0)([3579]\\d{8})$";

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
//        if (phoneNumber == null) {
//            return false;
//        }
//        log.info("Validating phone number: {}", phoneNumber);
        return Pattern.matches(PHONE_NUMBER_REGEX, phoneNumber);
    }
}
