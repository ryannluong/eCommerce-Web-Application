package com.github.klefstad_teaching.cs122b.gateway.model.data;

import java.time.Instant;

public class GatewayRequestObject {
    private String ipAddress, path;
    private Instant callTime;

    public String getIpAddress() {
        return ipAddress;
    }

    public GatewayRequestObject setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public String getPath() {
        return path;
    }

    public GatewayRequestObject setPath(String path) {
        this.path = path;
        return this;
    }

    public Instant getCallTime() {
        return callTime;
    }

    public GatewayRequestObject setCallTime(Instant callTime) {
        this.callTime = callTime;
        return this;
    }
}
