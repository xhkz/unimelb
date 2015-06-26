package com.unimelb.breakout.model;

/**
 * Created by Xiaoyu on 2014/10/9.
 */
public class LifeCounter {
    private int count;

    public LifeCounter() {
        count = 1;
    }

    public int getCount() {
        return count;
    }

    public void add() {
        count++;
    }

    public void reduce() { count--; }
}
