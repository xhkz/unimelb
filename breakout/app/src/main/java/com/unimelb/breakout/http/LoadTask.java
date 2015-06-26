package com.unimelb.breakout.http;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;

public class LoadTask extends AsyncTask<String, Void, XmlPullParser> {
    public AsyncResult delegate = null;
    private XmlPullParser xpp;

    public LoadTask() {
    }

    @Override
    protected XmlPullParser doInBackground(String... strings) {
        xpp = HttpUtils.getXmlPullParser(strings[0]);
        return xpp;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(XmlPullParser parser) {
        super.onPostExecute(parser);
        delegate.asyncResult(xpp);
    }
}
