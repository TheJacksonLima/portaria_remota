package com.jfl.FaceRecognition.dto;


import jakarta.validation.constraints.NotBlank;

public class FaceRequest {

    @NotBlank
    private String image;

    private String deviceId;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
