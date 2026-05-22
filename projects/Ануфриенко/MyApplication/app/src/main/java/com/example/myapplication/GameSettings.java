package com.example.myapplication;

public class GameSettings {

    private int rows;
    private int cols;
    private int startTimeSeconds;

    public GameSettings(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.startTimeSeconds = 20;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getStartTimeSeconds() {
        return startTimeSeconds;
    }

    public void setFieldSize(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }
}
