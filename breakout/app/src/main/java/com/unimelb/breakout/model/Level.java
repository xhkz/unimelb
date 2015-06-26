package com.unimelb.breakout.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Level implements Serializable, Parcelable {

    private static final long serialVersionUID = 3033950418080382940L;
    public static final int DEFAULT_BRICK = 0;
    private int seq;
    private int speed;
    private int ballSize;
    private double paddleWidthRatio;
    private double paddleHeightRatio;
    private double brickWidthRatio;
    private double brickHeightRatio;
    private int brickCount;
    private double direction;
    private double ballX;
    private double ballY;
    private DisplayMetrics dm;
    private int brickColor;
    private int bonusType;
    private int bonusIndex;
    private ObstacleType obstacleType;

    public Level() {
    }

    private Level(Parcel in) {
        seq = in.readInt();
        speed = in.readInt();
        ballSize = in.readInt();
        paddleWidthRatio = in.readDouble();
        paddleHeightRatio = in.readDouble();
        brickWidthRatio = in.readDouble();
        brickHeightRatio = in.readDouble();
        brickCount = in.readInt();
        direction = in.readDouble();
        ballX = in.readDouble();
        ballY = in.readDouble();
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setBallSize(int ballSize) {
        this.ballSize = ballSize;
    }

    public void setPaddleWidthRatio(double paddleWidthRatio) {
        this.paddleWidthRatio = paddleWidthRatio;
    }

    public void setPaddleHeightRatio(double paddleHeightRatio) {
        this.paddleHeightRatio = paddleHeightRatio;
    }

    public void setBrickWidthRatio(double brickWidthRatio) {
        this.brickWidthRatio = brickWidthRatio;
    }

    public void setBrickHeightRatio(double brickHeightRatio) {
        this.brickHeightRatio = brickHeightRatio;
    }

    public void setBrickCount(int brickCount) {
        this.brickCount = brickCount;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public void setBallX(double ballX) {
        this.ballX = ballX;
    }

    public void setBallY(double ballY) {
        this.ballY = ballY;
    }

    public void setDisplayMetrics(DisplayMetrics dm) {
        this.dm = dm;
    }

    public void setBrickColor(int brickColor) {
        this.brickColor = brickColor;
    }

    public void setBonusType(int bonusType) {
        this.bonusType = bonusType;
    }

    public void setBonusIndex(int bonusIndex) {
        this.bonusIndex = bonusIndex;
    }

    public void setObstacleType(ObstacleType obstacleType) {
        this.obstacleType = obstacleType;
    }

    public Paddle getPaddle() {
        int x = (int) ((1 - paddleWidthRatio) / 2 * dm.widthPixels);
        int y = (int) (dm.heightPixels * (1 - paddleHeightRatio));
        return new Paddle(x, y, (int) (dm.heightPixels * paddleHeightRatio), (int) (dm.widthPixels * paddleWidthRatio));
    }

    public Ball getBall() {
        return new Ball((int) (dm.widthPixels * ballX), (int) (dm.heightPixels * ballY), ballSize, (int) (speed * direction), speed);
    }

    public Obstacle getObstacle() {
        List<Brick> bricks = new ArrayList<Brick>();
        if (this.obstacleType != null) {

            // TODO: configurable
            int offsetY = (int) (dm.heightPixels * 0.15 * 1.1);
            int offsetX;

            double bWidth = dm.widthPixels * brickWidthRatio,
                    bHeight = dm.heightPixels * brickHeightRatio,
                    bSpaceX = bWidth * 1.2, bSpaceY = bHeight * 1.4;

            if (this.obstacleType.equals(ObstacleType.SQUARE)) {
                int lineCount = (int) Math.sqrt(this.brickCount);
                offsetX = (int) ((dm.widthPixels - bSpaceX * lineCount) / 2);
                for (int i = 0; i < brickCount; i++) {
                    bricks.add(new Brick(
                            (int) ((i % lineCount) * bSpaceX) + offsetX,
                            (int) (i / lineCount * bSpaceY) + offsetY,
                            (int) (bWidth), (int) (bHeight), brickColor,
                            i == bonusIndex ? bonusType : DEFAULT_BRICK));
                }
            }

            if (this.obstacleType.equals(ObstacleType.TRIANGLE)) {
                offsetX = (int) (0.2 * bWidth);
                int line = 0, counter = 0;
                while (counter < brickCount) {
                    line++;
                    for (int i = 0; i < line; i++) {
                        bricks.add(new Brick(
                                (int) ((i % line) * bSpaceX) + offsetX,
                                (int) ((line - 1) * bSpaceY) + offsetY,
                                (int) (bWidth), (int) (bHeight), brickColor,
                                counter == bonusIndex ? bonusType : DEFAULT_BRICK));
                        counter++;
                    }
                }
            }

            if (this.obstacleType.equals(ObstacleType.PYRAMID)) {
                offsetX = dm.widthPixels / 2;
                int line = 0, counter = 0;
                while (counter < brickCount) {
                    line++;
                    offsetX -= bSpaceX / 2;
                    for (int i = 0; i < line; i++) {
                        bricks.add(new Brick(
                                (int) ((i % line) * bSpaceX) + offsetX,
                                (int) ((line - 1) * bSpaceY) + offsetY,
                                (int) (bWidth), (int) (bHeight), brickColor,
                                counter == bonusIndex ? bonusType : DEFAULT_BRICK));
                        counter++;
                    }
                }
            }
        }

        return new Obstacle(bricks);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.seq);
        parcel.writeInt(this.speed);
        parcel.writeInt(this.ballSize);
        parcel.writeDouble(this.paddleWidthRatio);
        parcel.writeDouble(this.paddleHeightRatio);
        parcel.writeDouble(this.brickWidthRatio);
        parcel.writeDouble(this.brickHeightRatio);
        parcel.writeInt(this.brickCount);
        parcel.writeDouble(this.direction);
        parcel.writeDouble(this.ballX);
        parcel.writeDouble(this.ballY);
    }

    public static final Creator<Level> CREATOR
            = new Creator<Level>() {
        public Level createFromParcel(Parcel in) {
            return new Level(in);
        }

        public Level[] newArray(int size) {
            return new Level[size];
        }
    };
}
