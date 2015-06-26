package com.unimelb.breakout.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

import static java.lang.Math.abs;

public class Ball {

    private int size;
    private int ySpeed;
    private int xSpeed;
    private int posX;
    private int posY;

    public Ball(int x, int y, int size, int xSpeed, int ySpeed) {
        posX = x;
        posY = y;
        this.size = size;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public void revertY() {
        this.ySpeed = -ySpeed;
    }

    public void revertX() {
        this.xSpeed = -xSpeed;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(posX - size, posY - size,
                posX + size, posY + size, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP));
        canvas.drawCircle(posX, posY, size, paint);
    }

    public void move() throws InterruptedException {
        // xSpeed should converge to original speed.
        if (xSpeed > 5 || xSpeed < -5) {
            if (xSpeed - 1 >= 5) {
                xSpeed -= 1;
            } else if (xSpeed + 1 <= -5) {
                xSpeed += 1;
            }
        }

        posX += xSpeed;
        posY += ySpeed;
    }

    public void changeDirection(Direction direction) {
        int valX = abs(xSpeed);
        int valY = abs(ySpeed);
        switch (direction) {
            case NORTH_WEST:
                xSpeed = -valX;
                ySpeed = -valY;
                break;
            case NORTH_EAST:
                xSpeed = valX;
                ySpeed = -valY;
                break;
            case SOUTH_WEST:
                xSpeed = -valX;
                ySpeed = valY;
                break;
            case SOUTH_EAST:
                xSpeed = valX;
                ySpeed = valY;
                break;
        }
    }

    public void changeXSpeed(int xSpeed) {
        int newSpeed = this.xSpeed + xSpeed;
        if (newSpeed > 10) {
            newSpeed = 10;
        } else if (newSpeed < -10) {
            newSpeed = -10;
        }
        this.xSpeed = newSpeed;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getSize() {
        return size;
    }

    public int getXSpeed() {
        return xSpeed;
    }

    public int getYSpeed() {
        return ySpeed;
    }

}
