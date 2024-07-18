package com.tandapay.b2c.controllers;

import com.tandapay.b2c.dtos.InternalB2CTransactionRequest;
import com.tandapay.b2c.services.B2CRequestService;
import com.tandapay.b2c.services.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/b2c")
public class B2CController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private B2CRequestService b2CRequestService;

    @PostMapping("/request")
    public String receiveB2CRequest(@RequestBody InternalB2CTransactionRequest request) {
        b2CRequestService.saveRequest(request);
        kafkaProducerService.sendB2CRequest(request);
        return "B2C request received and processing";
    }
}
