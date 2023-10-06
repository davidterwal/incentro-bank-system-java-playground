package com.demo.ibs.repository;

import com.demo.ibs.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

public interface AccountRepository extends JpaRepository<Account, BigInteger>, PagingAndSortingRepository<Account, BigInteger> {

    @Query("select a from Account a join a.customer c where c.id = :id")
    Set<Account> findAccountsByCustomerId(final BigInteger id);

    @Modifying
    @Query("update Account a set a.amount = :amount where a.id = :id")
    void updateAccount(final BigInteger id, final BigDecimal amount);
}
