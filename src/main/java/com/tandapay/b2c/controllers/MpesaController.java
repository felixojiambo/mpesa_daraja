package com.tandapay.b2c.controllers;

import com.tandapay.b2c.dtos.AccessTokenResponse;
import com.tandapay.b2c.services.DarajaApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tandapay")
public class MpesaController {

    private final DarajaApi darajaApi;

    public MpesaController(DarajaApi darajaApi) {
        this.darajaApi = darajaApi;
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
}
