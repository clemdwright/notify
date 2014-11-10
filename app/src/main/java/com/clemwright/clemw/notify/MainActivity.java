package com.clemwright.clemw.notify;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private SharedPreferences sharedPreferences;
    private Button sendButton;
    private Spinner prioritySpinner;
    private CheckBox remoteInputCheckBox;
    private CheckBox bridgedLikeCheckBox;
    private CheckBox bigPictureStyleCheckBox;
    private CheckBox largeIconCheckBox;
    private CheckBox vibrateCheckBox;
    private boolean checkBoxDefault = false;
    private boolean largeIconDefault = false;
    SharedPreferences.Editor editor;
    private ArrayList<CheckBox> checkBoxes;
    private ArrayAdapter<Integer> priorityAdapter;
    private NotificationCompat.BigPictureStyle bigPictureStyle;
    private NotificationCompat.Action voiceReplyAction;
    private NotificationCompat.Action likeAction;


    private static final int DEFAULT_PRIORITY = 2;
    private static final Integer[] priorities = new Integer[]{2, 1, 0, -1, -2};
    private static final int NOTIFICATION_ID = 0;
    private static final int OPENED_REQUEST_CODE = 0;
    private static final int LIKED_REQUEST_CODE = 1;
    private static final int COMMENTED_REQUEST_CODE = 2;

    // Key for the string that's delivered in the action's intent
    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendButton = (Button) findViewById(R.id.button);
        sendButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendNotification();
            }
        });

        prioritySpinner = (Spinner) findViewById(R.id.prioritySpinner);
        priorityAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, priorities);

        prioritySpinner.setOnItemSelectedListener(onItemSelectedListener);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        bigPictureStyle = createBigPictureStyle();
        voiceReplyAction = createVoiceReplyAction();
        likeAction = createLikeAction();

        vibrateCheckBox = (CheckBox) findViewById(R.id.vibrateCheckBox);
        remoteInputCheckBox = (CheckBox) findViewById(R.id.remoteInput);
        bridgedLikeCheckBox = (CheckBox) findViewById(R.id.bridgedLike);
        bigPictureStyleCheckBox = (CheckBox) findViewById(R.id.bigPictureStyle);
        largeIconCheckBox = (CheckBox) findViewById(R.id.largeIcon);

        checkBoxes = new ArrayList<CheckBox>();
        checkBoxes.add(remoteInputCheckBox);
        checkBoxes.add(bridgedLikeCheckBox);
        checkBoxes.add(bigPictureStyleCheckBox);
        checkBoxes.add(largeIconCheckBox);
        checkBoxes.add(vibrateCheckBox);

        for (CheckBox checkBox : checkBoxes) {
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
        }
    }

    final AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            editor.putInt(getKey(parentView), priorities[position]);
            editor.commit();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            // your code here
        }
    };


    //Global On click listener for all views
    final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            editor.putBoolean(getKey(buttonView), isChecked);
            editor.commit();
        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(sharedPreferences.getBoolean(getKey(checkBox), checkBoxDefault));
        }

        prioritySpinner.setAdapter(priorityAdapter);
        int priority = sharedPreferences.getInt(getKey(prioritySpinner), DEFAULT_PRIORITY);
        int spinnerPosition = priorityAdapter.getPosition(priority);
        prioritySpinner.setSelection(spinnerPosition);


//
//        boolean vibrateChecked = sharedPreferences.getBoolean(getKey(vibrateCheckBox), checkBoxDefault);
//        vibrateCheckBox.setChecked(vibrateChecked);
//
//        boolean largeIconChecked = sharedPreferences.getBoolean(getKey(largeIconCheckBox), largeIconDefault);
//        largeIconCheckBox.setChecked(largeIconChecked);
    }


    private NotificationCompat.BigPictureStyle createBigPictureStyle() {
        NotificationCompat.BigPictureStyle bigPictureStyle =
                new NotificationCompat.BigPictureStyle();
        // Create bitmap from resource
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lou);
        // Add the bitmap to the big picture style
        bigPictureStyle.bigPicture(bitmap);
        // Sets a summary for the big picture view
        bigPictureStyle.setSummaryText(getString(R.string.content_text));
        return bigPictureStyle;
    }

    private NotificationCompat.Action createVoiceReplyAction() {

        String replyLabel = getResources().getString(R.string.reply_label);
        String[] replyChoices = getResources().getStringArray(R.array.reply_choices);

        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(replyLabel)
                .setChoices(replyChoices)
                .build();

        // Create an intent for the reply action
        Intent replyIntent = new Intent(this, ResultActivity.class);

        replyIntent.putExtra("IntentType", "Commented");

        PendingIntent replyPendingIntent =
                PendingIntent.getActivity(this, COMMENTED_REQUEST_CODE, replyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the reply action and add the remote input
        NotificationCompat.Action action =
                new NotificationCompat.Action(R.drawable.ic_action_chat,
                        getString(R.string.reply_label), replyPendingIntent);

        NotificationCompat.Action.Builder actionBuilder =
                new NotificationCompat.Action.Builder(action);

        actionBuilder.addRemoteInput(remoteInput);
        NotificationCompat.Action newAction = actionBuilder.build();

        return newAction;

    }

    private NotificationCompat.Action createLikeAction() {
        // Create an intent for the like action
        Intent likeIntent = new Intent(this, ResultActivity.class);

        likeIntent.putExtra("IntentType", "Liked");

        PendingIntent likePendingIntent =
                PendingIntent.getActivity(this, LIKED_REQUEST_CODE, likeIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        //Creates a like action that can be added to either wearable extender or normal notification
        NotificationCompat.Action likeAction =
                new NotificationCompat.Action(R.drawable.ic_action_like,
                        getString(R.string.like_label), likePendingIntent);

        return likeAction;

    }

    /*
     * Sends a notification.
     * Adapted from https://developer.android.com/training/wearables/notifications/creating.html
     */
    private void sendNotification() {

        // Allows you to update the notification later on.
        int mNotificationId = NOTIFICATION_ID;

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ResultActivity.class);

        resultIntent.putExtra("IntentType", "Opened");

        /*
         * The stack builder object will contain an artificial back stack for the
         * started Activity. This ensures that navigating backward from the Activity
         * leads out of your application to the Home screen.
         */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ResultActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        OPENED_REQUEST_CODE,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // Create bitmap from resource
        Bitmap profilePhoto = BitmapFactory.decodeResource(getResources(), R.drawable.nico);

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setContentTitle(getString(R.string.content_title))
                        .setContentText(getString(R.string.content_text));

        boolean largeIconChecked = sharedPreferences.getBoolean(getKey(largeIconCheckBox), largeIconDefault);
        boolean bridgedLikeChecked = sharedPreferences.getBoolean(getKey(bridgedLikeCheckBox), checkBoxDefault);
        boolean bigPictureStyleChecked = sharedPreferences.getBoolean(getKey(bigPictureStyleCheckBox), checkBoxDefault);
        boolean remoteInputChecked = sharedPreferences.getBoolean(getKey(remoteInputCheckBox), checkBoxDefault);
        boolean vibrateChecked = sharedPreferences.getBoolean(getKey(vibrateCheckBox), checkBoxDefault);
        int priority = sharedPreferences.getInt(getKey(prioritySpinner), DEFAULT_PRIORITY);

        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();

        if (largeIconChecked) mBuilder.setLargeIcon(profilePhoto);
        if (bridgedLikeChecked) mBuilder.addAction(likeAction);
        if (bigPictureStyleChecked) mBuilder.setStyle(bigPictureStyle);
        if (bridgedLikeChecked && remoteInputChecked) wearableExtender.addAction(likeAction);
        if (remoteInputChecked) {
            wearableExtender.addAction(voiceReplyAction);
            mBuilder.extend(wearableExtender);
        }

        mBuilder.setPriority(priority);
        mBuilder.extend(wearableExtender);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification mNotification = mBuilder.build();

        if (vibrateChecked) mNotification.defaults = Notification.DEFAULT_VIBRATE;

        mNotificationManager.notify(mNotificationId, mNotification);
    }

    //Takes a view and returns its view ID as a String
    private String getKey(View view) {
        return String.valueOf(view.getId());
    }
}
