package com.demo.ibs.service;

import com.demo.ibs.repository.AccountRepository;
import com.demo.ibs.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public AccountService(CustomerRepository customerRepository, AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transferMoney() {
        //TODO: Calcute the new sender balance and receiver balance
        //TODO: Update both accounts in the AccountRepository
    }
}
