package com.github.klefstad_teaching.cs122b.idm.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

public class RefreshResponse extends ResponseModel<RefreshResponse> {
    private String refreshToken, accessToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public RefreshResponse setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public RefreshResponse setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
