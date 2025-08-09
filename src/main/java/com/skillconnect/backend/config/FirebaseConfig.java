package com.skillconnect.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {

                String firebaseConfigEnv = System.getenv("FIREBASE_CONFIG");

                InputStream serviceAccount;
                if (firebaseConfigEnv != null && !firebaseConfigEnv.isEmpty()) {
                    // Production: Load from environment variable
                    serviceAccount = new ByteArrayInputStream(firebaseConfigEnv.getBytes(StandardCharsets.UTF_8));
                } else {
                    // Local: Load from file in resources
                    serviceAccount = this.getClass().getClassLoader()
                            .getResourceAsStream("firebase-service-account.json");

                    if (serviceAccount == null) {
                        throw new IllegalStateException("firebase-service-account.json not found in classpath or FIREBASE_CONFIG not set.");
                    }
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}
