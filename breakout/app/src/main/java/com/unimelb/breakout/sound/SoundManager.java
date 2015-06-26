package com.unimelb.breakout.sound;

import android.content.Context;
import android.media.MediaPlayer;

import com.unimelb.breakout.R;

public class SoundManager {

    private MediaPlayer mMediaPlayer;

    public SoundManager(Context context) {
        mMediaPlayer = MediaPlayer.create(context, R.raw.bgm);
        mMediaPlayer.setLooping(true);
    }

    public void play() {
        mMediaPlayer.start();
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.getCurrentPosition();
            mMediaPlayer.stop();
        }
    }


    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
