package com.example.pranaykumar.mapboxapp;

public class MsgSender {
    private String sender;

    MsgSender(String sender){
        this.sender=sender;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
