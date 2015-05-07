package com.gpt.dynlayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creating Layout
        RelativeLayout relativeLayout = new RelativeLayout(this);
        LinearLayout listLayout = new LinearLayout(this);
        TextView item = new TextView(this);

        ListView listView = new ListView(this);

        String[] values = new String[10];
        for(int i=0;i<10;i++){
            values[i] = ""+i;
        }
        class MyAdapter extends ArrayAdapter<String> {

            public MyAdapter(Context context, String[] strings) {
                super(context, -1, -1, strings);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                LinearLayout listLayout = new LinearLayout(getContext());
                listLayout.setLayoutParams(new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.WRAP_CONTENT,
                        AbsListView.LayoutParams.WRAP_CONTENT));
                listLayout.setId(ViewIdGenerator.generateViewId());

                TextView listText = new TextView(getContext());
                listText.setId(ViewIdGenerator.generateViewId());

                listLayout.addView(listText);

                listText.setText(super.getItem(position));

                return listLayout;
            }
        }
        listView.setAdapter(new MyAdapter(this,values));
        listLayout.addView(listView);
        setContentView(listLayout);
    }
}
