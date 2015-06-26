package com.unimelb.breakout.model;

public enum ObstacleType {
    TRIANGLE(1), PYRAMID(2), SQUARE(3);

    private int id;

    private ObstacleType(int id) {
        this.id = id;
    }

    public static ObstacleType getById(int id) {
        for (ObstacleType o : values()) {
            if (o.id == id) return o;
        }
        return PYRAMID;
    }
}
