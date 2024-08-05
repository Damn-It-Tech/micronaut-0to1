package com.cardbff.controller;

import com.cardbff.exceptions.CustomerNotFoundException;
import com.cardbff.exceptions.CustomerValidationException;
import com.cardbff.exceptions.DatabaseOperationException;
import com.cardbff.interceptor.LogMethods;
import com.cardbff.model.Customer;
import com.cardbff.service.CustomerService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jakarta.inject.Inject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Controller("/customer")
@LogMethods
public class CustomerController {

    @Inject
    CustomerService customerService;

    /*
    * Done using CompletableFuture, it is same as /create, only difference being different ways of
    * doing reactive programming.
    * */
    @Post("/createNew")
    @ExecuteOn(TaskExecutors.IO)
    public CompletableFuture<MutableHttpResponse<String>> createCustomers(@Body Customer customer) {
        ExecutorService executor = Executors.newCachedThreadPool();

        return CompletableFuture.supplyAsync(() -> {
            try {
                customerService.createCustomer(customer);
                return HttpResponse.ok(String.format("Customer %s added successfully", customer.getName()));
            } catch (CustomerValidationException e) {
                return HttpResponse.badRequest("Invalid customer data: " + e.getLocalizedMessage());
            } catch (DatabaseOperationException e) {
                return HttpResponse.serverError("Some database exception occurred: " + e.getCause().getLocalizedMessage());
            } catch (Exception e) {
                return HttpResponse.serverError("Some exception occurred: " + e.getLocalizedMessage());
            }
        }, executor);
    }

    /*
    * Same as /createNew just done using Flowable instead of CompletableFuture.
    * */
    @Post("/create")
    public Flowable<MutableHttpResponse<String>> createCustomer(@Body Customer customer) {
        return Flowable.fromCallable(() -> {
                    customerService.createCustomer(customer);
                    return String.format("Customer %s added successfully", customer.getName());
                })
                .subscribeOn(Schedulers.io())
                .map(HttpResponse::ok)
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof CustomerValidationException) {
                        return Flowable.just(HttpResponse.badRequest("Invalid customer data: " + throwable.getLocalizedMessage()));
                    } else if (throwable instanceof DatabaseOperationException) {
                        return Flowable.just(HttpResponse.serverError("Some database exception occurred: " + throwable.getCause().getLocalizedMessage()));
                    } else {
                        return Flowable.just(HttpResponse.serverError("Some exception occurred: " + throwable.getLocalizedMessage()));
                    }
                });
    }

    @Get("/getCustomer/{mobile}")
    public Flowable<MutableHttpResponse<Customer>> findCustomerByMobile(@PathVariable String mobile){

        return Flowable.fromCallable(() ->
                {
                    Customer customer = customerService.getCustomerByMobile(mobile);
                    if (customer == null) {
                        throw new CustomerNotFoundException("Customer not found for mobile: " + mobile);
                    }
                    return customer;
                })
                .subscribeOn(Schedulers.computation())
                .map(HttpResponse::ok)
                .onErrorResumeNext(throwable -> {
                    if(throwable instanceof CustomerNotFoundException) {
                        return Flowable.just(HttpResponse.notFound().body(null));
                    }
                    else {
                        return Flowable.just(HttpResponse.serverError(null));
                    }
                });

    }

    @Post("/verify")
    public Flowable<MutableHttpResponse<String>> verifyPanStatus(@Body String customerPan) throws Exception{
        return Flowable.fromCallable(() ->
                {
                    customerService.verifyPanDetails(customerPan);
                    return "Pan Details Verified";
                })
                .subscribeOn(Schedulers.computation())
                .map(HttpResponse::ok)
                .onErrorResumeNext(throwable -> {
                    if(throwable instanceof CustomerNotFoundException) {
                        return Flowable.just(HttpResponse.notFound(throwable.getLocalizedMessage()).body(null));
                    }
                    else {
                        return Flowable.just(HttpResponse.serverError(throwable.getLocalizedMessage()));
                    }
                });
    }

}