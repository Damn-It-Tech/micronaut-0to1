package com.cardbff

import com.cardbff.controller.CustomerController
import com.cardbff.exceptions.CustomerNotFoundException
import com.cardbff.exceptions.CustomerValidationException
import com.cardbff.exceptions.DatabaseOperationException
import com.cardbff.model.Customer
import com.cardbff.service.CustomerService
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification
import spock.lang.Unroll

@MicronautTest
class CustomerControllerTest extends Specification {

    @Inject
    CustomerController customerController

    @MockBean(CustomerService)
    CustomerService customerService = Mock(CustomerService)

    @Unroll
    def "test createCustomer with #description"() {
        given:
        def customer = Customer.builder()
                .name("Mehul")
                .mobileNo("8234567890")
                .email("mj@gmail.com")
                .dob("01/01/2001")
                .pan("ABCDE1234F")
                .isVerified(false)
                .build()

        when:
        def response = customerController.createCustomer(customer).blockingFirst()

        then:
        1 * customerService.createCustomer(customer) >> {
            if (exceptionToThrow) throw exceptionToThrow
        }

        response.body() == expectedBody
        response.status == expectedStatus

        where:
        description                       | exceptionToThrow                     | expectedStatus | expectedBody
        "successful creation"             | null                                 | HttpStatus.OK            | "Customer Mehul added successfully"
        "validation exception"    | new CustomerValidationException("Invalid data") | HttpStatus.BAD_REQUEST | "Invalid customer data: Invalid data"
        "database operation exception"    | new DatabaseOperationException("error") | HttpStatus.INTERNAL_SERVER_ERROR | "Some database exception occurred: DB error"
        "generic exception"               | new RuntimeException("Generic error") | HttpStatus.INTERNAL_SERVER_ERROR    | "Some exception occurred: Generic error"
    }

    @Unroll
    def "test findCustomerByMobile with #description"() {
        given:
        def mobileNo = "1234567890"
        def customer = Customer.builder()
                .name("John Doe")
                .mobileNo(mobileNo)
                .email("john.doe@example.com")
                .dob("01/01/1990")
                .pan("ABCDE1234F")
                .isVerified(false)
                .build()

        when:
        def response = customerController.findCustomerByMobile(mobileNo).blockingFirst()

        then:
        1 * customerService.getCustomerByMobile(mobileNo) >> { if (exceptionToThrow) throw exceptionToThrow else return customer }

        response.status == expectedStatus
        response.body() == expectedBody

        where:
        description               | exceptionToThrow                      | expectedStatus | expectedBody
        "customer found"          | null                                  | HttpStatus.OK            | new Customer(name: "John Doe", mobileNo: "1234567890", email: "john.doe@example.com", dob: "01/01/1990", pan: "ABCDE1234F", isVerified: false)
        "customer not found"      | new CustomerNotFoundException("Not found") | HttpStatus.NOT_FOUND            | null
        "generic exception"       | new RuntimeException("Generic error") | HttpStatus.INTERNAL_SERVER_ERROR            | null
    }

    @Unroll
    def "test verifyPanStatus with #description"() {
        given:
        def customerPan = "ABCDE1234F"

        when:
        def response = customerController.verifyPanStatus(customerPan).blockingFirst()

        then:
        1 * customerService.verifyPanDetails(customerPan) >> { if (exceptionToThrow) throw exceptionToThrow }

        response.status == expectedStatus
        response.body() == expectedBody

        where:
        description               | exceptionToThrow                      | expectedStatus | expectedBody
        "pan verified"            | null                                  | HttpStatus.OK            | "Pan Details Verified"
        "customer not found"      | new CustomerNotFoundException("Not found") | HttpStatus.NOT_FOUND            | null
        "generic exception"       | new RuntimeException("Generic error") | HttpStatus.INTERNAL_SERVER_ERROR            | "Generic error"
    }
}
