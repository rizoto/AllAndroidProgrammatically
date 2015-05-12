package com.gpt.dynlayout;

import android.app.Activity;
import android.os.SystemClock;
import android.support.test.espresso.base.MainThread;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.startsWith;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActivityClickTest {

    /**
     * A JUnit {@link Rule @Rule} to launch your activity under test. This is a replacement
     * for {@link ActivityInstrumentationTestCase2}.
     * <p>
     * Rules are interceptors which are executed for each test method and will run before
     * any of your setup code in the {@link Before @Before} method.
     * <p>
     * {@link ActivityTestRule} will create and launch of the activity for you and also expose
     * the activity under test. To get a reference to the activity you can use
     * the {@link ActivityTestRule#getActivity()} method.
     */
    @SuppressWarnings("deprecation")
    public ActivityClickTest() {
        // This constructor was deprecated - but we want to support lower API levels.
        super();
    }
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void click_sameActivity() throws Throwable{
        //onView(withTagValue(equalTo((Object)"TextView")));
        View textView = mActivityRule.getActivity().getWindow().getDecorView().getRootView().findViewWithTag("TextView");
        if (textView == null) throw new AssertionError();

        onView(withText("TextView")).check(matches(isDisplayed()));
        onView(withText("Download")).perform(click());
        Thread.sleep(2000);
        onView(withTagValue(is((Object)"TextView"))).check(matches(withText(containsString("Google"))));
    }

    @Test
    public void click_listActivity() throws Throwable{
        onView(withTagValue(is((Object) "List"))).check(matches(isDisplayed()));
        //onView(withTagValue(is((Object) "List"))).perform(click());
        //Thread.sleep(2000);
        //onView(withTagValue(is((Object)"TextView"))).check(matches(withText(containsString("Google"))));
    }
}