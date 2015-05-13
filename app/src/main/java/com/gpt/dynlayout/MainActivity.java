package com.gpt.dynlayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.RefWatcher;

import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

//class Cat {
//}
//class Box {
//    Cat hiddenCat;
//}
//class Docker {
//    static Box container;
//}

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;

    private TextView getResultTextView() {
        return resultTextView;
    }

    private void setResultTextView(TextView resultTextView) {
        this.resultTextView = resultTextView;
    }
    // test memory leak
    private static Drawable sBackground;

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = LeakMonitorApp.getRefWatcher(this);
//        refWatcher.watch(MainActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        // creating LinearLayout
        LinearLayout linLayout = new LinearLayout(this);
        // specifying vertical orientation
        linLayout.setOrientation(LinearLayout.VERTICAL);
        // creating LayoutParams
        LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        // set LinearLayout as a root element of the screen
        setContentView(linLayout, linLayoutParam);

        LayoutParams lpView = new LayoutParams(LayoutParams.MATCH_PARENT, 100);

        TextView tv = new TextView(this);
        tv.setText("TextView");
        tv.setTag("TextView");
        if (sBackground == null) {
            sBackground = getResources().getDrawable(R.drawable.jimi_bitmap_full);
        }
        tv.setBackgroundDrawable(sBackground);
        tv.setLayoutParams(lpView);
        linLayout.addView(tv);
        setResultTextView(tv);

        Button btn = new Button(this);
        btn.setText("Download");
        btn.setTag("Download");
        linLayout.addView(btn, lpView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringUrl = "http://www.google.com";
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new DownloadTask().execute(stringUrl);
                } else {
                    Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        LinearLayout.LayoutParams leftMarginParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        leftMarginParams.leftMargin = 50;

        Button btn1 = new Button(this);
        btn1.setText("GO to List");
        btn1.setTag("List");
        linLayout.addView(btn1, leftMarginParams);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShowListActivity.class);
                startActivity(intent);
            }
        });


        LinearLayout.LayoutParams rightGravityParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rightGravityParams.gravity = Gravity.END;

        Button btn2 = new Button(this);
        btn2.setText("Goto Map");
        linLayout.addView(btn2, rightGravityParams);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });


        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("My Activity");

        // memory leak test
//        Box box = new Box();
//        Cat schrodingerCat = new Cat();
//        box.hiddenCat = schrodingerCat;
//        Docker.container = box;
//        LeakMonitorApp.getRefWatcher(getApplicationContext()).watch(schrodingerCat);
//        exampleOne();
    }
    private void exampleOne() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    SystemClock.sleep(1000);
                }
            }
        }.start();
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return download(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // textView.setText(result);
            getResultTextView().setText(result);
        }
        private static final String DEBUG_TAG = "Http";
        // Given a URL, establishes an HttpUrlConnection and retrieves
        // content as a InputStream, which it returns as
        // a string.
        private String download(String myUrl) throws IOException {
            InputStream is = null;
            int len = 500;

            try {
                URL url = new URL(myUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                return readIt(is, len);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        // Reads an InputStream and converts it to a String.
        String readIt(InputStream stream, int len) throws IOException {
            Reader reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
    }

}
