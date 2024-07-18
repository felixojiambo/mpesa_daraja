package com.tandapay.b2c;

import com.tandapay.b2c.dtos.InternalB2CTransactionRequest;
import com.tandapay.b2c.repository.B2CEntriesRepository;
import com.tandapay.b2c.services.B2CRequestService;
import documents.B2C_Entries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class B2CRequestServiceTest {

    private B2CEntriesRepository repository;
    private B2CRequestService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(B2CEntriesRepository.class);
        service = new B2CRequestService();
        service.repository = repository;
    }

    @Test
    void testSaveRequest() {
        InternalB2CTransactionRequest request = new InternalB2CTransactionRequest();
        request.setTransactionId("123");

        service.saveRequest(request);

        verify(repository, times(1)).save(any(B2C_Entries.class));
    }
}
