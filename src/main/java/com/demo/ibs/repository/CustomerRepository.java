package com.demo.ibs.repository;

import com.demo.ibs.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigInteger;

public interface CustomerRepository extends JpaRepository<Customer, BigInteger>, PagingAndSortingRepository<Customer, BigInteger> {
}
