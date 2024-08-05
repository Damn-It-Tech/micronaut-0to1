package com.cardbff.connectors;

import com.cardbff.model.Customer;
import com.cardbff.model.KarzaResponseData;
import io.micronaut.http.HttpResponse;

public interface CustomerNetworkDao {
    public HttpResponse<KarzaResponseData> getPanStatusFromKarza(Customer customer);
}
