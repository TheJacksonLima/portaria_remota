package com.jfl.FaceRecognition.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;

@Service
public class FaceEvaluationService {

    public String uploadImage(byte[] imageBytes, String name) {

        Bucket bucket = StorageClient.getInstance().bucket();

        Blob blob = bucket.create(
                "faces/" + name + ".jpg",
                imageBytes,
                "image/jpeg"
        );

        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        return String.format(
                "https://storage.googleapis.com/%s/%s",
                bucket.getName(),
                blob.getName()
        );
    }
    public boolean evaluate(byte[] imageBytes) {
        return imageBytes.length > 10_000;
    }
}
