package com.tandapay.b2c.services;

import com.tandapay.b2c.dtos.AccessTokenResponse;
import com.tandapay.b2c.dtos.RegisterUrlResponse;

public interface DarajaApi {

    /**
     * @return Returns Daraja API Access Token Response
     */
    AccessTokenResponse getAccessToken();
    RegisterUrlResponse registerUrl();
}
