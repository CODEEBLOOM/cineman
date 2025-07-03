package com.codebloom.cineman.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.Collections;

@Configuration
public class GoogleDriveConfig {
    @Value("${google.service.account.key}")
    private Resource accountKey;
    
    @Bean
    public Drive googleDrive() throws Exception {
        // khởi tạo credentials(authentication cua gg API)
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(accountKey.getInputStream())
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/drive")); // CRUD file trên gg Drive

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName("Cineman").build();
    }
}
