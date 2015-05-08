package com.gpt.dynlayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class ViewIdGenerator {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    @SuppressLint("NewApi")
    public static int generateViewId() {

        if (Build.VERSION.SDK_INT < 17) {
            for (; ; ) {
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

    private ArrayAdapter listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creating Layout
        RelativeLayout relativeLayout = new RelativeLayout(this);
        LinearLayout listLayout = new LinearLayout(this);
        TextView item = new TextView(this);

        ListView listView = new ListView(this);

        String[][] values = new String[10][3];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 3; j++) {
                values[i][j] = "" + i + "-" + j;
            }
        }


        class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            ImageView bmImage;
            InputStream is;
            public DownloadImageTask(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                Bitmap mIcon11 = null;
                try {
                    URL url = new URL(urldisplay);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    int response = conn.getResponseCode();
                    Log.d("Response", "The response is: " + response);
                    is = conn.getInputStream();
//                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(is);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
                return mIcon11;
            }

            protected void onPostExecute(Bitmap result) {
                if(result != null) {
                    bmImage.setImageBitmap(result);
                    if (listAdapter != null) {
//                        listAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        class MyAdapter extends ArrayAdapter<String[]> {

            public MyAdapter(Context context, String[][] strings) {
                super(context, -1, -1, strings);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                LinearLayout itemLayout = new LinearLayout(getContext());
                itemLayout.setOrientation(LinearLayout.VERTICAL);
                itemLayout.setLayoutParams(new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.WRAP_CONTENT,
                        AbsListView.LayoutParams.WRAP_CONTENT));
                itemLayout.setId(ViewIdGenerator.generateViewId());

                TextView listText = new TextView(getContext());
                listText.setId(ViewIdGenerator.generateViewId());

                itemLayout.addView(listText);
                listText.setText(super.getItem(position)[0]);

                LinearLayout itemInnerLayout = new LinearLayout(getContext());
                itemInnerLayout.setOrientation(LinearLayout.HORIZONTAL);
                AbsListView.LayoutParams innerParams = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.WRAP_CONTENT,
                        AbsListView.LayoutParams.WRAP_CONTENT);
                itemInnerLayout.setLayoutParams(innerParams);

                itemInnerLayout.setId(ViewIdGenerator.generateViewId());

                TextView listText1 = new TextView(getContext());
                listText1.setId(ViewIdGenerator.generateViewId());
                itemInnerLayout.addView(listText1);
                listText1.setText(super.getItem(position)[1]);

                TextView listText2 = new TextView(getContext());
                listText2.setId(ViewIdGenerator.generateViewId());
                itemInnerLayout.addView(listText2);
                listText2.setText(super.getItem(position)[2]);

                ImageView imageView = new ImageView(getContext());
                imageView.setId(ViewIdGenerator.generateViewId());
                itemInnerLayout.addView(imageView);
                // show The Image
                DownloadImageTask task = new  DownloadImageTask(imageView);
                task.execute("http://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/American_Beaver.jpg/220px-American_Beaver.jpg");
                itemLayout.addView(itemInnerLayout);


                return itemLayout;
            }
        }
        listAdapter = new MyAdapter(this, values);
        listView.setAdapter(listAdapter);
        listLayout.addView(listView);
        setContentView(listLayout);
    }
}
