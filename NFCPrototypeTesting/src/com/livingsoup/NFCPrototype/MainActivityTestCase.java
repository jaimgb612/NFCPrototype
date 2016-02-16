package com.livingsoup.NFCPrototype;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by russellmilburn on 01/07/15.
 */
public class MainActivityTestCase extends ActivityUnitTestCase<MainActivity>
{
    private Intent startIntent;
    private TextView displayText;

    public MainActivityTestCase()
    {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        startIntent = new Intent(Intent.ACTION_PACKAGE_FIRST_LAUNCH);
    }

    @MediumTest
    public void testPreconditions()
    {
        startActivity(startIntent, null, null);
        displayText = (TextView) getActivity().findViewById(R.id.myTextView);
        assertNotNull(displayText);

    }

    public void testActivityLifecycle()
    {
        MainActivity activity = startActivity(startIntent, null, null);
        assertNotNull(activity);

        assertNotNull(activity.findViewById(R.id.myTextView));

        displayText = (TextView) getActivity().findViewById(R.id.myTextView);


        assertNotNull(displayText.getText());

        getInstrumentation().callActivityOnCreate(activity,null);
        getInstrumentation().callActivityOnStart(activity);

        assertEquals(displayText.getText(), "NFC Ready to Go");

    }




}
