package com.unimelb.breakout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.unimelb.breakout.model.ScoreCounter;
import com.unimelb.breakout.model.ScoreItems;
import com.unimelb.breakout.views.GameView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;


public class MainActivity extends Activity {

    protected static final int GUI_UPDATE = 0x55;
    protected static final int UPDATE_INTERVAL = 10;
    private static final int GAME_OVER = -1;
    public static final String PLAYER_NAME_PROMPT = "Please specify your name:";
    private static SoundPool mSoundPool;
    private static SparseIntArray mStreamIds;
    private static SparseBooleanArray mIsSoundLoaded = new SparseBooleanArray();

    private GameView gameView;
    private VelocityTracker velocityTracker;
    private float lastTouchX;
    private Gson gson;
    private ScoreItems scoreItems;
    private ScoreCounter scoreCounter;
    private MediaPlayer mMediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSound();

        mMediaPlayer = MediaPlayer.create(this, R.raw.readygo);
        mMediaPlayer.start();

        gson = new Gson();
        scoreCounter = new ScoreCounter();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        try {
            File file = getFileStreamPath(ScoreActivity.scoreFileName);
            if (file.exists()) {
                FileInputStream fis = openFileInput(ScoreActivity.scoreFileName);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                scoreItems = gson.fromJson(sb.toString(), ScoreItems.class);
                fis.close();
            } else {
                scoreItems = new ScoreItems();
            }
        } catch (FileNotFoundException e) {
            scoreItems = new ScoreItems();
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            scoreItems = new ScoreItems();
            e.printStackTrace();
        } catch (IOException e) {
            scoreItems = new ScoreItems();
            e.printStackTrace();
        }

        int nLevel = getIntent().getIntExtra("LV", 0);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), (dm.widthPixels < dm.heightPixels ? R.drawable.backh : R.drawable.backw));
        bmp = Bitmap.createScaledBitmap(bmp, dm.widthPixels, dm.heightPixels, true);
        BitmapDrawable bd = new BitmapDrawable(bmp);
        gameView = new GameView(this, dm, nLevel, scoreCounter, scoreItems);
        gameView.setBackgroundDrawable(bd);

        setContentView(gameView);

        gameView.setOnTouchListener(
                new RelativeLayout.OnTouchListener() {
                    public boolean onTouch(View view,
                                           MotionEvent event) {
                        handleTouch(event);
                        return true;
                    }
                }
        );

        new Thread(new GuiThread()).start();
    }

    private void initSound() {
        mStreamIds = new SparseIntArray();
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    switch (sampleId) {
                        case 1:
                            mIsSoundLoaded.put(R.raw.bonusbrick, true);
                            break;
                        case 2:
                            mIsSoundLoaded.put(R.raw.brickandwall, true);
                            break;
                        case 3:
                            mIsSoundLoaded.put(R.raw.gameover, true);
                            break;
                        case 4:
                            mIsSoundLoaded.put(R.raw.levelup, true);
                            break;
                        case 5:
                            mIsSoundLoaded.put(R.raw.paddle, true);
                            break;
                        case 6:
                            mIsSoundLoaded.put(R.raw.loselife, true);
                            break;
                    }
                }
            }
        });
        mStreamIds.put(R.raw.bonusbrick, mSoundPool.load(getApplicationContext(), R.raw.bonusbrick, 1));
        mStreamIds.put(R.raw.brickandwall, mSoundPool.load(getApplicationContext(), R.raw.brickandwall, 1));
        mStreamIds.put(R.raw.gameover, mSoundPool.load(getApplicationContext(), R.raw.gameover, 1));
        mStreamIds.put(R.raw.levelup, mSoundPool.load(getApplicationContext(), R.raw.levelup, 1));
        mStreamIds.put(R.raw.paddle, mSoundPool.load(getApplicationContext(), R.raw.paddle, 1));
        mStreamIds.put(R.raw.loselife, mSoundPool.load(getApplicationContext(), R.raw.loselife, 1));
    }

    public static void play(int soundResourceId) {
        if (mSoundPool != null && mIsSoundLoaded.get(soundResourceId, false)) {
            mSoundPool.play(mStreamIds.get(soundResourceId), 1, 1, 1, 0, 1f);
        }
    }

    private void stopSound() {
        if (mSoundPool != null) {
            for (int i = 0; i < mStreamIds.size(); i++) {
                mSoundPool.stop(mStreamIds.valueAt(i));
            }
            mSoundPool.release();
            mSoundPool = null;
        }
    }

    @Override
    protected void onDestroy() {
        stopSound();
        super.onDestroy();
    }

    private void handleTouch(MotionEvent event) {
        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);
        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                float currentX = event.getX();
                velocityTracker.addMovement(event);
                int unit = 10;
                velocityTracker.computeCurrentVelocity(unit, 10);
                float xVelocity = VelocityTrackerCompat.getXVelocity(velocityTracker, pointerId);
                gameView.setPaddleSpeed(xVelocity);
                gameView.movePaddle(currentX - lastTouchX);
                lastTouchX = currentX;
                break;
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MainActivity.GUI_UPDATE:
                    gameView.invalidate();
                    super.handleMessage(msg);
                    break;
                case MainActivity.GAME_OVER:
                    gameView.invalidate();
                    play(R.raw.gameover);
                    promptForUpdatingRank();
                    break;
            }

        }
    };

    class GuiThread implements Runnable {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = new Message();
                message.what = MainActivity.GUI_UPDATE;

                MainActivity.this.handler.sendMessage(message);
                try {
                    gameView.update();
                    Thread.sleep(UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            Message message = new Message();
            message.what = MainActivity.GAME_OVER;
            MainActivity.this.handler.sendMessage(message);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void promptForUpdatingRank() {
        final int count = scoreCounter.getCount();
        final Intent rankIntent = new Intent(this, ScoreActivity.class);
        rankIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        rankIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (scoreItems.isInTop10(count)) {

            final AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Congratulations");
            alert.setMessage(PLAYER_NAME_PROMPT);

            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String player = input.getText().toString();
                    // Do something with value!
                    if (player != null && !player.isEmpty()) {
                        scoreItems.updateRank(player, count);

                        try {
                            FileOutputStream fos = openFileOutput(ScoreActivity.scoreFileName,
                                    Context.MODE_PRIVATE);
                            String json = gson.toJson(scoreItems);
                            fos.write(json.getBytes());
                            fos.close();

                            startActivity(rankIntent);
                            finish();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        alert.setMessage(PLAYER_NAME_PROMPT);
                    }
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            if (!this.isDestroyed())
                alert.show();
        }
    }
}
