package com.tandapay.b2c.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandapay.b2c.config.MpesaConfiguration;
import com.tandapay.b2c.dtos.AccessTokenResponse;
import com.tandapay.b2c.dtos.RegisterUrlRequest;
import com.tandapay.b2c.dtos.RegisterUrlResponse;
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
}
