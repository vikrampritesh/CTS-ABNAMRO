package com.abnamro.personalbanking.basedomains.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MaturityEvent extends CustomerRequest {
    String eventId;
    String customerName;
    String correlationId;
    Double maturityAmount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    String dateOfMaturity;
    Remainder remainder;
}
