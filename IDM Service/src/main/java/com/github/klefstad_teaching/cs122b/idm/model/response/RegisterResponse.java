package com.github.klefstad_teaching.cs122b.idm.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

public class RegisterResponse extends ResponseModel<RegisterResponse> {
    private String email;
    private char[] password;

    public String getEmail() {
        return email;
    }

    public RegisterResponse setEmail(String email) {
        this.email = email;
        return this;
    }

    public char[] getPassword() {
        return password;
    }

    public RegisterResponse setPassword(char[] password) {
        this.password = password;
        return this;
    }
}
