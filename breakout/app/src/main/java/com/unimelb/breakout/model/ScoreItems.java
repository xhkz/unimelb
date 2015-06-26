package com.unimelb.breakout.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScoreItems implements Serializable {

    private List<ScoreItem> items;

    public ScoreItems() {
        items = new ArrayList<ScoreItem>();
    }

    public String rankString(int score) {
        if (isInTop10(score)) {
            return String.valueOf(rankScore(score));
        } else {
            return "-";
        }
    }

    public boolean isInTop10(int score) {
        return rankScore(score) != -1;
    }

    private int rankScore(int score) {
        List<Integer> scores = new ArrayList<Integer>();
        for (ScoreItem item : items) {
            scores.add(item.getScore());
        }

        Collections.sort(scores, Collections.reverseOrder());
        int i;
        for (i = 0; i < scores.size(); i++) {
            if (score > scores.get(i)) {
                return i + 1;
            }
        }

        if (scores.size() == 0) {
            return 1;
        } else if (i + 1 <= 10) {
            return i + 1;
        } else {
            return -1;
        }
    }

    public void updateRank(String playerName, int score) {
        items.add(new ScoreItem(playerName, score));

        removeDuplicates();
        Collections.sort(items, new Comparator<ScoreItem>() {
            public int compare(ScoreItem o1, ScoreItem o2) {
                return o2.getScore() - o1.getScore();
            }
        });

        while (items.size() > 10) {
            items.remove(items.size() - 1);
        }
    }

    private void removeDuplicates() {
        Set hashSet = new HashSet();
        hashSet.addAll(items);
        items.clear();
        items.addAll(hashSet);
    }

    public List<ScoreItem> getItems() {
        return items;
    }

    public void merge(ScoreItems remoteScoreItems) {
        if (remoteScoreItems != null) {
            items.addAll(remoteScoreItems.getItems());

            removeDuplicates();
            Collections.sort(items, new Comparator<ScoreItem>() {
                public int compare(ScoreItem o1, ScoreItem o2) {
                    return o2.getScore() - o1.getScore();
                }
            });

            while (items.size() > 10) {
                items.remove(items.size() - 1);
            }
        }
    }
}
