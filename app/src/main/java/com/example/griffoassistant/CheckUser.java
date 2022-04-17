package com.example.griffoassistant;

public class CheckUser {
    public String id, idUser, last, name;
    public boolean status;
    public int count;


    public CheckUser() {
    }

    public CheckUser(String id, String idUser, String last, boolean status, int count) {
        this.id = id;
        this.idUser = idUser;
        this.last = last;
        this.status = status;
        this.count=count;
    }
}
