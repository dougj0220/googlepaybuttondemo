package com.bhn.gpay.service.impl;

import com.bhn.gpay.domain.Authentication;
import com.bhn.gpay.domain.Authorization;
import com.bhn.gpay.domain.Billing;
import com.bhn.gpay.domain.Card;
import com.bhn.gpay.domain.VantivRequest;
import com.bhn.gpay.service.GatewayService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

@Service
public class VantivServiceImpl implements GatewayService {

    @Override
    public String processGooglePayload(String decryptedPayload) throws Exception {
        JsonObject payloadJsonObject = new JsonParser().parse(decryptedPayload).getAsJsonObject();
        JsonObject payloadMethodDetailsJsonObject = payloadJsonObject.get("paymentMethodDetails")
                .getAsJsonObject();
        //String cardNumber = payloadMethodDetailsJsonObject.get("pan").getAsString();
        // Change to fixed card number since Vantiv sandbox returns different statuses depending on last 3 digits of card number
        // 000 will be approved
        String cardNumber = "4470330769941000";
        String expirationMonth = payloadMethodDetailsJsonObject.get("expirationMonth").getAsString();
        String expirationYear = payloadMethodDetailsJsonObject.get("expirationYear").getAsString();

        JAXBContext jc = JAXBContext.newInstance(VantivRequest.class);

        Authentication authentication = new Authentication();
        authentication.setUser("JoesStore");
        authentication.setPassword("JoeyIsTheBe$t");

        Billing billing = new Billing();
        billing.setName("Jane Doe");
        billing.setAddressLine1("20 Main Street");
        billing.setCity("San Jose");
        billing.setState("CA");
        billing.setZip("95032");
        billing.setCountry("USA");
        billing.setEmail("jdoe@vantiv.com");
        billing.setPhone("978-551-0040");

        Card card = new Card();
        card.setType("VI");
        card.setNumber(cardNumber);
        card.setExpDate(expirationMonth + expirationYear.substring(2));
        card.setCardValidationNum("");

        Authorization authorization = new Authorization();
        authorization.setId("ididid");
        authorization.setReportGroup("rtpGrp");
        authorization.setCustomerId("12345");
        authorization.setOrderId(1);
        authorization.setAmount(1000);
        authorization.setOrderSource("ecommerce");
        authorization.setBillToAddress(billing);
        authorization.setCard(card);

        VantivRequest vantivRequest = new VantivRequest();
        vantivRequest.setVersion("12.1");
        vantivRequest.setXmlns("http://www.vantivcnp.com/schema");
        vantivRequest.setMerchantId("default");
        vantivRequest.setAuthentication(authentication);
        vantivRequest.setAuthorization(authorization);

        StringWriter stringWriter = new StringWriter();

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(vantivRequest, stringWriter);

        URL url = new URL("https://www.testvantivcnp.com/sandbox/communicator/online");
        URLConnection con = url.openConnection();
        HttpsURLConnection http = (HttpsURLConnection)con;
        http.setRequestMethod("POST");
        http.setRequestProperty("Content-Type", "text/xml");
        http.setDoOutput(true);
        http.getOutputStream().write(stringWriter.toString().getBytes(StandardCharsets.UTF_8));

        Reader in = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        for (int c; (c = in.read()) >= 0;)
            stringBuilder.append((char)c);

        return stringBuilder.toString();
    }
}
