package com.example.lotyintsdapp;

public class Italok {
    private String name;
    private String ar;
    private float csillag;
    private int kep;

    public Italok(String name, String ar, float csillag, int kep) {
        this.name = name;
        this.ar = ar;
        this.csillag = csillag;
        this.kep = kep;
    }

    public Italok() {
    }

    public String getName() {
        return name;
    }

    public String getAr() {return ar;}

    public float getCsillag() {return csillag;}

    public int getKep() {
        return kep;
    }
}
