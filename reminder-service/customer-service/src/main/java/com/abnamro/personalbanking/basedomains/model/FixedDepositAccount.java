package com.abnamro.personalbanking.basedomains.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;


@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FixedDepositAccount extends Account {

    @Column
    private String maturityDate;

}
