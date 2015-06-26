package com.unimelb.breakout.file;

import android.content.Context;
import android.util.Log;

import com.unimelb.breakout.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropsUtils {

    public static Properties getProperties(Context context) {
        Properties properties = new Properties();

        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.config);
            properties.load(inputStream);
        } catch (IOException e) {
            Log.d("IOException", e.getMessage());
        }

        return properties;
    }
}
