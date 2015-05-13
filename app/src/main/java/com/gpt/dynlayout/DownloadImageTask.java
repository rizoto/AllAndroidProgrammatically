package com.gpt.dynlayout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lkolacny on 9/05/15.
 */
class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    InputStream is = null;
    LruCache <String,Bitmap> mCache;
    private static HashMap<String,Boolean> mLoadingQueue;
    public DownloadImageTask(ImageView bmImage, LruCache cache) {
        this.bmImage = bmImage;
        mCache = cache;
        mLoadingQueue = new HashMap<>();
    }

    protected Bitmap onePixel() {
        int w = 1, h = 1;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap tempOnePixel = Bitmap.createBitmap(w, h, conf);
        return tempOnePixel;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap bitmap = null;
        if (mLoadingQueue.containsKey(urldisplay)) {
            Log.d("Loading Again",urldisplay);
            return null;
        }
        try {
            mLoadingQueue.put(urldisplay, true);
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
            if (response == 200) {
                is = conn.getInputStream();
//                    InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(is);
                if(bitmap!=null && mCache != null) {
                    mCache.put(urldisplay, bitmap);
                } else if (mCache != null) {
                    mCache.put(urldisplay, onePixel());
                }
            } else {
                // add to cache empty image to avoid another failed calls
                mCache.put(urldisplay, onePixel());
            }
            conn.disconnect();
        } catch (Exception e) {
            Log.e("Error", " " + e.getMessage()); // e.get.. can be null and it can crash ;( so +" " make Andy happy
            if (mCache != null) {
                Bitmap b = onePixel();
                mCache.put(urldisplay, b);
            }
        } finally {
            mLoadingQueue.put(urldisplay,false);
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e("Error", " " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
        if(result != null) {
            bmImage.setImageBitmap(result);
            // ((FeedAdapter)bmImage.getContext()).notifyDataSetChanged();
        }
    }
}