package com.clemwright.clemw.notify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.widget.TextView;
import android.widget.Toast;


public class ResultActivity extends Activity {

    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        textView = (TextView) findViewById(R.id.reply);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        if (intent != null) {

            String toastString = intent.getStringExtra("IntentType");

            Toast.makeText(getApplicationContext(), toastString,
                    Toast.LENGTH_SHORT).show();

            textView.setText(getMessageText(intent));
        }
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(EXTRA_VOICE_REPLY);
        }
        return null;
    }

}
