package com.cardbff

import com.cardbff.exceptions.CustomerNotFoundException
import com.cardbff.exceptions.HTTPClientException
import com.cardbff.model.Customer
import com.cardbff.model.KarzaResponseData
import com.cardbff.repository.CustomerDBRepository
import com.cardbff.repository.KarzaRepository
import com.cardbff.service.CustomerService
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification
import io.micronaut.http.HttpResponse

@MicronautTest
class CustomerServiceTest extends Specification {

    @Inject
    CustomerService customerService

    @MockBean(CustomerDBRepository)
    CustomerDBRepository customerDBRepository = Mock(CustomerDBRepository)

    @MockBean(KarzaRepository)
    KarzaRepository karzaRepository = Mock(KarzaRepository)



    def "test createCustomer with successful creation"() {
        given:
        def customer = Customer.builder()
                .name("John Doe")
                .mobileNo("8234567890")
                .email("john.doe@example.com")
                .dob("01/01/1990")
                .pan("ABCDE1234F")
                .isVerified(true)
                .build()

        when:
        customerService.createCustomer(customer)

        then:
        1 * customerDBRepository.createUser(customer.name, customer.dob, customer.pan, customer.mobileNo, customer.email)
    }

    def "test verifyPanDetails successfully"() {
        given:
        def customerPan = "VALID123"
        def customer = new Customer(pan: customerPan, verified: false)

        when:
        customerService.verifyPanDetails(customerPan)

        then:
        1 * customerDBRepository.getCustomerByPan(customerPan) >> Optional.of(customer)
        1 * karzaRepository.getPanStatusFromKarza(customer) >> HttpResponse.ok(new KarzaResponseData(statusCode: "101"))
        1 * customerDBRepository.updatePanVerificationStatus(customerPan, true)
    }

    def "test verifyPanDetails when customer not found"() {
        given:
        def customerPan = "NOTFOUND"

        when:
        customerService.verifyPanDetails(customerPan)

        then:
        1 * customerDBRepository.getCustomerByPan(customerPan) >> Optional.empty()
        thrown(CustomerNotFoundException)
    }

    def "test verifyPanDetails with incorrect input to Karza API"() {
        given:
        def customerPan = "INVALID12"
        def customer = new Customer(pan: customerPan, verified: false)

        when:
        customerService.verifyPanDetails(customerPan)

        then:
        1 * customerDBRepository.getCustomerByPan(customerPan) >> Optional.of(customer)
        1 * karzaRepository.getPanStatusFromKarza(customer) >> HttpResponse.ok(new KarzaResponseData(statusCode: "102"))
        thrown(HTTPClientException)
    }

    def "should throw HTTPClientException for incorrect input from Karza"() {
        given:
        String customerPan = "ABCDE1234F"
        Customer customer = new Customer(pan: customerPan, verified: false)

        when:
        customerService.verifyPanDetails(customerPan)

        then:
        1 * customerDBRepository.getCustomerByPan(customerPan) >> Optional.of(customer)
        1 * karzaRepository.getPanStatusFromKarza(customer) >> Mock(HttpResponse) {
            getBody() >> Optional.of(new KarzaResponseData(statusCode: "102"))
        }
        thrown(HTTPClientException)
    }

    def "should get customer by mobile number"() {
        given:
        String mobile = "9876543210"
        Customer customer = new Customer(name: "John Doe", mobileNo: mobile)

        when:
        Customer result = customerService.getCustomerByMobile(mobile)

        then:
        1 * customerDBRepository.getCustomerByMobile(mobile) >> Optional.of(customer)
        result == customer
    }

    def "should handle exception in getCustomerByMobile"() {
        given:
        String mobile = "9876543210"

        when:
        Customer result = customerService.getCustomerByMobile(mobile)

        then:
        1 * customerDBRepository.getCustomerByMobile(mobile) >> { throw new RuntimeException("Database error") }
        result == null
    }
}
