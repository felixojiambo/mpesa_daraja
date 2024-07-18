package com.tandapay.b2c.controllers;

import com.tandapay.b2c.dtos.InternalTransactionStatusRequest;
import com.tandapay.b2c.dtos.TransactionStatusSyncResponse;
import com.tandapay.b2c.services.DarajaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/b2c")
public class TransactionStatusController {

    @Autowired
    private DarajaApi darajaApi;

    @GetMapping("/status/{transactionId}")
    public TransactionStatusSyncResponse fetchPaymentStatus(@PathVariable String transactionId) {
        InternalTransactionStatusRequest request = new InternalTransactionStatusRequest();
        request.setTransactionID(transactionId);
        return darajaApi.getTransactionResult(request);
    }
}
