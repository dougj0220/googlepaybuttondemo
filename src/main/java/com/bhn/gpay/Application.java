package com.bhn.gpay;

import com.google.crypto.tink.apps.paymentmethodtoken.GooglePaymentsPublicKeysManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		GooglePaymentsPublicKeysManager.INSTANCE_TEST.refreshInBackground();
		SpringApplication.run(Application.class, args);
	}

}
