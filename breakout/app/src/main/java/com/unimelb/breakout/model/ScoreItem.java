package com.unimelb.breakout.model;

import java.io.Serializable;

public class ScoreItem implements Serializable {

    private final String player;
    private final int score;

    public ScoreItem(String player, int score) {
        this.player = player;
        this.score = score;
    }

    public String getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScoreItem)) return false;

        ScoreItem scoreItem = (ScoreItem) o;

        if (score != scoreItem.score) return false;
        if (!player.equals(scoreItem.player)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = player.hashCode();
        result = 31 * result + score;
        return result;
    }
}
