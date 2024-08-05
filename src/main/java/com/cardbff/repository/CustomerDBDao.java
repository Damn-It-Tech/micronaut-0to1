package com.cardbff.repository;


import com.cardbff.exceptions.DatabaseOperationException;
import com.cardbff.model.Customer;

import java.util.Optional;

public interface CustomerDBDao {
    public void createUser(String name, String dob, String pan, String mobile, String email) throws DatabaseOperationException;

    public void updatePanVerificationStatus(String pan, boolean status) throws DatabaseOperationException;

    public Optional<Customer> getCustomerByMobile(String mobile) throws DatabaseOperationException;

    public Optional<Customer> getCustomerByPan(String pan) throws DatabaseOperationException;
}

