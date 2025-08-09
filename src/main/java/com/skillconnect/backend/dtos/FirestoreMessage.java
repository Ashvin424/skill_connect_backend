package com.skillconnect.backend.dtos;

import com.google.cloud.Timestamp;

public class FirestoreMessage {
    private String senderId;
    private String message;
    private Timestamp timestamp;

    public FirestoreMessage() {
    }

    public FirestoreMessage(String senderId, String message, Timestamp timestamp) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
