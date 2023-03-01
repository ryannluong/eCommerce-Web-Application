package com.github.klefstad_teaching.cs122b.billing.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

public class OrderPaymentResponse extends ResponseModel<OrderPaymentResponse> {
    String paymentIntentId, clientSecret;

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public OrderPaymentResponse setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public OrderPaymentResponse setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }
}
