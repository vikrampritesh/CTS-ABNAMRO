package com.abnamro.personalbanking.customer.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

@Entity
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Customer {
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

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String phoneNumber;

    @Column
    private String email;

    /**
     * accountDtos of the Bank Customer
     */
    //@OneToMany(targetEntity=Account.class, cascade=ALL, mappedBy="id", fetch=FetchType.EAGER)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "accounts_id", referencedColumnName = "id")
    List<Account> accounts;

    /**
     * addressDtos of the Bank Customer
     */
    //@OneToMany(targetEntity=Address.class, cascade=ALL, mappedBy="id", fetch=FetchType.EAGER)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "addresses_id", referencedColumnName = "id")
    List <Address> addresses;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

}
