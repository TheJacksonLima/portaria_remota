package com.jfl.FaceRecognition;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FaceControllerIT {

    private static final int GCM_TAG_LENGTH = 128;
    private static final int NONCE_LENGTH = 12;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldEvaluateEncryptedFaceImage() throws Exception {

        // ===== LOAD AES KEY =====
        String base64Key = System.getenv("FACE_AES_KEY");
        if (base64Key == null) {
            throw new IllegalStateException("FACE_AES_KEY not defined");
        }

        byte[] aesKey = Base64.getDecoder().decode(base64Key);

        // ===== LOAD IMAGE =====
        byte[] imageBytes = Files.readAllBytes(
                Path.of("src/test/resources/test-face.jpg")
        );

        // ===== ENCRYPT (same as client) =====
        byte[] nonce = new byte[NONCE_LENGTH];
        new SecureRandom().nextBytes(nonce);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, nonce);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
        byte[] encrypted = cipher.doFinal(imageBytes);

        byte[] payload = new byte[nonce.length + encrypted.length];
        System.arraycopy(nonce, 0, payload, 0, nonce.length);
        System.arraycopy(encrypted, 0, payload, nonce.length, encrypted.length);

        String payloadBase64 = Base64.getEncoder().encodeToString(payload);

        // ===== REQUEST BODY =====
        Map<String, Object> request = Map.of(
                "image", payloadBase64,
                "deviceId", "test-device-01"
        );

        // ===== CALL ENDPOINT =====
        mockMvc.perform(
                        post("/api/face/evaluate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }
}
