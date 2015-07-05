package com.unimelb.breakout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.unimelb.breakout.file.LevelParser;
import com.unimelb.breakout.file.PropsUtils;
import com.unimelb.breakout.http.AsyncResult;
import com.unimelb.breakout.http.LoadTask;
import com.unimelb.breakout.model.Level;
import com.unimelb.breakout.sound.SoundManager;

import org.xmlpull.v1.XmlPullParser;

import java.util.List;
import java.util.Properties;

public class MenuActivity extends Activity implements AsyncResult {
    public static List<Level> levels;
    protected SoundManager soundManager;
    private ProgressDialog progress;
    private ToggleButton mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mToggle = (ToggleButton) findViewById(R.id.music_switch);

        progress = new ProgressDialog(this);
        soundManager = new SoundManager(getApplicationContext());
        if (mToggle.isChecked())
            soundManager.play();
    }

    @Override
    protected void onDestroy() {
        soundManager.stop();
        soundManager.release();
        super.onDestroy();
    }

    public void switchMusic(View view) {
        if (mToggle.isChecked()) {
            soundManager.play();
        } else {
            soundManager.pause();
        }
    }

    public void asyncResult(XmlPullParser xpp) {
        LevelParser levelParser = new LevelParser();
        if (xpp == null) {
            Toast.makeText(this, "Unable to load levels from server", Toast.LENGTH_SHORT).show();
            return;
        }
        levels = levelParser.parseLevels(xpp, "remote");
        progress.dismiss();
        Intent intent = new Intent(this, LevelActivity.class);
        intent.putExtra("levels", levels.size());
        startActivity(intent);
    }

    public void remoteGame(View view) {
        Properties properties = PropsUtils.getProperties(getApplicationContext());
        progress.setMessage("Loading levels from server .....");
        progress.show();
        LoadTask task = new LoadTask();
        task.delegate = this;
        task.execute(properties.getProperty("levels_url"));
    }

    public void localGame(View view) {
        LevelParser levelParser = new LevelParser();
        levels = levelParser.parseLevels(getResources().getXml(R.xml.levels), "local");
        Intent intent = new Intent(this, LevelActivity.class);
        intent.putExtra("levels", levels.size());
        startActivity(intent);
    }

    public void viewScore(View view) {
        Intent intent = new Intent(this, ScoreActivity.class);
        startActivity(intent);
    }

    public void help(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void exit(View view) {
        this.finish();
    }
}
