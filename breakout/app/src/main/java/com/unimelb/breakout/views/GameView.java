package com.unimelb.breakout.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

import com.unimelb.breakout.MainActivity;
import com.unimelb.breakout.MenuActivity;
import com.unimelb.breakout.R;
import com.unimelb.breakout.model.Ball;
import com.unimelb.breakout.model.Level;
import com.unimelb.breakout.model.LifeCounter;
import com.unimelb.breakout.model.Obstacle;
import com.unimelb.breakout.model.Paddle;
import com.unimelb.breakout.model.ScoreCounter;
import com.unimelb.breakout.model.ScoreItems;

import java.util.ArrayList;
import java.util.List;

import static com.unimelb.breakout.model.Direction.NORTH_EAST;
import static com.unimelb.breakout.model.Direction.NORTH_WEST;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class GameView extends View {
    private final int displayHeight;
    private final int displayWidth;
    private final List<Level> levels;
    private final DisplayMetrics displayMetrics;
    private final ScoreCounter scoreCounter;
    private final ScoreItems scoreItems;
    private boolean isOver;
    private int nLevel;
    private List<Ball> balls;
    private Obstacle obstacle;
    private Paddle paddle;
    private Ball ball;
    private Paint paint;
    private LifeCounter lifeCounter;

    public GameView(Context context, DisplayMetrics dm, int nLevel, ScoreCounter scoreCounter,
                    ScoreItems scoreItems) {
        super(context);
        this.displayMetrics = dm;
        this.scoreCounter = scoreCounter;
        this.scoreItems = scoreItems;
        this.nLevel = nLevel;

        setFocusable(true);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        displayWidth = dm.widthPixels;
        displayHeight = dm.heightPixels;
        levels = MenuActivity.levels;
        Level level = levels.get(nLevel);
        level.setDisplayMetrics(dm);
        lifeCounter = new LifeCounter();
        ball = level.getBall();
        balls = new ArrayList<Ball>();
        balls.add(ball);
        paddle = level.getPaddle();
        obstacle = level.getObstacle();
    }

    public void onDraw(Canvas canvas) {
        Rect rect;
        if (displayWidth < displayHeight) {
            rect = new Rect(0, 0, displayWidth, (int) (displayHeight * 0.1));
        } else {
            rect = new Rect(0, 0, displayWidth, (int) (displayHeight * 0.05));
        }
        paint.setColor(Color.argb(127, 0, 0, 0));
        canvas.drawRect(rect, paint);
        paint.setColor(Color.argb(127, 255, 255, 255));
        paint.setTextSize(displayWidth / (displayWidth < displayHeight ? 16 : 28));
        if (displayWidth > displayHeight) {
            canvas.drawText("Score: " + scoreCounter.getCount(), 10, 10 + displayHeight / 28, paint);
            canvas.drawText("Level: " + (nLevel + 1), displayWidth / 4 + 10, 10 + displayHeight / 28, paint);
            canvas.drawText("Lives: " + lifeCounter.getCount(), displayWidth * 3 / 4 + 10, 10 + displayHeight / 28, paint);
        } else {
            canvas.drawText("Score: " + scoreCounter.getCount(), 10, 10 + displayHeight / 28, paint);
            canvas.drawText("Level: " + (nLevel + 1), displayWidth / 2 + 10, 10 + displayHeight / 28, paint);
            canvas.drawText("Lives: " + lifeCounter.getCount(), displayWidth / 2 + 10, 20 + displayHeight / 14, paint);
        }
        if (isOver) {
            paint.setColor(Color.RED);
            paint.setTextSize(displayWidth / 9);
            canvas.drawText("Game Over", displayWidth * 2 / 9, displayHeight * 3 / 5, paint);
        } else {
            if (displayWidth > displayHeight) {
                canvas.drawText("Rank: " + scoreItems.rankString(scoreCounter.getCount()), displayWidth / 2 + 10, 10 + displayHeight / 28, paint);
            } else {
                canvas.drawText("Rank: " + scoreItems.rankString(scoreCounter.getCount()), 10, 20 + displayHeight / 14, paint);
            }

            paint.setColor(Color.RED);
            for (Ball ball : balls) {
                ball.draw(canvas);
            }

            paint.setColor(Color.GREEN);
            paddle.draw(canvas);
            obstacle.draw(canvas);
        }
    }

    public void update() throws InterruptedException {
        moveBalls(balls);
        List<Ball> outBalls = new ArrayList<Ball>();


        for (Ball ball : balls) {
            if (isLeftPaddleCorner(ball)) {
                ball.changeDirection(NORTH_WEST);
            } else if (isRightPaddleCorner(ball)) {
                ball.changeDirection(NORTH_EAST);
            } else if (isDisplayTop(ball)) {
                ball.revertY();
            } else if (isPaddleTop(ball)) {
                ball.revertY();
                ball.changeXSpeed(paddle.getSpeed());
            } else if (isVerticalAspect(ball)) {
                ball.revertX();
            } else if (ball.getPosY() + ball.getSize() >= displayHeight) {
                outBalls.add(ball);

                if (balls.size() - intersection(balls, outBalls).size() == 0) {
                    lifeCounter.reduce();
                    if (lifeCounter.getCount() > 0) {
                        MainActivity.play(R.raw.loselife);
                        Ball newBall = new Ball(displayWidth * 6 / 10, displayHeight / 2,
                                ball.getSize(), ball.getXSpeed(), ball.getYSpeed());
                        balls.add(newBall);
                    } else {
                        gameOver();
                    }
                }
            } else {
                obstacle.update(balls, scoreCounter, paddle, lifeCounter);
                if (obstacle.getCount() == 0) {
                    nLevel++;
                    nextLevel();
                }
            }

            if (isLeftPaddleCorner(ball) || isRightPaddleCorner(ball) || isPaddleTop(ball)) {
                MainActivity.play(R.raw.paddle);
            } else if (isVerticalAspect(ball) || isDisplayTop(ball)) {
                MainActivity.play(R.raw.brickandwall);
            }
        }

        balls.removeAll(intersection(balls, outBalls));
    }

    private void moveBalls(List<Ball> balls) throws InterruptedException {
        for (Ball ball : balls) {
            ball.move();
        }
    }

    private void nextLevel() throws InterruptedException {
        if (nLevel > levels.size() - 1) {
            gameOver();
        } else {
            MainActivity.play(R.raw.levelup);
            Level level = levels.get(nLevel);
            level.setDisplayMetrics(displayMetrics);
            ball = level.getBall();
            balls.clear();
            balls.add(ball);
            paddle = level.getPaddle();
            obstacle = level.getObstacle();
        }
    }

    private boolean isDisplayTop(Ball ball) {
        return ball.getPosY() - (displayHeight * (displayWidth < displayHeight ? 0.1 : 0.05)) <= ball.getSize();
    }

    public boolean isPaddleTop(Ball ball) {
        int ballSize = ball.getSize();
        int ballPosY = ball.getPosY();
        int ballPosX = ball.getPosX();
        return ballPosY >= paddle.getPosY() - ballSize && ballPosX > paddle.getPosX() && ballPosX <= paddle.getPosX() + paddle.getWidth();
    }

    private boolean isVerticalAspect(Ball ball) {
        int ballSize = ball.getSize();
        int ballPosX = ball.getPosX();
        return ballPosX <= ballSize || ballPosX >= displayWidth - ballSize;
    }

    public boolean isLeftPaddleCorner(Ball ball) {
        return sqrt(pow(ball.getPosX() - paddle.getPosX(), 2)
                + pow(ball.getPosY() - paddle.getPosY(), 2)) <= ball.getSize();
    }

    public boolean isRightPaddleCorner(Ball ball) {
        int rightX = paddle.getPosX() + paddle.getWidth();
        return sqrt(pow(ball.getPosX() - rightX, 2)
                + pow(ball.getPosY() - paddle.getPosY(), 2)) <= ball.getSize();
    }

    public void movePaddle(float moveX) {
        paddle.move(moveX, this.getWidth());
    }

    public void gameOver() throws InterruptedException {
        this.isOver = true;
        throw new InterruptedException();
    }

    public void setPaddleSpeed(float xVelocity) {
        paddle.setSpeed((int) xVelocity);
    }


    private <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

}