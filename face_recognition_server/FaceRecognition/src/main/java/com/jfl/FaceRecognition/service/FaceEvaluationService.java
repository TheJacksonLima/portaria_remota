package com.jfl.FaceRecognition.service;

import org.springframework.stereotype.Service;

@Service
public class FaceEvaluationService {

    public boolean evaluate(byte[] imageBytes) {
        // MOCK:
        // Aqui entra:
        // - ML model
        // - OpenCV
        // - chamada externa
        // - comparação biométrica

        // Simula aprovação
        return imageBytes.length > 10_000;
    }
}
