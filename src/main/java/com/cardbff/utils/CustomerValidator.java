package com.cardbff.utils;

import com.cardbff.exceptions.CustomerValidationException;
import com.cardbff.model.Customer;
import io.micronaut.http.HttpStatus;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class CustomerValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
    private static final Pattern PAN_PATTERN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern DOB_PATTERN = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$");

    public static void validateCustomer(Customer customer) throws CustomerValidationException {
        validateField(customer::getName, "Name cannot be null");
        validateField(customer::getDob, DOB_PATTERN.asPredicate(), "Invalid DOB");
        validateField(customer::getPan, PAN_PATTERN.asPredicate(), "Invalid PAN format");
        validateField(customer::getMobileNo, MOBILE_PATTERN.asPredicate(), "Invalid mobile number format");
        validateField(customer::getEmail, EMAIL_PATTERN.asPredicate(), "Invalid email format");
    }

    private static void validateField(Supplier<?> fieldSupplier, String errorMessage) throws CustomerValidationException {
        if (Objects.isNull(fieldSupplier.get())) {
            throw new CustomerValidationException(errorMessage);
        }
    }

    private static <T> void validateField(Supplier<T> fieldSupplier, Predicate<T> validationPredicate, String errorMessage) throws CustomerValidationException {
        T value = fieldSupplier.get();
        if (Objects.isNull(value) || !validationPredicate.test(value)) {
            throw new CustomerValidationException(errorMessage);
        }
    }
}