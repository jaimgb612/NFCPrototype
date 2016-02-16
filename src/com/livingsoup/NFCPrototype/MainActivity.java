package com.livingsoup.NFCPrototype;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
    public static final String TAG = MainActivity.class.getName();
    public static final String MIME_TEXT_PLAIN = "text/plain";

    private NfcAdapter nfcAdapter;
    private TextView displayText;
    private TextView uuidText;
    private TextView selectTextView;
    private RadioButton radioButtonA;
    private RadioButton radioButtonB;
    private RadioButton radioButtonC;
    private String selectedProduct;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        displayText = (TextView) findViewById(R.id.myTextView);
        uuidText = (TextView) findViewById(R.id.uuidText);
        selectTextView = (TextView) findViewById(R.id.selectTextView);

        radioButtonA = (RadioButton)findViewById(R.id.ra_product_A);
        radioButtonB = (RadioButton)findViewById(R.id.ra_product_B);
        radioButtonC = (RadioButton)findViewById(R.id.ra_product_C);

        radioButtonA.setOnClickListener(radioButtonListener);
        radioButtonB.setOnClickListener(radioButtonListener);
        radioButtonC.setOnClickListener(radioButtonListener);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null)
        {
            Toast.makeText(this, "this device does not support NFC", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled())
        {
            displayText.setText("NFC is disabled");
            displayText.setTextColor(Color.parseColor("#f50707"));
        }
        else
        {
            displayText.setText(R.string.ready_text);
            displayText.setTextColor(Color.parseColor("#07b907"));
        }


        handleIntent(getIntent());
    }


    private RadioGroup.OnClickListener radioButtonListener = new RadioGroup.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {

            if (v == radioButtonA)
            {
                setSelectedProduct("PRODUCT_A");
            }
            else if (v == radioButtonB)
            {
                setSelectedProduct("PRODUCT_B");
            }
            else if (v == radioButtonC)
            {
                setSelectedProduct("PRODUCT_C");
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (NfcAdapter.getDefaultAdapter(this)!= null)
        {
            NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent)
    {
        String action = intent.getAction();
        Log.i(TAG, action);


        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action))
        {

            if (selectedProduct == null)
            {
                selectTextView.setTextColor(Color.parseColor("#f50707"));
                return;
            }

            Log.i(TAG, "ACTION_TAG_DISCOVERED");
            byte[] byte_id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String uid = "";

            for (int i = 0; i < byte_id.length; i++)
            {
                uid += byte_id[i];
            }

            Log.i(TAG, "ID: " +uid);
            Tag tag = (Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Log.i(TAG, "tag: " + tag);

            Resources resources = getBaseContext().getResources();
            String text = resources.getString(R.string.output_text);

            uuidText.setText(text + uid);
            sendToWebService(uid);
        }
    }

    private void sendToWebService(String uuid)
    {

        NfcVo nfcVo = new NfcVo();
        nfcVo.product = getSelectedProduct();
        nfcVo.uuid = uuid;
        CallRestURL callRestURL = new CallRestURL();
        callRestURL.execute(nfcVo);
    }


    public String getSelectedProduct()
    {
        return selectedProduct;
    }

    public void setSelectedProduct(String selectedProduct)
    {
        this.selectedProduct = selectedProduct;
        Log.i(TAG, selectedProduct);
    }
}
