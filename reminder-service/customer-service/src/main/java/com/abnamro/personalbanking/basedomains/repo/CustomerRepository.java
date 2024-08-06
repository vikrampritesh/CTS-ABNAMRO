package com.abnamro.personalbanking.basedomains.repo;
import com.abnamro.personalbanking.basedomains.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
