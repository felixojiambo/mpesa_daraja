package com.tandapay.b2c.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandapay.b2c.dtos.CommonSyncResponse;
import com.tandapay.b2c.dtos.InternalB2CTransactionRequest;
import com.tandapay.b2c.repository.B2CEntriesRepository;
import documents.B2C_Entries;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DarajaApi darajaApi;

    @Autowired
    private B2CEntriesRepository b2CEntriesRepository;

    @KafkaListener(topics = "b2c-requests", groupId = "b2c_group")
    public void consumeB2CRequest(String message) {
        try {
            InternalB2CTransactionRequest request = objectMapper.readValue(message, InternalB2CTransactionRequest.class);
            CommonSyncResponse response = darajaApi.performB2CTransaction(request);

            B2C_Entries entry = b2CEntriesRepository.findById(request.getTransactionId()).orElseThrow();
            entry.setStatus(response.getResponseDescription());
            b2CEntriesRepository.save(entry);

            log.info("B2C transaction processed and MongoDB updated for transactionId: {}", request.getTransactionId());
        } catch (Exception e) {
            log.error("Error processing B2C request", e);
        }
    }
}
