package com.tandapay.b2c.services;

import com.tandapay.b2c.dtos.AccessTokenResponse;

public interface DarajaApi {

    /**
     * @return Returns Daraja API Access Token Response
     */
    AccessTokenResponse getAccessToken();
}
