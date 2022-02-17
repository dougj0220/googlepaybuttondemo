package com.bhn.gpay.service;

public interface DecryptService {
    public String decryptGooglePayload(String payload) throws Exception;
    public String decryptSamsungPayload(String payload);
}
