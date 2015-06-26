package com.unimelb.breakout.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

public class Paddle {
    private int width;
    private final int height;
    private int posX;
    private int posY;
    private int speed;
    private int widthEnlarge;
    private int widthReduce;

    public Paddle(int x, int y, int height, int width) {
        posX = x;
        posY = y;
        this.height = height;
        this.width = width;
        this.widthEnlarge = width * 2;
        this.widthReduce = width / 2;
    }

    public int getPosY() {
        return posY;
    }

    public int getPosX() {
        return posX;
    }

    public void move(float moveX, int viewWidth) {
        if (posX + moveX < 0) {
            posX = 0;
        } else if (posX + moveX + width > viewWidth) {
            posX = viewWidth - width;
        } else {
            posX += moveX;
        }
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(posX, posY,
                posX, posY + height, Color.WHITE, Color.BLACK, Shader.TileMode.MIRROR));
        canvas.drawRoundRect(new RectF(posX, posY, posX + width, posY + height), 6, 6, paint);
    }

    public int getWidth() {
        return width;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void EnlargeWidth() {
        this.width = this.widthEnlarge;
    }

    public void ReduceWidth(){
        this.width = this.widthReduce;
    }

    public int getSpeed() {
        return speed;
    }
}
