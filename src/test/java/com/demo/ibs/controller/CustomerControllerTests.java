package com.demo.ibs.controller;

import com.demo.ibs.model.Customer;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // starts Spring Boot application on random port
class CustomerControllerTests {

    @Autowired
    // helper for running HTTP request to the locally running application
    private TestRestTemplate template;

    /**
     * "Find all" endpoint tests
     */
    @Test
    void shouldReturnAPageOfCustomer(){
        final ResponseEntity<String> response = template.getForEntity("/customers?page=0&size=1", String.class);
        final DocumentContext documentContext = JsonPath.parse(response.getBody());
        final JSONArray page = documentContext.read("$[*]");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page).hasSize(1);
    }

    @Test
    void shouldReturnSortedDescendingPageOfCustomers(){
        final ResponseEntity<String> response = template.getForEntity("/customers?page=0&size=1&sort=name,desc", String.class);
        final DocumentContext documentContext = JsonPath.parse(response.getBody());
        final JSONArray page = documentContext.read("$[*]");
        final String name = documentContext.read("$[0].name");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page).hasSize(1);
        assertThat(name).isEqualTo("Marcel Peereboom");
    }

    @Test
    void shouldReturnSortedAscendingPageOfCustomers(){
        final ResponseEntity<String> response = template.getForEntity("/customers?page=0&size=1&sort=name,asc", String.class);
        final DocumentContext documentContext = JsonPath.parse(response.getBody());
        final JSONArray page = documentContext.read("$[*]");
        final String name = documentContext.read("$[0].name");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page).hasSize(1);
        assertThat(name).isEqualTo("David ter Wal");
    }

    @Test
    void shouldReturnASortedPageOfCustomersWithNoParametersAndUseDefaultValues(){
        final ResponseEntity<String> response = template.getForEntity("/customers", String.class);
        final DocumentContext documentContext = JsonPath.parse(response.getBody());
        final JSONArray page = documentContext.read("$[*]");
        final List<String> names = documentContext.read("$..name");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page).hasSize(2);
        assertThat(names).containsExactly("David ter Wal", "Marcel Peereboom");
    }

    /**
     * "Find one" customer endpoint tests
     */
    @Test
    void shouldReturnCustomerWhenDataIsSaved(){
        final ResponseEntity<String> response = template.getForEntity("/customers/1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // converts response String into a JSON-aware object
        final DocumentContext documentContext = JsonPath.parse(response.getBody());

        // the response JSON object should contain an id field with value 1
        final Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(1);

        // the response JSON object should contain a name field with value Marcel Peereboom
        final String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("Marcel Peereboom");

    }

    @Test
    void shouldNotReturnCustomerWithAnUnknownId(){
        final ResponseEntity<String> response = template.getForEntity("/customers/999", String.class);

        // Customer with id 999 should not exist
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    /**
     * Create new customer test
     */
    @Test
    @DirtiesContext // causes Spring to start with a clean slate, as if other tests hadn't been run. The created object in this method will be removed
    void shouldCreateNewCustomer(){
        final Customer customer = new Customer(null, "Hans Timmerman", null);
        final ResponseEntity<Void> postResponse = template.postForEntity("/customers", customer, Void.class);
        final URI location = postResponse.getHeaders().getLocation();
        final ResponseEntity<String> getResponse = template.getForEntity(location, String.class);

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // converts response String into a JSON-aware object
        final DocumentContext documentContext = JsonPath.parse(getResponse.getBody());

        // the response JSON object should contain an id field which is not null
        final Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();

        // the response JSON object should contain an amount field with value Hans Timmerman
        final String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("Hans Timmerman");

    }
}
