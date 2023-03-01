package com.github.klefstad_teaching.cs122b.idm.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

public class AuthenticateResponse extends ResponseModel<AuthenticateResponse> {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public AuthenticateResponse setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
