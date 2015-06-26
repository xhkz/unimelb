package com.unimelb.breakout.model;

public class ScoreCounter {

    private int count;

    public ScoreCounter() {
        count = 0;
    }

    public int getCount() {
        return count;
    }

    public void add() {
        count++;
    }

}


