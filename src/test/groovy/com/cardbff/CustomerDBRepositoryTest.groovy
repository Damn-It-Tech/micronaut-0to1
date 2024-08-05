package com.cardbff

import com.cardbff.repository.CustomerDBRepository
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification
import spock.lang.Subject
import javax.sql.DataSource

@MicronautTest
class CustomerDBRepositoryTest extends Specification {

    @Inject
    DataSource dataSource

    @Subject
    @Inject
    CustomerDBRepository customerDBRepository

    /*
    Note: Before running this test make sure the row isn't present in the database. Otherwise it will fail.
    * */
    def "createUser should insert a new customer"() {
        given:
        println "DataSource: ${dataSource}"
        println "CustomerDBRepository: ${customerDBRepository}"

        assert dataSource != null : "DataSource is null"
        assert customerDBRepository != null : "CustomerDBRepository is null"

        def name = "John Doe"
        def dob = "1990-01-01"
        def pan = "ABCDE1234F"
        def mobile = "1234567890"
        def email = "john@example.com"

        when:
        customerDBRepository.createUser(name, dob, pan, mobile, email)

        then:
        def customer = customerDBRepository.getCustomerByPan(pan)
        println "Retrieved customer: ${customer}"

        assert customer.isPresent() : "Customer not found"

        def retrievedCustomer = customer.get()
        assert retrievedCustomer.name == name
        assert retrievedCustomer.dob == dob
        assert retrievedCustomer.pan == pan
        assert retrievedCustomer.mobileNo == mobile
        assert retrievedCustomer.email == email
        assert !retrievedCustomer.verified
    }
}