package com.tandapay.b2c.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandapay.b2c.config.MpesaConfiguration;
import com.tandapay.b2c.dtos.*;
import com.tandapay.b2c.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import okhttp3.*;
import static com.tandapay.b2c.utils.Constants.*;

@Service
@Slf4j
public class DarajaApiImpl implements DarajaApi {

    private final MpesaConfiguration mpesaConfiguration;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public DarajaApiImpl(MpesaConfiguration mpesaConfiguration, OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        this.mpesaConfiguration = mpesaConfiguration;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public AccessTokenResponse getAccessToken() {
        String credentials = String.format("%s:%s", mpesaConfiguration.getConsumerKey(), mpesaConfiguration.getConsumerSecret());
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        Request request = new Request.Builder()
                .url(String.format("%s?grant_type=%s", mpesaConfiguration.getOauthEndpoint(), mpesaConfiguration.getGrantType()))
                .get()
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BASIC_AUTH_STRING, encodedCredentials))
                .addHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_HEADER_VALUE)
                .build();

        int retryCount = 3;
        while (retryCount > 0) {
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    log.info("Received response: {}", responseBody);
                    return objectMapper.readValue(responseBody, AccessTokenResponse.class);
                } else {
                    log.error("Failed to get access token, response code: {}", response.code());
                    return new AccessTokenResponse(null, null);
                }
            } catch (IOException e) {
                log.error("Attempt {} - Could not get access token. -> {}", (3 - retryCount + 1), e.getLocalizedMessage(), e);
                retryCount--;
                if (retryCount == 0) {
                    return new AccessTokenResponse(null, null);
                }
            }
        }
        return new AccessTokenResponse(null, null);
    }

    @Override
    public RegisterUrlResponse registerUrl() {
        AccessTokenResponse accessTokenResponse = getAccessToken();

        RegisterUrlRequest registerUrlRequest = new RegisterUrlRequest();
        registerUrlRequest.setConfirmationURL(mpesaConfiguration.getConfirmationURL());
        registerUrlRequest.setResponseType(mpesaConfiguration.getResponseType());
        registerUrlRequest.setShortCode(mpesaConfiguration.getShortCode());
        registerUrlRequest.setValidationURL(mpesaConfiguration.getValidationURL());


        RequestBody body = RequestBody.create(Objects.requireNonNull(HelperUtility.toJson(registerUrlRequest)), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(mpesaConfiguration.getRegisterUrlEndpoint())
                .post((okhttp3.RequestBody) body)
                .addHeader("Authorization", String.format("Bearer %s", accessTokenResponse.getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();

            // use Jackson to Decode the ResponseBody ...
            return objectMapper.readValue(response.body().string(), RegisterUrlResponse.class);

        } catch (IOException e) {
            log.error(String.format("Could not register url -> %s", e.getLocalizedMessage()));
            return null;
        }
    }
    @Override
    public B2CTransactionSyncResponse performB2CTransaction(InternalB2CTransactionRequest internalB2CTransactionRequest) {
        AccessTokenResponse accessTokenResponse = getAccessToken();
        log.info(String.format("Access Token: %s", accessTokenResponse.getAccessToken()));

        B2CTransactionRequest b2CTransactionRequest = new B2CTransactionRequest();

        b2CTransactionRequest.setCommandID(internalB2CTransactionRequest.getCommandID());
        b2CTransactionRequest.setAmount(internalB2CTransactionRequest.getAmount());
        b2CTransactionRequest.setPartyB(internalB2CTransactionRequest.getPartyB());
        b2CTransactionRequest.setRemarks(internalB2CTransactionRequest.getRemarks());
        b2CTransactionRequest.setOccassion(internalB2CTransactionRequest.getOccassion());

        // get the security credentials ...
        b2CTransactionRequest.setSecurityCredential(HelperUtility.getSecurityCredentials(mpesaConfiguration.getB2cInitiatorPassword()));

        log.info(String.format("Security Creds: %s", b2CTransactionRequest.getSecurityCredential()));

        // set the result url ...
        b2CTransactionRequest.setResultURL(mpesaConfiguration.getB2cResultUrl());
        b2CTransactionRequest.setQueueTimeOutURL(mpesaConfiguration.getB2cQueueTimeoutUrl());
        b2CTransactionRequest.setInitiatorName(mpesaConfiguration.getB2cInitiatorName());
        b2CTransactionRequest.setPartyA(mpesaConfiguration.getShortCode());


        RequestBody body = RequestBody.create(
                Objects.requireNonNull(HelperUtility.toJson(b2CTransactionRequest)),JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getB2cTransactionEndpoint())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s", accessTokenResponse.getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();

            return objectMapper.readValue(response.body().string(), B2CTransactionSyncResponse.class);
        } catch (IOException e) {
            log.error(String.format("Could not perform B2C transaction ->%s", e.getLocalizedMessage()));
            return null;
        }

    }
    @Override
    public TransactionStatusSyncResponse getTransactionResult(InternalTransactionStatusRequest internalTransactionStatusRequest) {

        TransactionStatusRequest transactionStatusRequest = new TransactionStatusRequest();
        transactionStatusRequest.setTransactionID(internalTransactionStatusRequest.getTransactionID());

        transactionStatusRequest.setInitiator(mpesaConfiguration.getB2cInitiatorName());
        transactionStatusRequest.setSecurityCredential(HelperUtility.getSecurityCredentials(mpesaConfiguration.getB2cInitiatorPassword()));
        transactionStatusRequest.setCommandID(TRANSACTION_STATUS_QUERY_COMMAND);
        transactionStatusRequest.setPartyA(mpesaConfiguration.getShortCode());
        transactionStatusRequest.setIdentifierType(SHORT_CODE_IDENTIFIER);
        transactionStatusRequest.setResultURL(mpesaConfiguration.getB2cResultUrl());
        transactionStatusRequest.setQueueTimeOutURL(mpesaConfiguration.getB2cQueueTimeoutUrl());
        transactionStatusRequest.setRemarks(TRANSACTION_STATUS_VALUE);
        transactionStatusRequest.setOccasion(TRANSACTION_STATUS_VALUE);

        AccessTokenResponse accessTokenResponse = getAccessToken();

        RequestBody body = RequestBody.create(
                Objects.requireNonNull(HelperUtility.toJson(transactionStatusRequest)),JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getTransactionResultUrl())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s", accessTokenResponse.getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            // use Jackson to Decode the ResponseBody ...

            return objectMapper.readValue(response.body().string(), TransactionStatusSyncResponse.class);
        } catch (IOException e) {
            log.error(String.format("Could not fetch transaction result -> %s", e.getLocalizedMessage()));
            return null;
        }


    }
}
