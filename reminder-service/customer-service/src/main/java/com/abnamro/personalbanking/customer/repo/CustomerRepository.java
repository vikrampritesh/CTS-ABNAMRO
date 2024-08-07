package com.abnamro.personalbanking.customer.repo;
import com.abnamro.personalbanking.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
