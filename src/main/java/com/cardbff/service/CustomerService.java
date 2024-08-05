package com.cardbff.service;


import com.cardbff.repository.CustomerDBDao;
import com.cardbff.connectors.CustomerNetworkDao;
import com.cardbff.exceptions.CustomerNotFoundException;
import com.cardbff.exceptions.CustomerValidationException;
import com.cardbff.exceptions.DatabaseOperationException;
import com.cardbff.exceptions.HTTPClientException;
import com.cardbff.interceptor.LogMethods;
import com.cardbff.model.Customer;
import com.cardbff.model.KarzaResponseData;
import com.cardbff.utils.CustomerValidator;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.logging.Logger;

@Singleton
@LogMethods
public class CustomerService {

    private static final Logger logger = Logger.getLogger(CustomerService.class.getName());

    @Inject
    CustomerDBDao customerRepository;

    @Inject
    CustomerNetworkDao karzaRepository;

    public void createCustomer(Customer customer) throws CustomerValidationException, DatabaseOperationException {

        CustomerValidator.validateCustomer(customer);

        customerRepository.createUser(customer.getName(), customer.getDob(),
                customer.getPan(), customer.getMobileNo(), customer.getEmail());
    }

    public void verifyPanDetails(String customerPan) throws DatabaseOperationException, CustomerNotFoundException, HTTPClientException {
        Customer customer = customerRepository.getCustomerByPan(customerPan).orElse(null);

        if(customer == null) {
            throw new CustomerNotFoundException("Customer with PAN: " + customerPan + " not found in database");
        }
        if(!customer.isVerified()) {
            verifyPanStatusFromKarza(customer);
        }

    }

    private void verifyPanStatusFromKarza(Customer customer) throws HTTPClientException {

        HttpResponse<KarzaResponseData> response = karzaRepository.getPanStatusFromKarza(customer);

        if(response != null && response.getBody().isPresent()) {
           String statusCode = response.getBody().get().getStatusCode();
           switch (statusCode) {
               case "101":
                   customerRepository.updatePanVerificationStatus(customer.getPan(), true);
                   break;
               case "102":
                    throw new HTTPClientException("Incorrect input to Karza API, pan number might be wrong");
               case "105":
                   throw new HTTPClientException("Consent was marked as N in the input, please check");
               default:
                   throw new HTTPClientException("Unknown exception occurred from Karza APIs");
           }

        }
    }

    public Customer getCustomerByMobile(String mobile) throws DatabaseOperationException {
        logger.info("inside service get customer by mobile ");

        try {
            return customerRepository.getCustomerByMobile(mobile).orElse(null);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
