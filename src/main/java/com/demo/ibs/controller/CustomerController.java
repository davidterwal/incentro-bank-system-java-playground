package com.demo.ibs.controller;

import com.demo.ibs.model.Customer;
import com.demo.ibs.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository repository;

    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> findAll(final Pageable pageable){
        final Page<Customer> page = repository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "name"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> findById(@PathVariable final BigInteger id){
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createCustomer(@RequestBody final Customer customer, final UriComponentsBuilder builder){
        final Customer savedCustomer = repository.save(customer);
        final URI location = builder.path("/customers/{id}").buildAndExpand(savedCustomer.getId()).toUri();
        return ResponseEntity.created(location).build();
    }
}
