package com.unimelb.breakout.model;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

public class Obstacle {
    private List<Brick> bricks;

    public Obstacle(List<Brick> bricks) {
        this.bricks = bricks;
    }

    public synchronized void draw(Canvas canvas) {
        for (Brick brick : this.bricks) {
            brick.draw(canvas);
        }
    }

    public synchronized void update(List<Ball> balls, ScoreCounter score, Paddle paddle, LifeCounter lifeCounter) throws InterruptedException {
        List<Brick> toRemove = new ArrayList<Brick>();
        for (Brick b : this.bricks) {
            if (b.update(balls, score, paddle, lifeCounter)) {
                toRemove.add(b);
            }
        }
        this.bricks.removeAll(toRemove);
    }

    public int getCount() {
        return bricks.size();
    }
}
