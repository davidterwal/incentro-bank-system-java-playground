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

    public ResponseEntity<List<Account>> findAll() {
        return null;
    }

    public ResponseEntity<Account> findAccountById() {
        return null;
    }

    public ResponseEntity<Set<Account>> findAccountsByCustomerId() {
        return null;
    }

    public ResponseEntity<Void> createAccount() {
        return null;
    }

    public ResponseEntity<Void> transferMoney() {
        return null;
    }
}
