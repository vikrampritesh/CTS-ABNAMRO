package com.abnamro.personalbanking.basedomains.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Account {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long id;

    @Column(length = 100)
    private String accountNumber;

    @Column
    private Double principalAmount;

    @Column
    private Double interestRate;

    @Column
    private Double interestAmount;

    @Column
    private Double balance;

    @Column
    private String dateOfOpening;

    @Column
    private String dateOfClosing;

    @Column(length = 100)
    private String status;

    @Column(length = 100)
    private String accountType;

    @Column
    private Boolean isSeniorCitizen;

    @Column
    private Long months;

    @Column
    private Long years;

    //@ManyToOne(fetch = FetchType.EAGER)
    //@JoinColumn(name = "accounts_id", insertable=false, updatable=false)
    //@JsonIgnore
   // private Customer accounts;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

}
