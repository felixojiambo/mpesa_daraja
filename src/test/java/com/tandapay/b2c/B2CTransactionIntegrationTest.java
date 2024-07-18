package com.tandapay.b2c;

import com.tandapay.b2c.dtos.InternalB2CTransactionRequest;
import com.tandapay.b2c.repository.B2CEntriesRepository;
import documents.B2C_Entries;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class B2CTransactionIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private B2CEntriesRepository repository;

    @Test
    void testReceiveB2CRequest() {
        InternalB2CTransactionRequest request = new InternalB2CTransactionRequest();
        request.setTransactionId("123");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/b2c/request", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("B2C request received and processing");

        B2C_Entries entry = repository.findByTransactionId("123").orElse(null);
        assertThat(entry).isNotNull();
        assertThat(entry.getTransactionId()).isEqualTo("123");
        assertThat(entry.getStatus()).isEqualTo("PENDING");
    }
}

