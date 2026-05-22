package com.example.labb1new.models;

import java.io.Serializable;

public class GameConfig implements Serializable {

    public enum Mode { PAIRS, TRIPLES }

    private int gridSize;
    private Mode mode;


    public GameConfig(int gridSize, Mode mode) {
        this.gridSize = gridSize;
        this.mode = mode;
    }

    public int getGridSize() { return gridSize; }
    public Mode getMode() { return mode; }

}

