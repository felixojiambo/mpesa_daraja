package com.tandapay.b2c.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandapay.b2c.dtos.*;
import com.tandapay.b2c.services.DarajaApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tandapay")
@Slf4j
public class MpesaController {

    private final DarajaApi darajaApi;
    private final AcknowledgeResponse acknowledgeResponse;
    private final ObjectMapper objectMapper;

    public MpesaController(DarajaApi darajaApi, AcknowledgeResponse acknowledgeResponse, ObjectMapper objectMapper) {
        this.darajaApi = darajaApi;
        this.acknowledgeResponse = acknowledgeResponse;
        this.objectMapper = objectMapper;
    }

    @GetMapping(path = "/token", produces = "application/json")
    public ResponseEntity<AccessTokenResponse> getAccessToken() {
        AccessTokenResponse accessTokenResponse = darajaApi.getAccessToken();
        if (accessTokenResponse.getAccessToken() != null) {
            return ResponseEntity.ok(accessTokenResponse);
        } else {
            return ResponseEntity.status(500).body(accessTokenResponse);
        }
    }

    @GetMapping(path = "/register-url", produces = "application/json")
    public ResponseEntity<RegisterUrlResponse> registerUrl() {
        return ResponseEntity.ok(darajaApi.registerUrl());
    }

    @PostMapping(path = "/validation", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> mpesaValidation(@RequestBody MpesaValidationResponse mpesaValidationResponse) {
        return ResponseEntity.ok(acknowledgeResponse);
    }

    // === B2C Transaction Region ====

    @PostMapping(path = "/b2c-transaction-result", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> b2cTransactionAsyncResults(@RequestBody B2CTransactionAsyncResponse b2CTransactionAsyncResponse)
            throws JsonProcessingException {
        log.info("============ B2C Transaction Response =============");
        log.info(objectMapper.writeValueAsString(b2CTransactionAsyncResponse));
        return ResponseEntity.ok(acknowledgeResponse);
    }

    @PostMapping(path = "/b2c-queue-timeout", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> queueTimeout(@RequestBody Object object) {
        return ResponseEntity.ok(acknowledgeResponse);
    }

    @PostMapping(path = "/b2c-transaction", produces = "application/json")
    public ResponseEntity<B2CTransactionSyncResponse> performB2CTransaction(@RequestBody InternalB2CTransactionRequest internalB2CTransactionRequest) {
        return ResponseEntity.ok(darajaApi.performB2CTransaction(internalB2CTransactionRequest));
    }
    @PostMapping(path = "/simulate-transaction-result", produces = "application/json")
    public ResponseEntity<TransactionStatusSyncResponse> getTransactionStatusResult(@RequestBody InternalTransactionStatusRequest internalTransactionStatusRequest) {
        return ResponseEntity.ok(darajaApi.getTransactionResult(internalTransactionStatusRequest));
    }
}
