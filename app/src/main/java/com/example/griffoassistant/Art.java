package com.example.griffoassistant;

public class Art {
    public String id, nameArt, nameAuthor, textArt, image;


    public Art() {
    }

    public Art(String id, String nameArt, String nameAuthor, String textArt, String image) {
        this.id = id;
        this.nameArt = nameArt;
        this.nameAuthor = nameAuthor;
        this.textArt = textArt;
        this.image=image;
    }
}
