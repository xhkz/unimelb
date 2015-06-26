package com.unimelb.breakout;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.unimelb.breakout.file.PropsUtils;
import com.unimelb.breakout.model.ScoreItem;
import com.unimelb.breakout.model.ScoreItems;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreActivity extends ListActivity {

    public static final String scoreFileName = "score_file";
    private ScoreItems localScores;
    private Gson gson;
    private SimpleAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Downloading scores from server...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        gson = new Gson();
        localScores = localScores();
        new DownloadTask(getApplicationContext()).execute(PropsUtils.getProperties(getApplicationContext()).getProperty("score_url"));

        String[] from = {"rank", "name", "score"};
        int[] to = {R.id.rank, R.id.player, R.id.score};
        adapter = new SimpleAdapter(this, buildData(localScores.getItems()), R.layout.scorerow, from, to);
        setListAdapter(adapter);
    }

    private ArrayList<Map<String, String>> buildData(List<ScoreItem> items) {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        int i = 1;
        for (ScoreItem item : items) {
            list.add(makePair(i++, item.getPlayer(), item.getScore()));
        }

        return list;
    }

    private HashMap<String, String> makePair(int rank, String name, int score) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("rank", String.valueOf(rank));
        item.put("name", name);
        item.put("score", String.valueOf(score));
        return item;
    }

    private ScoreItems localScores() {
        ScoreItems scoreItems;
        try {
            FileInputStream fis = openFileInput(scoreFileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            scoreItems = gson.fromJson(sb.toString(), ScoreItems.class);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            scoreItems = new ScoreItems();
        } catch (IOException e) {
            e.printStackTrace();
            scoreItems = new ScoreItems();
        }

        return scoreItems;
    }

    private class DownloadTask extends AsyncTask<String, Integer, ScoreItems> {
        private Context context;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected ScoreItems doInBackground(String... sUrl) {
            InputStream input = null;
            HttpURLConnection connection = null;
            ScoreItems scoreItems = null;

            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                int fileLength = connection.getContentLength();
                StringBuffer fileContent = new StringBuffer("");
                input = connection.getInputStream();
                byte buffer[] = new byte[4096];
                long total = 0;
                int count;

                while ((count = input.read(buffer)) != -1) {
                    total += count;
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    fileContent.append(new String(buffer, 0, count));
                }

                scoreItems = gson.fromJson(fileContent.toString(), ScoreItems.class);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }

            return scoreItems;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(ScoreItems scoreItems) {

            if (scoreItems == null) {
                Toast.makeText(context, "Cannot connect to network", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            } else {
                localScores.merge(scoreItems);
                String[] from = {"rank", "name", "score"};
                int[] to = {R.id.rank, R.id.player, R.id.score};

                try {
                    FileOutputStream fos = context.getApplicationContext().openFileOutput(scoreFileName, Context.MODE_PRIVATE);
                    String json = gson.toJson(localScores);
                    fos.write(json.getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                new UploadTask(context).execute("http://bback.herokuapp.com/bback/update/");
                adapter = new SimpleAdapter(getApplicationContext(), buildData(localScores.getItems()), R.layout.scorerow, from, to);
                Toast.makeText(context, "New score downloaded", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UploadTask extends AsyncTask<String, Integer, Boolean> {
        private final Context context;

        public UploadTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... sUrl) {
            boolean result = false;
            HttpClient hc = new DefaultHttpClient();
            String message;

            HttpPost httpPost = new HttpPost(sUrl[0]);

            try {
                message = gson.toJson(localScores);

                httpPost.setEntity(new StringEntity(message, "UTF8"));
                httpPost.setHeader("Content-type", "application/json");
                HttpResponse resp = hc.execute(httpPost);
                if (resp != null) {
                    if (resp.getStatusLine().getStatusCode() == 200)
                        result = true;
                }

            } catch (Exception e) {
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Uploading merged scores to server");
            progressDialog.setIndeterminate(true);
            progressDialog.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (!result) {
                Toast.makeText(context, "Cannot connect to network", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            } else {
                Toast.makeText(context, "New score uploaded", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

