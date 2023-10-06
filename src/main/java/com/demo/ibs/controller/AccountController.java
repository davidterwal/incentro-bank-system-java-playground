package com.demo.ibs.controller;

import com.demo.ibs.model.Account;
import com.demo.ibs.repository.AccountRepository;
import com.demo.ibs.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public AccountController(AccountRepository repository, AccountService accountService) {
        this.accountRepository = repository;
        this.accountService = accountService;
    }

    //TODO: Add mapping
    public ResponseEntity<List<Account>> findAll() {
        //TODO: Retrieve a list of all accounts, with pagination, of all customers from the repository and return it with the proper response code
        return null;
    }

    //TODO: Add mapping
    public ResponseEntity<Account> findAccountById() {
        //TODO: Retrieve an account by it's id from the repository and return it with the proper response code
        //TODO: If account is found, return status 'OK', else return status 'NOT FOUND'
        return null;
    }

    //TODO: Add mapping
    public ResponseEntity<Set<Account>> findAccountsByCustomerId() {
        //TODO: Retrieve a set of all accounts of a customer from the repository and return it with the proper response code
        //TODO: If the customer has accounts, return status 'OK', else return status 'NOT FOUND'
        return null;
    }

    //TODO: Add mapping
    public ResponseEntity<Void> createAccount() {
        //TODO: Save a new account into the repository and return a ResponseEntity with the proper response code and location header
        //TODO: If account is created, return status 'OK', else return status 'BAD REQUEST'
        return null;
    }

    //TODO: Add mapping
    public ResponseEntity<Void> transferMoney() {
        //TODO: Retrieve both the sender and receiver account
        //TODO: If both are present, execute the transferMoney() method in the AccountService class and return status 'NO CONTENT', else return status 'NOT FOUND'
        return null;
    }
}
