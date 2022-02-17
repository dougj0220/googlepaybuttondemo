package com.bhn.gpay.service;

public interface GatewayService {
    public String processGooglePayload(String decryptedPayload) throws Exception;
}
