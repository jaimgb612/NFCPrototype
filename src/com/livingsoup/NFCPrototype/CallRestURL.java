package com.livingsoup.NFCPrototype;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by russellmilburn on 30/06/15.
 */
public class CallRestURL extends AsyncTask<NfcVo, Void, Boolean>
{
    public static final String TAG = CallRestURL.class.getName();
    private NfcVo nfcVo;

    @Override
    protected Boolean doInBackground(NfcVo... nfcVos)
    {
        this.nfcVo = nfcVos[0];
        String path = "/api/service/nfc/"+nfcVo.uuid +"/" + nfcVo.product;
        try {
            converse("192.168.0.3", 8080, path);
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
        Log.i(TAG, success.toString());

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
        while((line = in.readLine()) != null)
        {
            sb.append(line);
        }
        in.close();



    }

}
