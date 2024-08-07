package com.abnamro.personalbanking.customer.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FixedDepositAccount extends Account {

    @Column
    private String maturityDate;

}
