package com.gpt.dynlayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

class ViewIdGenerator {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    @SuppressLint("NewApi")
    public static int generateViewId() {

        if (Build.VERSION.SDK_INT < 17) {
            for (;;) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }

    }
}

public class ShowListActivity extends AppCompatActivity {

    private FeedAdapter listAdapter;
    private LruCache<String, Bitmap> mMemoryCache;
    private ArrayList<String[]> values;
    private ProgressDialog mProgress;

    Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
    void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creating Layout
        final LinearLayout listLayout = new LinearLayout(this);

        ListView listView = new ListView(this);
        listLayout.setOrientation(LinearLayout.HORIZONTAL);
        listLayout.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.MATCH_PARENT));
        listLayout.setId(ViewIdGenerator.generateViewId());

        values = new ArrayList<>();
        // test code
        String[] s = new String[3];
        s[0] = "";
        s[1] = "";
        s[2] = "";
        values.add(s);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };

        listAdapter = new FeedAdapter(this, mMemoryCache, values);
        listView.setAdapter(listAdapter);
        listLayout.addView(listView);
        setContentView(listLayout);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // init mProgress
        mProgress = new ProgressDialog(this);

        class DownloadJsonTask extends AsyncTask<URL,Void,JSONObject> {
            InputStream is;
            JSONObject json;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(mProgress != null && !ShowListActivity.this.isFinishing()) {
                    mProgress.show();
                }
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                if (listAdapter != null && jsonObject != null) {
                    listAdapter.notifyDataSetInvalidated();
                    listAdapter.parseJson(json);
                    listAdapter.notifyDataSetChanged();
                }
                if(mProgress != null && !ShowListActivity.this.isFinishing()) {
                    mProgress.dismiss();
                }
            }
            @Override
            protected JSONObject doInBackground(URL... params) {
                URL url = params[0];
                try {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setRequestProperty("content-type","application/json; charset=UTF-8");
                    // Starts the query
                    conn.connect();
                    int response = conn.getResponseCode();
                    Log.d("Response JSON", "The response is: " + response);
                    is = conn.getInputStream();
                    String jsonString = getStringFromInputStream(is, conn.getContentEncoding());
                    try {
                        json = (JSONObject) new JSONTokener(jsonString).nextValue();
                    } catch (JSONException e) {
                        Log.e("JSON", e.getMessage());
                    }
                } catch (IOException io) {

                } finally {

                }
                return json;
            }
            // convert InputStream to String
            private String getStringFromInputStream(InputStream is, String encoding) {

                BufferedReader br = null;
                StringBuilder sb = new StringBuilder();

                String line;
                try {
                    br = new BufferedReader(new InputStreamReader(is));
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return sb.toString();

            }
        }
        try {
            new DownloadJsonTask().execute(new URL("https://dl.dropboxusercontent.com/u/746330/facts.json"));
        } catch (MalformedURLException e) {
            Log.e("DownloadJsonTask", e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mProgress != null)
        {
            mProgress.dismiss();
            mProgress = null;
        }
    }
}
