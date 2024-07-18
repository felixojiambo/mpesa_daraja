package com.tandapay.b2c.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandapay.b2c.dtos.InternalB2CTransactionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendB2CRequest(InternalB2CTransactionRequest request) {
        try {
            String message = objectMapper.writeValueAsString(request);
            kafkaTemplate.send("b2c-requests", message);
            log.info("B2C Request sent to Kafka topic: {}", message);
        } catch (Exception e) {
            log.error("Error sending B2C request to Kafka", e);
        }
    }
}
