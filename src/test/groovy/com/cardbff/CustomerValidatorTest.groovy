package com.cardbff

import com.cardbff.exceptions.CustomerValidationException
import com.cardbff.model.Customer
import com.cardbff.utils.CustomerValidator
import io.micronaut.http.HttpStatus
import spock.lang.Specification
import spock.lang.Unroll

class CustomerValidatorTest extends Specification {

    def "should not throw exception for valid customer"() {
        given:
        def validCustomer = new Customer(
                name: "John Doe",
                dob: "01/01/1990",
                pan: "ABCDE1234F",
                mobileNo: "9876543210",
                email: "john.doe@example.com"
        )

        when:
        CustomerValidator.validateCustomer(validCustomer)

        then:
        noExceptionThrown()
    }

    def "should throw exception for null name"() {
        given:
        def customer = new Customer(
                name: null,
                dob: "01/01/1990",
                pan: "ABCDE1234F",
                mobileNo: "9876543210",
                email: "john.doe@example.com"
        )

        when:
        CustomerValidator.validateCustomer(customer)

        then:
        def exception = thrown(CustomerValidationException)
        exception.message == "Name cannot be null"
    }

    @Unroll
    def "should throw exception for invalid DOB: #invalidDob"() {
        given:
        def customer = new Customer(
                name: "John Doe",
                dob: invalidDob,
                pan: "ABCDE1234F",
                mobileNo: "9876543210",
                email: "john.doe@example.com"
        )

        when:
        CustomerValidator.validateCustomer(customer)

        then:
        def exception = thrown(CustomerValidationException)
        exception.message == "Invalid DOB"

        where:
        invalidDob << ["32/01/1990", "01/13/1990", "01/01/90", "01-01-1990"]
    }

    @Unroll
    def "should throw exception for invalid PAN: #invalidPan"() {
        given:
        def customer = new Customer(
                name: "John Doe",
                dob: "01/01/1990",
                pan: invalidPan,
                mobileNo: "9876543210",
                email: "john.doe@example.com"
        )

        when:
        CustomerValidator.validateCustomer(customer)

        then:
        def exception = thrown(CustomerValidationException)
        exception.message == "Invalid PAN format"

        where:
        invalidPan << ["ABCDE12345", "abcde1234f", "ABC1234G", "ABCD1234EF"]
    }

    @Unroll
    def "should throw exception for invalid mobile number: #invalidMobile"() {
        given:
        def customer = new Customer(
                name: "John Doe",
                dob: "01/01/1990",
                pan: "ABCDE1234F",
                mobileNo: invalidMobile,
                email: "john.doe@example.com"
        )

        when:
        CustomerValidator.validateCustomer(customer)

        then:
        def exception = thrown(CustomerValidationException)
        exception.message == "Invalid mobile number format"

        where:
        invalidMobile << ["98765432100", "5876543210", "987654321", "abcdefghij"]
    }

    @Unroll
    def "should throw exception for invalid email: #invalidEmail"() {
        given:
        def customer = new Customer(
                name: "John Doe",
                dob: "01/01/1990",
                pan: "ABCDE1234F",
                mobileNo: "9876543210",
                email: invalidEmail
        )

        when:
        CustomerValidator.validateCustomer(customer)

        then:
        def exception = thrown(CustomerValidationException)
        exception.message == "Invalid email format"

        where:
        invalidEmail << ["john.doe@", "@example.com", "john.doe@example", "john.doe@example"]
    }
}