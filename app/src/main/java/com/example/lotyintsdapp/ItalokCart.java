package com.example.lotyintsdapp;

public class ItalokCart {
    private String italID;
    private String name;
    private String ar;
    private float csillag;
    private int kep;

    public ItalokCart(String italID, String name, String ar, float csillag, int kep) {
        this.italID = italID;
        this.name = name;
        this.ar = ar;
        this.csillag = csillag;
        this.kep = kep;
    }

    public ItalokCart() {}

    public String getItalID() {
        return italID;
    }

    public void setItalID(String italID) {
        this.italID = italID;
    }

    public String getName() {
        return name;
    }

    public String getAr() {
        return ar;
    }

    public float getCsillag() {
        return csillag;
    }

    public int getKep() {
        return kep;
    }
}
