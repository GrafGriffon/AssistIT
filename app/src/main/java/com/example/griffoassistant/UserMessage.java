package com.example.griffoassistant;

import java.util.List;

public class UserMessage {
    public String id, message, from, image;
    public List<String>list;


    public UserMessage() {
    }

    public UserMessage(String id, String message, String from, String image) {
        this.id = id;
        this.message = message;
        this.from = from;
        this.image=image;
    }
}
