package com.example.pranaykumar.mapboxapp;

public class Message {
    private String message,time,user,by;

    Message(String message,String time,String user,String by){
        this.message=message;
        this.time=time;
        this.user=user;
        this.by=by;

    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getUser() {
        return user;
    }

    public String getBy() {
        return by;
    }
}

