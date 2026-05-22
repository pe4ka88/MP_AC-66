package com.example.myapplication;
public class Record {

    public String name;
    public int timeLeft;
    public String mode;

    public Record() {
        // Пустой конструктор нужен для Firestore
    }

    public Record(String name, int timeLeft, String mode) {
        this.name = name;
        this.timeLeft = timeLeft;
        this.mode = mode;
    }
}