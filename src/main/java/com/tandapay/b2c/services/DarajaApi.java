package com.tandapay.b2c.services;

import com.tandapay.b2c.dtos.*;

public interface DarajaApi {

    /**
     * @return Returns Daraja API Access Token Response
     */
    AccessTokenResponse getAccessToken();
    RegisterUrlResponse registerUrl();
    B2CTransactionSyncResponse performB2CTransaction(InternalB2CTransactionRequest internalB2CTransactionRequest);

    TransactionStatusSyncResponse getTransactionResult(InternalTransactionStatusRequest internalTransactionStatusRequest);
}
