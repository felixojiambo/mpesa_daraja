package com.tandapay.b2c.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandapay.b2c.dtos.*;
import com.tandapay.b2c.repository.B2CEntriesRepository;
import com.tandapay.b2c.services.DarajaApi;
import documents.B2C_Entries;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("tandapay")
@Slf4j
public class MpesaController {

    private final DarajaApi darajaApi;
    private final AcknowledgeResponse acknowledgeResponse;
    private final ObjectMapper objectMapper;
    private final B2CEntriesRepository b2CEntriesRepository;

    public MpesaController(DarajaApi darajaApi, AcknowledgeResponse acknowledgeResponse, ObjectMapper objectMapper, B2CEntriesRepository b2CEntriesRepository) {
        this.darajaApi = darajaApi;
        this.acknowledgeResponse = acknowledgeResponse;
        this.objectMapper = objectMapper;
        this.b2CEntriesRepository = b2CEntriesRepository;
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
        B2C_Entries b2CEntry = b2CEntriesRepository.findByBillRefNumber(mpesaValidationResponse.getBillRefNumber());

        b2CEntry.setRawCallbackPayloadResponse(mpesaValidationResponse);
        b2CEntry.setResultCode("0");
        b2CEntry.setTransactionId(mpesaValidationResponse.getTransID());

        b2CEntriesRepository.save(b2CEntry);

        return ResponseEntity.ok(acknowledgeResponse);
    }

    // === B2C Transaction Region ====

    @PostMapping(path = "/b2c-transaction-result", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> b2cTransactionAsyncResults(@RequestBody B2CTransactionAsyncResponse b2CTransactionAsyncResponse)
            throws JsonProcessingException {
        log.info("============ B2C Transaction Response =============");
        log.info(objectMapper.writeValueAsString(b2CTransactionAsyncResponse));
        Result b2cResult = b2CTransactionAsyncResponse.getResult();

        B2C_Entries b2cInternalRecord = b2CEntriesRepository.findByConversationIdOrOriginatorConversationId(
                b2cResult.getConversationID(),
                b2cResult.getOriginatorConversationID());

        b2cInternalRecord.setRawCallbackPayloadResponse(b2CTransactionAsyncResponse);
        b2cInternalRecord.setResultCode(String.valueOf(b2cResult.getResultCode()));
        b2cInternalRecord.setTransactionId(b2cResult.getTransactionID());

        b2CEntriesRepository.save(b2cInternalRecord);
        return ResponseEntity.ok(acknowledgeResponse);
    }

    @PostMapping(path = "/b2c-queue-timeout", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> queueTimeout(@RequestBody Object object) {
        return ResponseEntity.ok(acknowledgeResponse);
    }

    @PostMapping(path = "/b2c-transaction", produces = "application/json")
    public ResponseEntity<CommonSyncResponse> performB2CTransaction(@RequestBody InternalB2CTransactionRequest internalB2CTransactionRequest) {
        log.info("Received B2C Transaction Request: {}", internalB2CTransactionRequest);
        CommonSyncResponse commonSyncResponse = darajaApi.performB2CTransaction(internalB2CTransactionRequest);

        if (commonSyncResponse != null) {
            log.info("B2C Transaction Response: {}", commonSyncResponse);
            B2C_Entries b2CEntry = new B2C_Entries();
            b2CEntry.setTransactionType("B2C");
            b2CEntry.setAmount(Long.valueOf(internalB2CTransactionRequest.getAmount()));
            b2CEntry.setEntryDate(new Date());
            b2CEntry.setOriginatorConversationId(commonSyncResponse.getOriginatorConversationID());
            b2CEntry.setConversationId(commonSyncResponse.getConversationID());
            b2CEntry.setMsisdn(internalB2CTransactionRequest.getPartyB());

            b2CEntriesRepository.save(b2CEntry);
            return ResponseEntity.ok(commonSyncResponse);
        } else {
            log.error("B2C Transaction Response is null");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping(path = "/simulate-transaction-result", produces = "application/json")
    public ResponseEntity<TransactionStatusSyncResponse> getTransactionStatusResult(@RequestBody InternalTransactionStatusRequest internalTransactionStatusRequest) {
        return ResponseEntity.ok(darajaApi.getTransactionResult(internalTransactionStatusRequest));
    }
}
