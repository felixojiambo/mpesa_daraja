package com.tandapay.b2c.services;

import com.tandapay.b2c.dtos.InternalB2CTransactionRequest;
import com.tandapay.b2c.repository.B2CEntriesRepository;
import documents.B2C_Entries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class B2CRequestService {

    @Autowired
    public B2CEntriesRepository repository;



    public void saveRequest(InternalB2CTransactionRequest request) {
        B2C_Entries entry = new B2C_Entries();
        entry.setTransactionId(request.getTransactionId());
        entry.setStatus("PENDING");
        entry.setDetails(request.toString());
        repository.save(entry);
    }
}
