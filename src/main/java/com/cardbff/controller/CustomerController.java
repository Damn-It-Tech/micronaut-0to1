package com.cardbff.controller;

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
                customerService.createCustomer(customer);
                return HttpResponse.ok(String.format("Customer %s added successfully", customer.getName()));
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
                .map(HttpResponse::ok);
    }

    @Get("/getCustomer/{mobile}")
    public Flowable<MutableHttpResponse<Customer>> findCustomerByMobile(@PathVariable String mobile){

        return Flowable.fromCallable(() ->
                        customerService.getCustomerByMobile(mobile))
                .subscribeOn(Schedulers.computation())
                .map(HttpResponse::ok);
    }

    @Post("/verify")
    public Flowable<MutableHttpResponse<String>> verifyPanStatus(@Body String customerPan) throws Exception{
        return Flowable.fromCallable(() ->
                {
                    customerService.verifyPanDetails(customerPan);
                    return "Pan Details Verified";
                })
                .subscribeOn(Schedulers.computation())
                .map(HttpResponse::ok);
    }
}