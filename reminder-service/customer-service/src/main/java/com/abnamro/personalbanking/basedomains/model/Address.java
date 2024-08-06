package com.abnamro.personalbanking.basedomains.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.time.OffsetDateTime;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Address {

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
    private String streetAddress1;

    @Column
    private String streetAddress2;

    @Column
    private String state;

    @Column
    private String city;

    @Column
    private Integer zip;

  //  @ManyToOne(fetch = FetchType.EAGER)
   // @JoinColumn(name = "address_id", insertable=false, updatable=false)
   // private Customer addresses;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @JsonIgnore
    private OffsetDateTime dateCreated;

}
