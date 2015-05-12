package com.gpt.dynlayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.res.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lkolacny on 9/05/15.
 */
class FeedAdapter extends ArrayAdapter<String[]> {
    int itemLayoutId = ViewIdGenerator.generateViewId();
    int listTextId = ViewIdGenerator.generateViewId();
    int itemInnerLayoutId = ViewIdGenerator.generateViewId();
    int listText1Id = ViewIdGenerator.generateViewId();
    int imageViewId = ViewIdGenerator.generateViewId();
    private LruCache<String, Bitmap> mCache;
    public FeedAdapter(Context context, LruCache cache,ArrayList<String[]> strings) {
        super(context, -1, -1, strings);
        mCache = cache;
    }

    public void loadBitmap(String urlKey, ImageView imageView) {
        if(urlKey == null || urlKey == "" || urlKey.isEmpty() || urlKey == "null")
        {
            // wrong urlKey
            return;
        }
        final Bitmap bitmap = mCache.get(urlKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            DownloadImageTask task = new  DownloadImageTask(imageView,mCache);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, urlKey);
            else
                task.execute(urlKey);

        }
    }

    public void parseJson (JSONObject json) {
        try {
            clear();
            JSONArray array = json.getJSONArray("rows");
            for (int j = 0; j < 10; j++) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.getJSONObject(i);
                    String[] s = new String[3];
                    s[0] = item.getString("title");
                    s[1] = item.getString("description");
                    s[2] = item.getString("imageHref");
                    if (!(s[0] == "null" && s[1] == "null" && s[2] == "null")) {
                        // at least one of them is not null
                        if(s[0].equals("null"))
                        {
                            s[0] = "";
                        }
                        if(s[1].equals("null"))
                        {
                            s[1] = "";
                        }
                        if(s[2].equals("null"))
                        {
                            s[2] = null;
                        }
                        add(s);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        if(convertView != null) {
//            TextView titleTextView = (TextView)convertView.findViewById(listTextId);
//            titleTextView.setText(super.getItem(position)[0]);
////            TextView listText1 = (TextView)convertView.findViewById(listText1Id);
////            listText1.setText(super.getItem(position)[1]);
//            // show The Image
//            ImageView imageView = (ImageView)convertView.findViewById(imageViewId);
//            // show The Image
//            String tempUrlKey = super.getItem(position)[2];
//            if (tempUrlKey != null && !tempUrlKey.isEmpty()) {
//                Bitmap tempBitmap = mCache.get(super.getItem(position)[2]);
//                if (tempBitmap != null) {
//                    imageView.setImageBitmap(tempBitmap);
//                } else {
//                    loadBitmap(super.getItem(position)[2], imageView);
//                }
//            }
//            return convertView;
//        }

        RelativeLayout itemLayout = new RelativeLayout(getContext());
        AbsListView.LayoutParams relativeParams = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        itemLayout.setLayoutParams(relativeParams);
        itemLayout.setId(itemLayoutId);

        // Title Text View
        TextView titleTextView = new TextView(getContext());
        titleTextView.setId(listTextId);
        RelativeLayout.LayoutParams relParamTitle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleTextView.setText(super.getItem(position)[0]);
        titleTextView.setTypeface(null, Typeface.BOLD_ITALIC);
        titleTextView.setTextColor(Color.BLUE);
        itemLayout.addView(titleTextView, relParamTitle);


        LinearLayout itemInnerLayout = new LinearLayout(getContext());
        itemInnerLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,4f);

        itemInnerLayout.setLayoutParams(innerParams);
        itemInnerLayout.setId(itemInnerLayoutId);


        TextView listText1 = new TextView(getContext());
        listText1.setId(listText1Id);
        LinearLayout.LayoutParams listText1Params = new LinearLayout.LayoutParams(
                0,
                AbsListView.LayoutParams.WRAP_CONTENT);
        listText1Params.weight = 3f;
        listText1.setLayoutParams(listText1Params);
        itemInnerLayout.addView(listText1);
        listText1.setText(super.getItem(position)[1]);

        ImageView imageView = new ImageView(getContext());
        imageView.setId(imageViewId);
        LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(
                0,
                AbsListView.LayoutParams.WRAP_CONTENT);
        imageViewParams.weight = 1f;
        imageViewParams.gravity = Gravity.TOP;
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(imageViewParams);
        itemInnerLayout.addView(imageView);
        // show The Image
        String tempUrlKey = super.getItem(position)[2];
        if (tempUrlKey != null && !tempUrlKey.isEmpty()) {
            Bitmap tempBitmap = mCache.get(super.getItem(position)[2]);
            if (tempBitmap != null) {
                imageView.setImageBitmap(tempBitmap);
            } else {
                loadBitmap(super.getItem(position)[2], imageView);
            }
        }
        RelativeLayout.LayoutParams itemInnerRelLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        itemInnerRelLayoutParams.addRule(RelativeLayout.BELOW,listTextId);
        itemLayout.addView(itemInnerLayout,itemInnerRelLayoutParams);
        return itemLayout;
    }
}