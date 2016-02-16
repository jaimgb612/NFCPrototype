package com.livingsoup.NFCPrototype;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.livingsoup.NFCPrototype.MainActivityTest \
 * com.livingsoup.NFCPrototype.tests/android.test.InstrumentationTestRunner
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    private TextView displayText;
    private Boolean isSuccessful;

    public MainActivityTest()
    {
        super(MainActivity.class);
        setName("NFC Prototype Test");
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        mainActivity = getActivity();
        displayText = (TextView) mainActivity.findViewById(R.id.myTextView);
        //mainActivity.onCreate(null);

    }


//    public final void testHasTextDisplay()
//    {
//        assertNotNull(displayText);
//        Resources resources = getInstrumentation().getTargetContext().getResources();
//        String text = resources.getString(R.string.ready_text);
//        assertEquals(text, displayText.getText().toString());
//    }

    public final void testInternetPermission()
    {
        Context testContext = getActivity().getApplicationContext();
        PackageManager pm = testContext.getPackageManager();

        int expected = PackageManager.PERMISSION_GRANTED;
        int actual = pm.checkPermission("android.permission.INTERNET", testContext.getPackageName());
        assertEquals(expected, actual);
    }


    public final void testCallToService() throws Throwable
    {
        NfcVo vo = new NfcVo();
        vo.product = "PRODUCT_A";
        vo.uuid = "1234567890";
        final CountDownLatch signal = new CountDownLatch(1);


        final AsyncTask<NfcVo, Void, Boolean> myTask = new AsyncTask<NfcVo, Void, Boolean>()
        {

            @Override
            protected Boolean doInBackground(NfcVo... params)
            {
                NfcVo nfcVo = params[0];
                String path = "/api/service/nfc/"+nfcVo.uuid +"/" + nfcVo.product;
                //String path = "/api/service";
                try {
                    converse("192.168.0.3", 8080, path);
                    //converse("192.168.1.161", 8080, path);
                    return true;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success)
            {
                super.onPostExecute(success);
                isSuccessful = success;
            }

            private void converse(String host, int port, String path) throws IOException
            {
                URL url = new URL("http", host, port, path);

                URLConnection conn = url.openConnection();
                conn.setDoInput(true);
                conn.setAllowUserInteraction(true);
                conn.connect();

                StringBuilder sb = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while((line = in.readLine()) != null) {
                    sb.append(line);
                }
                in.close();
            }
        };

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                myTask.execute(vo);
            }
        });

        signal.await(5, TimeUnit.SECONDS);

        assertTrue(isSuccessful);

    }





}
