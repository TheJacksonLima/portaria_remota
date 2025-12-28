package com.jfl.FaceRecognition.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseMessagingService.class);

    public void sendApprovalRequest(String requestId, String imageUrl, String deviceId) {

        Message message = Message.builder()
                .setTopic("face-approval")
                .setNotification(
                        Notification.builder()
                                .setTitle("Nova solicitação de acesso")
                                .setBody("Toque para aprovar ou negar")
                                .build()
                )
                .putData("requestId", requestId)
                .putData("imageUrl", imageUrl)
                .putData("deviceId", deviceId)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Firebase message sent | response={}", response);
        } catch (Exception e) {
            log.error("Error sending Firebase message", e);
        }
    }
}
