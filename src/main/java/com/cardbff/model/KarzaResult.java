package com.cardbff.model;

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
public class KarzaResult {
    String status;
    boolean dobMatch;
    boolean nameMatch;
}
