package com.bhn.gpay.service;

import com.google.crypto.tink.apps.paymentmethodtoken.PaymentMethodTokenRecipient;
import com.google.crypto.tink.apps.paymentmethodtoken.PaymentMethodTokenSender;
import com.google.crypto.tink.apps.paymentmethodtoken.SenderIntermediateCertFactory;
import com.google.crypto.tink.subtle.Base64;
import com.google.crypto.tink.subtle.EllipticCurves;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.security.interfaces.ECPrivateKey;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class GooglePayTest {
    /**
     * Created with:
     *
     * <pre>
     * step 1.) Generate a EC private key: openssl ecparam -name prime256v1 -genkey -noout -out gpay_key.pem
     * step 2.) (optional to view the private/public keys: openssl ec -in gpay_key.pem -pubout -text -noout
     * step 3.) Generate a base64-encoded public key: openssl ec -in gpay_key.pem -pubout -text -noout 2> /dev/null | grep "pub:" -A5 | sed 1d | xxd -r -p | base64 | paste -sd "\0" - | tr -d '\n\r ' > gpay_public_key.txt
     * step 4.) Generate a base64-encoded private key in PKCS #8 format: openssl pkcs8 -topk8 -inform PEM -outform DER -in gpay_key.pem -nocrypt | base64 | paste -sd "\0" -
     * </pre>
     */
    private static final String MY_PUBLIC_KEY_BASE64 =
            "BHZxyuhzhIyLnJV/HHtlhUxfxkumzhHWwp10LW+f5m6Yi1rGos/ypNPJ7Mw6fSO5gNv/BXbQLx14Pq6smkRwHmg=";

    private static final String MY_PRIVATE_KEY_PKCS8_BASE64 =
    "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgRapj7OP09R/pSiNXy4XI65339xkV1yFy+QlDdW/jksehRANCAAR2ccroc4SMi5yVfxx7ZYVMX8ZLps4R1sKddC1vn+ZumItaxqLP8qTTyezMOn0juYDb/wV20C8deD6urJpEcB5o";

    /** Sample Google provided JSON with its public signing keys. */
    // https://payments.developers.google.com/paymentmethodtoken/test/keys.json
    private static final String GOOGLE_VERIFYING_PUBLIC_KEYS_JSON =
            "{\n"
                    + "  \"keys\": [\n"
                    + "    {\n"
                    + "      \"keyValue\": \"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEPYnHwS8uegWAewQtlxizmLFynw"
                    + "HcxRT1PK07cDA6/C4sXrVI1SzZCUx8U8S0LjMrT6ird/VW7be3Mz6t/srtRQ==\",\n"
                    + "      \"protocolVersion\": \"ECv1\"\n"
                    + "    },\n"
                    + "    {\n"
                    + "      \"keyValue\": \"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE/1+3HBVSbdv+j7NaArdgMyoSAM"
                    + "43yRydzqdg1TxodSzA96Dj4Mc1EiKroxxunavVIvdxGnJeFViTzFvzFRxyCw==\",\n"
                    + "      \"keyExpiration\": \""
                    + Instant.now().plus(Duration.standardDays(1)).getMillis()
                    + "\",\n"
                    + "      \"protocolVersion\": \"ECv2\"\n"
                    + "    },\n"
                    + "    {\n"
                    + "      \"keyValue\": \"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAENXvYqxD5WayKYhuXQevdGdLA8i"
                    + "fV4LsRS2uKvFo8wwyiwgQHB9DiKzG6T/P1Fu9Bl7zWy/se5Dy4wk1mJoPuxg==\",\n"
                    + "      \"keyExpiration\": \""
                    + Instant.now().plus(Duration.standardDays(1)).getMillis()
                    + "\",\n"
                    + "      \"protocolVersion\": \"ECv2SigningOnly\"\n"
                    + "    }\n"
                    + "  ]\n"
                    + "}";

    /**
     * Sample Google private signing key for the ECv2 protocolVersion.
     *
     * <p>Corresponds to ECv2 private key of the key in {@link #GOOGLE_VERIFYING_PUBLIC_KEYS_JSON}.
     */
    private static final String GOOGLE_SIGNING_EC_V2_PRIVATE_KEY_PKCS8_BASE64 =
            "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgKvEdSS8f0mjTCNKev"
                    + "aKXIzfNC5b4A104gJWI9TsLIMqhRANCAAT/X7ccFVJt2/6Ps1oCt2AzKhIAz"
                    + "jfJHJ3Op2DVPGh1LMD3oOPgxzUSIqujHG6dq9Ui93Eacl4VWJPMW/MVHHIL";

    /**
     * Sample Google intermediate public signing key for the ECv2 protocolVersion.
     *
     * <p>Base64 version of the public key encoded in ASN.1 type SubjectPublicKeyInfo defined in the
     * X.509 standard.
     *
     * <p>The intermediate public key will be signed by {@link
     * #GOOGLE_SIGNING_EC_V2_PRIVATE_KEY_PKCS8_BASE64}.
     */
    private static final String GOOGLE_SIGNING_EC_V2_INTERMEDIATE_PUBLIC_KEY_X509_BASE64 =
            "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE/1+3HBVSbdv+j7NaArdgMyoSAM43yR"
                    + "ydzqdg1TxodSzA96Dj4Mc1EiKroxxunavVIvdxGnJeFViTzFvzFRxyCw==";

    /**
     * Sample Google intermediate private signing key for the ECv2 protocolVersion.
     *
     * <p>Corresponds to private key of the key in {@link
     * #GOOGLE_SIGNING_EC_V2_INTERMEDIATE_PUBLIC_KEY_X509_BASE64}.
     */
    private static final String GOOGLE_SIGNING_EC_V2_INTERMEDIATE_PRIVATE_KEY_PKCS8_BASE64 =
            "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgKvEdSS8f0mjTCNKev"
                    + "aKXIzfNC5b4A104gJWI9TsLIMqhRANCAAT/X7ccFVJt2/6Ps1oCt2AzKhIAz"
                    + "jfJHJ3Op2DVPGh1LMD3oOPgxzUSIqujHG6dq9Ui93Eacl4VWJPMW/MVHHIL";

    private static final String RECIPIENT_ID = "1234567890123456789";
    private static final String PROTOCOL_VERSION_EC_V2 = "ECv2";

    private final String creditCardPayload = "{\n" +
            "    \"gatewayMerchantId\": \"1234567890123456789\",\n" +
            "    \"messageExpiration\": \"" + Instant.now().plus(Duration.standardDays(1)).getMillis() +"\",\n" +
            //"    \"messageId\": \"AH2EjteBXyoZR2TrJK_C2SKvwoG0dsw5WsQDkGOODXA_jwN0TF9j_I_NzWOLNwImcjBEfndMBiDgDJx9phz6H3D4jJQKy22-zcm38gaVcSPK3T_y7Qj1JEAZ-V0KgjpOXWBQTxC2M6vV\",\n" +
            "    \"messageId\": \"some unique base 64 url encoded message id\",\n" +
            "    \"paymentMethod\": \"CARD\",\n" +
            "    \"paymentMethodDetails\": {\n" +
            "        \"expirationYear\": 2026,\n" +
            "        \"expirationMonth\": 12,\n" +
            "        \"pan\": \"4111111111111111\",\n" +
            "        \"authMethod\": \"PAN_ONLY\"\n" +
            "    }\n}";
    @Test
    public void testECV2Decryption() throws Exception {
        ECPrivateKey pk = EllipticCurves.getEcPrivateKey(Base64.decode(MY_PRIVATE_KEY_PKCS8_BASE64));
        PaymentMethodTokenRecipient recipient =
                new PaymentMethodTokenRecipient.Builder()
                        .protocolVersion(PROTOCOL_VERSION_EC_V2)
                        .senderVerifyingKeys(GOOGLE_VERIFYING_PUBLIC_KEYS_JSON)
                        .recipientId(RECIPIENT_ID)
                        .addRecipientPrivateKey(MY_PRIVATE_KEY_PKCS8_BASE64)
                        //.addRecipientPrivateKey(pk)
                        .build();

        //String plaintext = "blah";
        //assertEquals(plaintext, recipient.unseal(sender.seal(plaintext)));

        String encryptedPayload = messageEncryptor(creditCardPayload);
        assertEquals(creditCardPayload, recipient.unseal(encryptedPayload));
    }

    private String messageEncryptor(String payload) throws Exception {
        //ECPublicKey pub = EllipticCurves.getEcPublicKey(Base64.decode(MY_PUBLIC_KEY_BASE64));
        PaymentMethodTokenSender sender =
                new PaymentMethodTokenSender.Builder()
                        .protocolVersion(PROTOCOL_VERSION_EC_V2)
                        .senderIntermediateSigningKey(
                                GOOGLE_SIGNING_EC_V2_INTERMEDIATE_PRIVATE_KEY_PKCS8_BASE64)
                        .senderIntermediateCert(
                                new SenderIntermediateCertFactory.Builder()
                                        .protocolVersion(PROTOCOL_VERSION_EC_V2)
                                        .addSenderSigningKey(GOOGLE_SIGNING_EC_V2_PRIVATE_KEY_PKCS8_BASE64)
                                        .senderIntermediateSigningKey(
                                                GOOGLE_SIGNING_EC_V2_INTERMEDIATE_PUBLIC_KEY_X509_BASE64)
                                        .expiration(Instant.now().plus(Duration.standardDays(1)).getMillis())
                                        .build()
                                        .create())
                        .recipientId(RECIPIENT_ID)
                        .rawUncompressedRecipientPublicKey(MY_PUBLIC_KEY_BASE64)
                        //.recipientPublicKey(pub)
                        .build();
        return sender.seal(payload);
    }


  /* String paymentMethodToken = "{\n" +
            "  \"protocolVersion\":\"ECv2\",\n" +
            "  \"signature\":\"MEQCIH6Q4OwQ0jAceFEkGF0JID6sJNXxOEi4r+mA7biRxqBQAiAondqoUpU/bdsrAOpZIsrHQS9nwiiNwOrr24RyPeHA0Q\\u003d\\u003d\",\n" +
            "  \"intermediateSigningKey\":{\n" +
            "    \"signedKey\": \"{\\\"keyExpiration\\\":\\\"1542323393147\\\",\\\"keyValue\\\":\\\"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE/1+3HBVSbdv+j7NaArdgMyoSAM43yRydzqdg1TxodSzA96Dj4Mc1EiKroxxunavVIvdxGnJeFViTzFvzFRxyCw\\\\u003d\\\\u003d\\\"}\",\n" +
            "    \"signatures\": [\"MEYCIQCO2EIi48s8VTH+ilMEpoXLFfkxAwHjfPSCVED/QDSHmQIhALLJmrUlNAY8hDQRV/y1iKZGsWpeNmIP+z+tCQHQxP0v\"]\n" +
            "  },\n" +
            "  \"signedMessage\":\"{\\\"tag\\\":\\\"jpGz1F1Bcoi/fCNxI9n7Qrsw7i7KHrGtTf3NrRclt+U\\\\u003d\\\",\\\"ephemeralPublicKey\\\":\\\"BJatyFvFPPD21l8/uLP46Ta1hsKHndf8Z+tAgk+DEPQgYTkhHy19cF3h/bXs0tWTmZtnNm+vlVrKbRU9K8+7cZs\\\\u003d\\\",\\\"encryptedMessage\\\":\\\"mKOoXwi8OavZ\\\"}\"\n" +
            "}";*/

    /*private byte[] getPrivateKey() throws IOException {
        return FileUtils.readFileToByteArray(new File("/Users/doug.jeremias/dev/cashstar/samsung_pay_java/src/main/resources/gpay_64key.der"));
    }*/

   /* @Test
    public void testDecryption() throws Exception {
        byte[] key = getPrivateKey();
        ECPrivateKey pk = EllipticCurves.getEcPrivateKey(Base64.decode(new String(key)));
        String publicKey = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEGnJ7Yo1sX9b4kr4Aa5uq58JRQfzD8bIJXw7WXaap\\/hVE+PnFxvjx4nVxt79SdRuUVeu++HZD0cGAv4IOznc96w==";
        ECPublicKey pub = EllipticCurves.getEcPublicKey(Base64.decode(publicKey));
        JSONObject json = new JSONObject(paymentMethodToken);
        String decryptedMessage =
                new PaymentMethodTokenRecipient.Builder()
                        //.fetchSenderVerifyingKeysWith(GooglePaymentsPublicKeysManager.INSTANCE_TEST)
                        .addSenderVerifyingKey(pub)
                        .recipientId("merchant:1234567890123456789")
                        // This guide applies only to protocolVersion = ECv2
                        .protocolVersion("ECv2")
                        // Multiple private keys can be added to support graceful
                        // key rotations.
                        //"MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgRapj7OP09R/pSiNXy4XI65339xkV1yFy+QlDdW/jksehRANCAAR2ccroc4SMi5yVfxx7ZYVMX8ZLps4R1sKddC1vn+ZumItaxqLP8qTTyezMOn0juYDb/wV20C8deD6urJpEcB5o"
                        .addRecipientPrivateKey(pk)
                        .build()
                        .unseal(json.toString());

        Assert.assertNotNull(decryptedMessage);
        //TinkConfig.register();
        //AeadConfig.register();
        GooglePaymentsPublicKeysManager test = GooglePaymentsPublicKeysManager.INSTANCE_TEST;
        //String keysetFilename = "/Users/doug.jeremias/dev/cashstar/samsung_pay_java/src/main/resources/google_test_keys.json";
        //KeysetHandle keysetHandle = CleartextKeysetHandle.read( JsonKeysetReader.withFile(new File(keysetFilename)));
        //Assert.assertNotNull(keysetHandle);

    }*/

   /* public PrivateKey keyToValue(byte[] pkcs8key) throws GeneralSecurityException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8key);
        KeyFactory factory = KeyFactory.getInstance("EC");

        return factory.generatePrivate(spec);
    }*/

}
