package com.unimelb.breakout.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.unimelb.breakout.MainActivity;
import com.unimelb.breakout.R;

import java.util.List;

import static com.unimelb.breakout.model.Direction.NORTH_EAST;
import static com.unimelb.breakout.model.Direction.NORTH_WEST;
import static com.unimelb.breakout.model.Direction.SOUTH_EAST;
import static com.unimelb.breakout.model.Direction.SOUTH_WEST;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Brick {
    private int posX;
    private int posY;
    private int width;
    private int height;
    private int color;
    private int bonus;

    public Brick(int posX, int posY, int width, int height, int color, int bonus) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.color = color;
        this.bonus = bonus;
    }

    public void draw(Canvas canvas) {
        int thisColor;
        if (bonus == Level.DEFAULT_BRICK) {
            thisColor = color;
        } else {
            thisColor = Color.parseColor("#FF8800");
        }

        Paint paint = new Paint();
        paint.setShader(new LinearGradient(posX, posY,
                posX, posY + height, thisColor, Color.BLACK, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(new RectF(posX, posY, posX + width, posY + height), 5, 5, paint);

    }

    //if brick is hit, return true
    public boolean update(List<Ball> balls, ScoreCounter score, Paddle paddle, LifeCounter lifeCounter) throws InterruptedException {
        for (Ball ball : balls) {
            if (isUpperLeftBrickCorner(ball)) {
                ball.changeDirection(NORTH_WEST);
            } else if (isUpperRightBrickCorner(ball)) {
                ball.changeDirection(NORTH_EAST);
            } else if (isHorizontalAspect(ball)) {
                ball.revertY();
            } else if (isBottomLeftCorner(ball)) {
                ball.changeDirection(SOUTH_WEST);
            } else if (isBottomRightCorner(ball)) {
                ball.changeDirection(SOUTH_EAST);
            } else if (isVerticalAspect(ball)) {
                ball.revertX();
            } else {
                // this ball doesn't hit brick, check next one
                continue;
            }

            bonusBrickAffect(balls, ball, paddle, lifeCounter);
            score.add();
            return true;
        }

        // no ball hits brack
        return false;
    }

    private boolean isBottomRightCorner(Ball ball) {
        int lowerY = posY + height;
        int rightX = posX + width;
        return sqrt(pow(ball.getPosX() - rightX, 2)
                + pow(ball.getPosY() - lowerY, 2)) <= ball.getSize();
    }

    private boolean isBottomLeftCorner(Ball ball) {
        int lowerY = posY + height;
        return sqrt(pow(ball.getPosX() - posX, 2)
                + pow(ball.getPosY() - lowerY, 2)) <= ball.getSize();
    }

    public boolean isUpperLeftBrickCorner(Ball ball) {
        return sqrt(pow(ball.getPosX() - posX, 2)
                + pow(ball.getPosY() - posY, 2)) <= ball.getSize();
    }

    public boolean isUpperRightBrickCorner(Ball ball) {
        int rightX = posX + width;
        return sqrt(pow(ball.getPosX() - rightX, 2)
                + pow(ball.getPosY() - posY, 2)) <= ball.getSize();
    }

    public boolean isHorizontalAspect(Ball ball) {
        int upperY = ball.getPosY() - ball.getSize();
        int lowerY = ball.getPosY() + ball.getSize();
        int x = ball.getPosX();
        return (upperY <= posY + height & upperY > posY || lowerY >= posY & lowerY < posY + height)
                & x >= posX & x <= posX + width;
    }

    private boolean isVerticalAspect(Ball ball) {
        int y = ball.getPosY();
        int leftX = ball.getPosX() - ball.getSize();
        int rightX = ball.getPosX() + ball.getSize();
        return (rightX >= posX & rightX < posX + width || leftX <= posX + width & leftX > posX)
                & y >= posY & y <= posY + height;
    }

    private void bonusBrickAffect(List<Ball> balls, Ball ball, Paddle paddle, LifeCounter lifeCounter)
            throws InterruptedException {
        switch (bonus) {
            case 1:
                lifeCounter.add();
                break;
            case 2:
                // enlarge the player's paddle
                paddle.EnlargeWidth();
                break;
            case 3:
                // reduce the player's paddle
                paddle.ReduceWidth();
                break;
            case 4:
                Ball newBall = new Ball(ball.getPosX(), ball.getPosY(), ball.getSize(),
                        -ball.getXSpeed(), ball.getYSpeed());
                balls.add(newBall);
                break;
            default:
        }

        if (bonus == 1 || bonus == 2 || bonus == 3 || bonus == 4) {
            MainActivity.play(R.raw.bonusbrick);
        } else {
            MainActivity.play(R.raw.brickandwall);
        }
    }
}
