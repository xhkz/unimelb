package com.unimelb.breakout.http;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

public class HttpUtils {

    public static XmlPullParser getXmlPullParser(String addr) {
        HttpGet httpGet = new HttpGet(addr);
        XmlPullParserFactory factory;
        XmlPullParser xpp = null;
        try {
            HttpResponse response = new DefaultHttpClient().execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();

                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();
                xpp.setInput(new StringReader(EntityUtils.toString(entity, HTTP.UTF_8)));
            }

        } catch (XmlPullParserException e) {
            Log.d("XmlPullParserException", e.getMessage());
        } catch (IOException e) {
            Log.d("IOException", e.getMessage());
        }

        return xpp;
    }
}
