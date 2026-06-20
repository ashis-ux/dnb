package com.bsp.dnb.config;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

 
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class IamPublicKeyLoader {
	
	private static final Logger log =
            LoggerFactory.getLogger(
            		IamPublicKeyLoader.class);

    @Value("${iam.public-key-url}")
    private String publicKeyUrl;

 
    @Bean
    public RSAPublicKey iamPublicKey() {

        try {

            log.info("Fetching IAM public key from {}", publicKeyUrl);

            RestTemplate rt = new RestTemplate();

            String pem =
                    rt.getForObject(
                            publicKeyUrl,
                            String.class);

            String stripped = pem
                    .replace(
                            "-----BEGIN PUBLIC KEY-----",
                            "")
                    .replace(
                            "-----END PUBLIC KEY-----",
                            "")
                    .replaceAll("\\s+", "");

            byte[] decoded =
                    Base64.getDecoder()
                          .decode(stripped);

            X509EncodedKeySpec spec =
                    new X509EncodedKeySpec(
                            decoded);

            RSAPublicKey key =
                    (RSAPublicKey) KeyFactory
                            .getInstance("RSA")
                            .generatePublic(spec);

            log.info(
                    "IAM public key loaded successfully");

            return key;

        } catch (Exception ex) {

            log.error(
                    "Unable to load IAM public key",
                    ex);

            return null;
        }
    }


}
