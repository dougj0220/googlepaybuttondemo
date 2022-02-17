package com.bhn.gpay.service.impl;

import com.bhn.gpay.service.DecryptService;
import com.google.crypto.tink.apps.paymentmethodtoken.GooglePaymentsPublicKeysManager;
import com.google.crypto.tink.apps.paymentmethodtoken.PaymentMethodTokenRecipient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.vertx.core.json.Json;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class DecryptServiceImpl implements DecryptService {

    @Override
    public String decryptGooglePayload(String payload) throws Exception {
        String MY_PRIVATE_KEY_PKCS8_BASE64 =
                "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgRapj7OP09R/pSiNXy4XI65339xkV1yFy+QlDdW/jksehRANCAAR2ccroc4SMi5yVfxx7ZYVMX8ZLps4R1sKddC1vn+ZumItaxqLP8qTTyezMOn0juYDb/wV20C8deD6urJpEcB5o";

        try {
            GooglePaymentsPublicKeysManager keysManager = GooglePaymentsPublicKeysManager.INSTANCE_TEST;

            PaymentMethodTokenRecipient paymentMethodTokenRecipient =
                    new PaymentMethodTokenRecipient.Builder()
                            .protocolVersion("ECv2")
                            .fetchSenderVerifyingKeysWith(keysManager)
                            .recipientId("merchant:" + "12345678901234567890")
                            .addRecipientPrivateKey(MY_PRIVATE_KEY_PKCS8_BASE64)
                            .build();

            return paymentMethodTokenRecipient.unseal(payload);
        } catch (GeneralSecurityException e) {
            String message = "Unable to decrypt payload, error message: " + e.getMessage();
            throw new Exception(message);
        }
    }

    @Override
    public String decryptSamsungPayload(String payload) {
        JsonObject payloadObj = new Gson().fromJson(payload, JsonObject.class);
        JsonObject detailsObj = payloadObj.get("details").getAsJsonObject();
        JsonObject paymentCredentialObj = detailsObj.get("paymentCredential").getAsJsonObject();
        JsonObject sPayAuthDataObj = paymentCredentialObj.get("3DS").getAsJsonObject();
        String payloadData = sPayAuthDataObj.get("data").getAsString();

        try {
            // split the compacted serialized data by . (dot)
            String[] splitToken = payloadData.split("\\.");

            final int JWE_ENCRYPTED_KEY_INDEX = 1;
            final int JWE_IV_INDEX = 2;
            final int JWE_CYPHERTEXT_INDEX = 3;
            final int JWE_AUTH_TAG_INDEX = 4;

            byte[] initVector = Base64.getUrlDecoder()
                    .decode(splitToken[JWE_IV_INDEX].getBytes());
            byte[] cipherText = Base64.getUrlDecoder()
                    .decode(splitToken[JWE_CYPHERTEXT_INDEX].getBytes());
            byte[] encryptedKey = Base64.getUrlDecoder()
                    .decode(splitToken[JWE_ENCRYPTED_KEY_INDEX].getBytes());
            byte[] tag = Base64.getUrlDecoder()
                    .decode(splitToken[JWE_AUTH_TAG_INDEX].getBytes());

            Cipher decrypt = Cipher.getInstance("RSA");
            String filePath = this.getClass().getResource("/new_key.der").getFile();
            byte[] derKey = Files.readAllBytes(Paths.get(filePath));
            decrypt.init(Cipher.DECRYPT_MODE, getPrivateKey(derKey));
            byte[] keyData = decrypt.doFinal(encryptedKey);

            byte[] outputData = new byte[cipherText.length];

            final Cipher javaAES128 = Cipher.getInstance("AES_128/GCM/NoPadding");
            final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * Byte.SIZE,
                    initVector);
            final SecretKeySpec keySpec = new SecretKeySpec(keyData, "AES");

            javaAES128.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
            int offset = javaAES128.update(cipherText, 0, cipherText.length, outputData, 0);
            javaAES128.update(tag, 0, tag.length, outputData, offset);
            javaAES128.doFinal(outputData, offset);
            return (new String(outputData));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | ShortBufferException | BadPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private PrivateKey getPrivateKey(byte[] derKey)
            throws IOException, IllegalArgumentException {
        try {
            PrivateKey privateKey = null;

            if (derKey != null) {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                KeySpec privateKeySpec = new PKCS8EncodedKeySpec(derKey);
                privateKey = keyFactory.generatePrivate(privateKeySpec);
            }

            return privateKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalArgumentException("The provided private key is not supported", e);
        }
    }
}
