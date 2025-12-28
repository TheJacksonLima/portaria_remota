package com.jfl.FaceRecognition.controller;

import com.jfl.FaceRecognition.dto.FaceRequest;
import com.jfl.FaceRecognition.firebase.FirebaseMessagingService;
import com.jfl.FaceRecognition.security.CryptoService;
import com.jfl.FaceRecognition.service.FaceEvaluationService;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/face")
public class FaceController {
    private static final Logger log = LoggerFactory.getLogger(FaceController.class);

    private final CryptoService cryptoService;
    private final FaceEvaluationService evaluationService;
    private final FirebaseMessagingService firebaseMessagingService;

    public FaceController( CryptoService cryptoService, FaceEvaluationService evaluationService, FirebaseMessagingService firebaseMessagingService) {
        this.cryptoService = cryptoService;
        this.evaluationService = evaluationService;
        this.firebaseMessagingService= firebaseMessagingService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluate(@Valid @RequestBody FaceRequest request) {

        log.info("Face evaluation request received | deviceId={}", request.getDeviceId());

        byte[] imageBytes = cryptoService.decrypt(request.getImage());
        String requestId = UUID.randomUUID().toString();

        String imageUrl = evaluationService.uploadImage(imageBytes, requestId);

        firebaseMessagingService.sendApprovalRequest(
                requestId,
                imageUrl,
                request.getDeviceId()
        );

        log.info("Face sent for human approval | requestId={}", requestId);

        return ResponseEntity.accepted().body(Map.of(
                "status", "pending",
                "requestId", requestId
        ));
    }

}
