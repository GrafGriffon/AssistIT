package com.example.griffoassistant;

public class Message {
    public String id, nameArt, nameAuthor, textArt, image;


    public Message() {
    }

    public Message(String id, String nameArt, String nameAuthor, String textArt, String image) {
        this.id = id;
        this.nameArt = nameArt;
        this.nameAuthor = nameAuthor;
        this.textArt = textArt;
        this.image=image;
    }
}
