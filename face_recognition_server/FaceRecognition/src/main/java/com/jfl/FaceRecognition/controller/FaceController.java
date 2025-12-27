package com.jfl.FaceRecognition.controller;

import com.jfl.FaceRecognition.dto.FaceRequest;
import com.jfl.FaceRecognition.security.CryptoService;
import com.jfl.FaceRecognition.service.FaceEvaluationService;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import java.util.Map;

@RestController
@RequestMapping("/api/face")
public class FaceController {
    private static final Logger log = LoggerFactory.getLogger(FaceController.class);


    private final CryptoService cryptoService;
    private final FaceEvaluationService evaluationService;

    public FaceController(
            CryptoService cryptoService,
            FaceEvaluationService evaluationService
    ) {
        this.cryptoService = cryptoService;
        this.evaluationService = evaluationService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluate(@Valid @RequestBody FaceRequest request) {
        log.info( "Face evaluation request received | deviceId={}", request.getDeviceId());

        byte[] imageBytes = cryptoService.decrypt(request.getImage());

        boolean authorized = evaluationService.evaluate(imageBytes);

        if (authorized) {
            log.info( "Face evaluation: authorized!");
            return ResponseEntity.ok(Map.of("status", "ok"));
        }

        log.info( "Face evaluation: denied!");
        return ResponseEntity.status(403)
                .body(Map.of("status", "denied"));
    }
}
