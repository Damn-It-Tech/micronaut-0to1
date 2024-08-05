package com.cardbff.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Introspected
@Builder
@Serdeable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class KarzaResponseData {
    KarzaResult result;
    @JsonProperty("request_id")
    String requestId;
    @JsonProperty("status-code")
    String statusCode;
}
