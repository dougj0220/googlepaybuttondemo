package com.bhn.gpay.domain;

public class ApiResponse {

    private String paymentResponse;
    private String gatewayResponse;

    public ApiResponse(String paymentResponse, String gatewayResponse) {
        this.paymentResponse = paymentResponse;
        this.gatewayResponse = gatewayResponse;
    }

    public String getPaymentResponse() {
        return paymentResponse;
    }

    public void setPaymentResponse(String paymentResponse) {
        this.paymentResponse = paymentResponse;
    }

    public String getGatewayResponse() {
        return gatewayResponse;
    }

    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }
}
