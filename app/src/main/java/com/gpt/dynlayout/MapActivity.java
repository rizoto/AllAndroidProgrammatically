package com.gpt.dynlayout;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.SupportMapFragment;


public class MapActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //RelativeLayout relativeLayout = new RelativeLayout(this);
        if(savedInstanceState == null) {
            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content,mapFragment).commit();
        }
    }

}
