package com.gpt.dynlayout;

import android.support.test.espresso.base.MainThread;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by luborkolacny on 12/05/15.
 */
public class AnotherTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mFirstTestActivity;
    private TextView mFirstTestText;
    private View mRootView;

    public AnotherTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mFirstTestActivity = getActivity();
        mRootView = mFirstTestActivity.getWindow().getDecorView().getRootView();
    }

    public void testPreconditions() {
        assertNotNull("mFirstTestActivity is null", mFirstTestActivity);
        assertNotNull("mRootView is null", mRootView);
    }

    public void testButtons() throws Throwable {
        final Button downloadButton = (Button)mRootView.findViewWithTag("Download");
        assertNotNull("downloadButton is null", downloadButton);
        String title = downloadButton.getText().toString();
        assertEquals("Title is not Download", title, "Download");
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                downloadButton.performClick();
            }
        });
        Thread.sleep(2000);
    }
}