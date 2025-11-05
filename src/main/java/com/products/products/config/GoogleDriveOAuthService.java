package com.products.products.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GoogleDriveOAuthService {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.application.name}")
    private String applicationName;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private String accessToken;
    private String refreshToken;

    public void setTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Drive buildDriveService() throws Exception {
        // ✅ Check for nulls
        Objects.requireNonNull(clientId, "Google clientId is not set");
        Objects.requireNonNull(clientSecret, "Google clientSecret is not set");
        Objects.requireNonNull(applicationName, "Application name is not set");
        Objects.requireNonNull(accessToken, "Access token is not set");
        Objects.requireNonNull(refreshToken, "Refresh token is not set");

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JacksonFactory.getDefaultInstance())
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken);

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName(applicationName)
         .build();
    }
}
