package com.unimelb.breakout.file;

import android.graphics.Color;
import android.util.Log;

import com.unimelb.breakout.model.Level;
import com.unimelb.breakout.model.ObstacleType;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelParser {

    public LevelParser() {
    }

    public List<Level> parseLevels(XmlPullParser parser, String type) {
        List<Level> levels = new ArrayList<Level>();

        try {
            parser.next();
            if (type.equals("local"))
                parser.next();

            parser.require(XmlPullParser.START_TAG, null, "levels");

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();

                if (name.equals("level")) {
                    levels.add(readLevel(parser));
                } else {
                    skip(parser);
                }
            }
        } catch (XmlPullParserException e) {
            Log.d("IO_ERR", e.getMessage());
        } catch (IOException e) {
            Log.d("XPP_ERR", e.getMessage());
        }

        return levels;
    }

    private Level readLevel(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "level");

        Level level = new Level();
        Ratio ratio = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("seq")) {
                level.setSeq(readInt(parser, "seq"));
            } else if (name.equals("speed")) {
                level.setSpeed(readInt(parser, "speed"));
            } else if (name.equals("ball_size")) {
                level.setBallSize(readInt(parser, "ball_size"));
            } else if (name.equals("ratio")) {
                ratio = readRatio(parser);
            } else if (name.equals("brick_count")) {
                level.setBrickCount(readInt(parser, "brick_count"));
            } else if (name.equals("ball_x")) {
                level.setBallX(readDouble(parser, "ball_x"));
            } else if (name.equals("ball_y")) {
                level.setBallY(readDouble(parser, "ball_y"));
            } else if (name.equals("direction")) {
                level.setDirection(readDouble(parser, "direction"));
            } else if (name.equals("brick_color")) {
                level.setBrickColor(Color.parseColor(readText(parser)));
            } else if (name.equals("bonus_type")) {
                level.setBonusType(readInt(parser, "bonus_type"));
            } else if (name.equals("bonus_index")) {
                level.setBonusIndex(readInt(parser, "bonus_index"));
            } else if (name.equals("obstacle_type")) {
                level.setObstacleType(ObstacleType.getById(readInt(parser, "obstacle_type")));
            } else {
                skip(parser);
            }
        }

        if (ratio != null) {
            level.setPaddleWidthRatio(ratio.getPaddleWidth());
            level.setPaddleHeightRatio(ratio.getPaddleHeight());
            level.setBrickWidthRatio(ratio.getBrickWidth());
            level.setBrickHeightRatio(ratio.getBrickHeight());
        }

        return level;
    }

    private Ratio readRatio(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "ratio");
        double paddleWidth = 0.1;
        double paddleHeight = 0.01;
        double brickWidth = 0.05;
        double brickHeight = 0.01;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("paddle_width")) {
                paddleWidth = readDouble(parser, "paddle_width");
            } else if (name.equals("paddle_height")) {
                paddleHeight = readDouble(parser, "paddle_height");
            } else if (name.equals("brick_width")) {
                brickWidth = readDouble(parser, "brick_width");
            } else if (name.equals("brick_height")) {
                brickHeight = readDouble(parser, "brick_height");
            } else {
                skip(parser);
            }
        }

        return new Ratio(paddleWidth, paddleHeight, brickWidth, brickHeight);
    }

    private int readInt(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, tag);
        int value = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, null, tag);
        return value;
    }

    private double readDouble(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, tag);
        double value = Double.parseDouble(readText(parser));
        parser.require(XmlPullParser.END_TAG, null, tag);
        return value;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    class Ratio {
        private double paddleWidth;
        private double paddleHeight;
        private double brickWidth;
        private double brickHeight;

        Ratio(double paddleWidth, double paddleHeight, double brickWidth, double brickHeight) {
            this.paddleWidth = paddleWidth;
            this.paddleHeight = paddleHeight;
            this.brickWidth = brickWidth;
            this.brickHeight = brickHeight;
        }

        public double getPaddleWidth() {
            return paddleWidth;
        }

        public double getPaddleHeight() {
            return paddleHeight;
        }

        public double getBrickWidth() {
            return brickWidth;
        }

        public double getBrickHeight() {
            return brickHeight;
        }
    }
}
