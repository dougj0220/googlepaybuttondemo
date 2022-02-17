package com.bhn.gpay;

import com.bhn.gpay.domain.Authentication;
import com.bhn.gpay.domain.Authorization;
import com.bhn.gpay.domain.Billing;
import com.bhn.gpay.domain.Card;
import com.bhn.gpay.domain.VantivRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

@RunWith(JUnit4.class)
public class VantivRequestTest {

    @Test
    public void testAuthorize() throws JAXBException, IOException {
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
        card.setType("MC");
        card.setNumber("5454545454545454");
        card.setExpDate("1112");
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
        System.out.println(stringWriter.toString());

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
        String response = stringBuilder.toString();

        System.out.println(response);
    }
}
