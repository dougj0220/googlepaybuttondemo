package com.bhn.gpay.service.impl;

import Api.PaymentsApi;
import Invokers.ApiClient;
import Invokers.ApiException;
import Model.CreatePaymentRequest;
import Model.PtsV2PaymentsPost201Response;
import Model.Ptsv2paymentsClientReferenceInformation;
import Model.Ptsv2paymentsOrderInformation;
import Model.Ptsv2paymentsOrderInformationAmountDetails;
import Model.Ptsv2paymentsOrderInformationBillTo;
import Model.Ptsv2paymentsPaymentInformation;
import Model.Ptsv2paymentsPaymentInformationCard;
import Model.Ptsv2paymentsPaymentInformationTokenizedCard;
import Model.Ptsv2paymentsProcessingInformation;
import com.bhn.gpay.Configuration;
import com.bhn.gpay.service.GatewayService;
import com.cybersource.authsdk.core.ConfigException;
import com.cybersource.authsdk.core.MerchantConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class CybersourceServiceImpl implements GatewayService {

    @Override
    public String processGooglePayload(String decryptedPayload) throws Exception {
        JsonObject payloadJsonObject = new JsonParser().parse(decryptedPayload).getAsJsonObject();
        JsonObject payloadMethodDetailsJsonObject = payloadJsonObject.get("paymentMethodDetails")
                .getAsJsonObject();
        String cardNumber = payloadMethodDetailsJsonObject.get("pan").getAsString();
        String expirationMonth = payloadMethodDetailsJsonObject.get("expirationMonth").getAsString();
        String expirationYear = payloadMethodDetailsJsonObject.get("expirationYear").getAsString();

        boolean userCapture = true;

        CreatePaymentRequest requestObj = new CreatePaymentRequest();

        Ptsv2paymentsClientReferenceInformation clientReferenceInformation = new Ptsv2paymentsClientReferenceInformation();
        clientReferenceInformation.code("TC_1231223");
        requestObj.clientReferenceInformation(clientReferenceInformation);

        Ptsv2paymentsProcessingInformation processingInformation = new Ptsv2paymentsProcessingInformation();
        processingInformation.capture(false);
        if (userCapture) {
            processingInformation.capture(true);
        }

        processingInformation.paymentSolution("012");
        requestObj.processingInformation(processingInformation);

        Ptsv2paymentsPaymentInformation paymentInformation = new Ptsv2paymentsPaymentInformation();
        Ptsv2paymentsPaymentInformationTokenizedCard paymentInformationTokenizedCard = new Ptsv2paymentsPaymentInformationTokenizedCard();
        paymentInformationTokenizedCard.number(cardNumber);
        paymentInformationTokenizedCard.expirationMonth(expirationMonth);
        paymentInformationTokenizedCard.expirationYear(expirationYear);
        //paymentInformationTokenizedCard.cryptogram("EHuWW9PiBkWvqE5juRwDzAUFBAk=");
        paymentInformationTokenizedCard.transactionType("1");
        paymentInformation.tokenizedCard(paymentInformationTokenizedCard);

        requestObj.paymentInformation(paymentInformation);

        Ptsv2paymentsOrderInformation orderInformation = new Ptsv2paymentsOrderInformation();
        Ptsv2paymentsOrderInformationAmountDetails orderInformationAmountDetails = new Ptsv2paymentsOrderInformationAmountDetails();
        orderInformationAmountDetails.totalAmount("20");
        orderInformationAmountDetails.currency("USD");
        orderInformation.amountDetails(orderInformationAmountDetails);

        Ptsv2paymentsOrderInformationBillTo orderInformationBillTo = new Ptsv2paymentsOrderInformationBillTo();
        orderInformationBillTo.firstName("John");
        orderInformationBillTo.lastName("Deo");
        orderInformationBillTo.address1("901 Metro Center Blvd");
        orderInformationBillTo.locality("Foster City");
        orderInformationBillTo.administrativeArea("CA");
        orderInformationBillTo.postalCode("94404");
        orderInformationBillTo.country("US");
        orderInformationBillTo.email("test@cybs.com");
        orderInformationBillTo.phoneNumber("6504327113");
        orderInformation.billTo(orderInformationBillTo);

        requestObj.orderInformation(orderInformation);

        PtsV2PaymentsPost201Response result = null;
        try {
            Properties merchantProp = Configuration.getMerchantDetails();
            ApiClient apiClient = new ApiClient();
            MerchantConfig merchantConfig = new MerchantConfig(merchantProp);

            PaymentsApi apiInstance = new PaymentsApi(apiClient);
            result = apiInstance.createPayment(requestObj, merchantConfig);

            String responseCode = apiClient.responseCode;
            String status = apiClient.status;
            System.out.println("ResponseCode :" + responseCode);
            System.out.println("ResponseMessage :" + status);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public String processBasicCard(String payload) {
        JsonObject payloadJsonObject = new JsonParser().parse(payload).getAsJsonObject();

        JsonObject detailsObject = payloadJsonObject.get("details").getAsJsonObject();
        String cardNumber = detailsObject.get("cardNumber").getAsString();
        String cardSecurityCode = detailsObject.get("cardSecurityCode").getAsString();
        String expirationMonth = detailsObject.get("expiryMonth").getAsString();
        String expirationYear = detailsObject.get("expiryYear").getAsString();

        JsonObject billingObject = detailsObject.get("billingAddress").getAsJsonObject();
        String billingCountry = billingObject.get("country").getAsString();
        String[] billingName = billingObject.get("recipient").getAsString().split(" ");
        String billingFirstName = billingName[0];
        String billingLastName = billingName[1];
        String billingAddress = billingObject.get("addressLine").getAsJsonArray().get(0).getAsString();
        String billingZip = billingObject.get("postalCode").getAsString();
        String billingCity = billingObject.get("city").getAsString();
        String billingState = billingObject.get("region").getAsString();

        String responseCode = null;
        String status = null;
        PtsV2PaymentsPost201Response response = null;
        boolean capture = false;
        Properties merchantProp;
        CreatePaymentRequest request;

        request = new CreatePaymentRequest();

        Ptsv2paymentsClientReferenceInformation client = new Ptsv2paymentsClientReferenceInformation();
        client.code("test_payment");
        request.clientReferenceInformation(client);

        /*
        Ptsv2paymentsPointOfSaleInformation saleInformation = new Ptsv2paymentsPointOfSaleInformation();
        saleInformation.cardPresent(false);
        saleInformation.catLevel(6);
        saleInformation.terminalCapability(4);
        request.pointOfSaleInformation(saleInformation);
         */

        Ptsv2paymentsOrderInformationBillTo billTo = new Ptsv2paymentsOrderInformationBillTo();
        billTo.country(billingCountry);
        billTo.firstName(billingFirstName);
        billTo.lastName(billingLastName);
        billTo.address1(billingAddress);
        billTo.postalCode(billingZip);
        billTo.locality(billingCity);
        billTo.administrativeArea(billingState);
        billTo.email("test@cybs.com");

        Ptsv2paymentsOrderInformationAmountDetails amountDetails = new Ptsv2paymentsOrderInformationAmountDetails();
        amountDetails.totalAmount("100.00");
        amountDetails.currency("USD");

        Ptsv2paymentsOrderInformation orderInformation = new Ptsv2paymentsOrderInformation();
        orderInformation.billTo(billTo);
        orderInformation.amountDetails(amountDetails);
        request.setOrderInformation(orderInformation);

        Ptsv2paymentsProcessingInformation processingInformation = new Ptsv2paymentsProcessingInformation();
        if (capture == true) {
            processingInformation.capture(true);
        }
        request.processingInformation(processingInformation);

        Ptsv2paymentsPaymentInformationCard card = new Ptsv2paymentsPaymentInformationCard();
        card.expirationYear(expirationYear);
        card.number(cardNumber);
        card.securityCode(cardSecurityCode);
        card.expirationMonth(expirationMonth);

        Ptsv2paymentsPaymentInformation paymentInformation = new Ptsv2paymentsPaymentInformation();
        paymentInformation.card(card);
        request.setPaymentInformation(paymentInformation);

        try {
            /* Read Merchant details. */
            merchantProp = Configuration.getMerchantDetails();
            MerchantConfig merchantConfig = new MerchantConfig(merchantProp);

            PaymentsApi paymentApi = new PaymentsApi();
            response = paymentApi.createPayment(request, merchantConfig);

            responseCode = ApiClient.responseCode;
            status = ApiClient.status;

            System.out.println("ResponseCode :" + responseCode);
            System.out.println("Status :" + status);
            System.out.println(response.getId());

        } catch (ApiException | ConfigException e) {
            e.printStackTrace();
        }
        return response.toString();

    }
}
