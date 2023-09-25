package com.demo.ibs.controller;

import com.demo.ibs.dto.MoneyTransferDto;
import com.demo.ibs.model.Account;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // starts Spring Boot application on random port
class AccountControllerTests {

    @Autowired
    // helper for running HTTP request to the locally running application
    private TestRestTemplate restTemplate;

    /**
     * "Find all" endpoint tests
     */
    @Test
    void shouldReturnAPageOfAccount() {
        final ResponseEntity<String> response = restTemplate.getForEntity("/accounts?page=0&size=1", String.class);
        final DocumentContext documentContext = JsonPath.parse(response.getBody());
        final JSONArray page = documentContext.read("$[*]");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page).hasSize(1);
    }

    @Test
    void shouldReturnSortedDescendingPageOfAccounts() {
        final ResponseEntity<String> response = restTemplate.getForEntity("/accounts?page=0&size=1&sort=amount,desc", String.class);
        final DocumentContext documentContext = JsonPath.parse(response.getBody());
        final JSONArray page = documentContext.read("$[*]");
        final Double amount = documentContext.read("$[0].amount");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page).hasSize(1);
        assertThat(amount).isEqualTo(500000.00);
    }

    @Test
    void shouldReturnSortedAscendingPageOfAccounts() {
        final ResponseEntity<String> response = restTemplate.getForEntity("/accounts?page=0&size=1&sort=amount,asc", String.class);
        final DocumentContext documentContext = JsonPath.parse(response.getBody());
        final JSONArray page = documentContext.read("$[*]");
        final Double amount = documentContext.read("$[0].amount");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page).hasSize(1);
        assertThat(amount).isEqualTo(100000.00);
    }

    @Test
    void shouldReturnASortedPageOfAccountsWithNoParametersAndUseDefaultValues() {
        final ResponseEntity<String> response = restTemplate.getForEntity("/accounts", String.class);
        final DocumentContext documentContext = JsonPath.parse(response.getBody());
        final JSONArray page = documentContext.read("$[*]");
        final JSONArray amounts = documentContext.read("$..amount");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page).hasSize(3);
        assertThat(amounts).containsExactly(100000.00, 200000.00, 500000.00);
    }

    /**
     * "Find one" account endpoint tests
     */
    @Test
    void shouldReturnAccountWhenDataIsSaved() {
        final ResponseEntity<String> responseEntity = restTemplate.getForEntity("/accounts/1", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // converts response String into a JSON-aware object
        final DocumentContext documentContext = JsonPath.parse(responseEntity.getBody());

        // the response JSON object should contain an id field with value 1
        final Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(1);

        // the response JSON object should contain an amount field with value 200000.00
        final Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(200000.00);
    }

    @Test
    void shouldNotReturnAccountWithAnUnknownId() {
        final ResponseEntity<String> responseEntity = restTemplate.getForEntity("/accounts/999", String.class);

        // Account with id 999 should not exist
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isBlank();
    }

    /**
     * "Find all" for customer endpoint tests
     */
    @Test
    void shouldReturnAccountsForCustomerWhenDataIsSaved() {
        final ResponseEntity<String> responseEntity = restTemplate.getForEntity("/accounts/customer/2", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // converts response String into a JSON-aware object
        final DocumentContext documentContext = JsonPath.parse(responseEntity.getBody());

        // the response JSON object should contain an account id field with value 2 and one with value 3
        final List<Number> accountIds = documentContext.read("$..id");
        assertThat(accountIds).containsOnlyOnce(2, 3);

        // the response JSON object should contain an amount field with value 500000.00 for id = 2 and 100000.00 for id = 3
        final List<Double> amounts = documentContext.read("$..amount");
        assertThat(amounts).containsOnlyOnce(500000.00, 100000.00);
    }

    @Test
    void shouldNotReturnAccountForCustomerWithAnUnknownId() {
        final ResponseEntity<String> responseEntity = restTemplate.getForEntity("/accounts/customer/999", String.class);

        // Customer with id 999 should not exist
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isBlank();
    }

    /**
     * Create new account test
     */
    @Test
    @DirtiesContext
    // causes Spring to start with a clean slate, as if other tests hadn't been run. The created object in this method will be removed
    void shouldCreateNewAccount() {
        final Account account = new Account(null, BigDecimal.valueOf(120000.00), null);
        final ResponseEntity<Void> postResponse = restTemplate.postForEntity("/accounts/1", account, Void.class);
        final URI location = postResponse.getHeaders().getLocation();
        final ResponseEntity<String> getResponse = restTemplate.getForEntity(location, String.class);

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // converts response String into a JSON-aware object
        final DocumentContext documentContext = JsonPath.parse(getResponse.getBody());

        // the response JSON object should contain an id field which is not null
        final Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();

        // the response JSON object should contain an amount field with value 120000.00
        final Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(120000.00);
    }

    /**
     * Update account tests
     */
    @Test
    @DirtiesContext
    // causes Spring to start with a clean slate, as if other tests hadn't been run. The created object in this method will be removed
    void shouldUpdateAnExistingAccount() {
        final MoneyTransferDto moneyTransferDto = new MoneyTransferDto(BigInteger.valueOf(1), BigInteger.valueOf(2), BigDecimal.valueOf(50000));
        final HttpEntity<MoneyTransferDto> entity = new HttpEntity<>(moneyTransferDto);
        final ResponseEntity<Void> putResponse = restTemplate
                .exchange("/accounts/transfer", HttpMethod.PUT, entity, Void.class);
        final ResponseEntity<String> getSenderAccountResponse = restTemplate.getForEntity("/accounts/1", String.class);
        final ResponseEntity<String> getReceiverAccountResponse = restTemplate.getForEntity("/accounts/2", String.class);

        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(getSenderAccountResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getReceiverAccountResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        final DocumentContext senderDocumentContext = JsonPath.parse(getSenderAccountResponse.getBody());
        final Double senderAmount = senderDocumentContext.read("$.amount");
        assertThat(senderAmount).isEqualTo(150000.00);

        final DocumentContext receiverDocumentContext = JsonPath.parse(getReceiverAccountResponse.getBody());
        final Double receiverAmount = receiverDocumentContext.read("$.amount");
        assertThat(receiverAmount).isEqualTo(550000.00);
    }

    @Test
    void shouldNotUpdateAAccountThatDoesNotExist() {
        final MoneyTransferDto moneyTransferDto = new MoneyTransferDto(BigInteger.valueOf(999), BigInteger.valueOf(2), BigDecimal.valueOf(50000));
        final HttpEntity<MoneyTransferDto> entity = new HttpEntity<>(moneyTransferDto);
        final ResponseEntity<Void> response = restTemplate
                .exchange("/accounts/transfer", HttpMethod.PUT, entity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
