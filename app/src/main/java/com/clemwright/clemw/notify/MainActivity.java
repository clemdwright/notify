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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends Activity {

    //Shared preferences
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //Check boxes
    private CheckBox remoteInputCheckBox;
    private CheckBox bridgedLikeCheckBox;
    private CheckBox bigPictureStyleCheckBox;
    private CheckBox largeIconCheckBox;
    private CheckBox vibrateCheckBox;
    private ArrayList<CheckBox> checkBoxes;
    private boolean checkBoxDefault = false;

    //Priority spinner
    private Spinner prioritySpinner;
    private ArrayAdapter<Integer> priorityAdapter;

    //Other
    private Button sendButton;
    private Boolean autoCancel = true;
    NotificationManager mNotificationManager;

    //Notification enhancements
    private NotificationCompat.BigPictureStyle bigPictureStyle;
    private NotificationCompat.Action voiceReplyAction;
    private NotificationCompat.Action likeAction;
    private Bitmap profilePhoto;

    //Constants
    private static final int DEFAULT_PRIORITY = 2;
    private static final Integer[] PRIORITIES = new Integer[]{2, 1, 0, -1, -2};
    private static final int NOTIFICATION_ID = 0;
    private static final int OPENED_REQUEST_CODE = 0;
    private static final int LIKED_REQUEST_CODE = 1;
    private static final int COMMENTED_REQUEST_CODE = 2;
    private static final String OPENED_VALUE = "Opened";
    private static final String LIKED_VALUE = "Liked";
    private static final String COMMENTED_VALUE = "Commented";

    //Listeners
    private final AdapterView.OnItemSelectedListener onItemSelectedListener =
            new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            editor.putInt(getKey(parentView), PRIORITIES[position]);
            editor.commit();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            // your code here
        }
    };
    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            editor.putBoolean(getKey(buttonView), isChecked);
            editor.commit();
        }
    };

    /*
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Shared preferences
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //Check boxes
        vibrateCheckBox = (CheckBox) findViewById(R.id.vibrateCheckBox);
        bigPictureStyleCheckBox = (CheckBox) findViewById(R.id.bigPictureStyle);
        bridgedLikeCheckBox = (CheckBox) findViewById(R.id.bridgedLike);
        remoteInputCheckBox = (CheckBox) findViewById(R.id.remoteInput);
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

        //Priority spinner
        prioritySpinner = (Spinner) findViewById(R.id.prioritySpinner);
        priorityAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, PRIORITIES);
        prioritySpinner.setAdapter(priorityAdapter);
        prioritySpinner.setOnItemSelectedListener(onItemSelectedListener);

        //Send button
        sendButton = (Button) findViewById(R.id.button);
        sendButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendNotification();
            }
        });

        //Notification enhancements
        bigPictureStyle = createBigPictureStyle();
        voiceReplyAction = createVoiceReplyAction();
        likeAction = createLikeAction();
        profilePhoto = BitmapFactory.decodeResource(getResources(), R.drawable.beyonce);

        //Notification manager
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /*
     * onStart
     */
    @Override
    protected void onStart() {
        super.onStart();

        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(sharedPreferences.getBoolean(getKey(checkBox), checkBoxDefault));
        }

        int priority = sharedPreferences.getInt(getKey(prioritySpinner), DEFAULT_PRIORITY);
        int spinnerPosition = priorityAdapter.getPosition(priority);
        prioritySpinner.setSelection(spinnerPosition);
    }

    /*
     * sendNotification
     */
    private void sendNotification() {

        //TODO: look back into the backstack code I removed. I think it is causing some nav issues.

        mNotificationManager.cancel(NOTIFICATION_ID);
        int mNotificationId = NOTIFICATION_ID;

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setContentTitle(getString(R.string.content_title))
                        .setContentText(getString(R.string.content_text))
                        .setAutoCancel(autoCancel);

        boolean largeIconChecked = sharedPreferences.getBoolean(getKey(largeIconCheckBox), checkBoxDefault);
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
        mBuilder.setContentIntent(createPendingIntent(OPENED_VALUE, OPENED_REQUEST_CODE));
        Notification mNotification = mBuilder.build();

        if (vibrateChecked) mNotification.defaults = Notification.DEFAULT_VIBRATE;

        mNotificationManager.notify(mNotificationId, mNotification);
    }

    /*
     * createBigPictureStyle
     */
    private NotificationCompat.BigPictureStyle createBigPictureStyle() {
        NotificationCompat.BigPictureStyle bigPictureStyle =
                new NotificationCompat.BigPictureStyle();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.family);
        bigPictureStyle.bigPicture(bitmap);
        bigPictureStyle.setSummaryText(getString(R.string.content_text));
        return bigPictureStyle;
    }

    /*
     * createVoiceReplyAction
     */
    private NotificationCompat.Action createVoiceReplyAction() {

        PendingIntent replyPendingIntent = createPendingIntent(COMMENTED_VALUE, COMMENTED_REQUEST_CODE);

        String replyLabel = getResources().getString(R.string.reply_label);
        String[] replyChoices = getResources().getStringArray(R.array.reply_choices);

        RemoteInput remoteInput = new RemoteInput.Builder(Utils.EXTRA_VOICE_REPLY)
                .setLabel(replyLabel)
                .setChoices(replyChoices)
                .build();

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_action_chat, replyLabel, replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        return action;

    }

    /*
     * createPendingIntent
     */
    private PendingIntent createPendingIntent(String extraValue, int requestCode) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(Utils.EXTRA_NAME, extraValue);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    /*
     * createLikeAction
     */
    private NotificationCompat.Action createLikeAction() {
        PendingIntent likePendingIntent = createPendingIntent(LIKED_VALUE, LIKED_REQUEST_CODE);
        NotificationCompat.Action likeAction =
                new NotificationCompat.Action(R.drawable.ic_action_like,
                        getString(R.string.like_label), likePendingIntent);
        return likeAction;
    }

    /*
     * getKey
     */
    private String getKey(View view) {
        return String.valueOf(view.getId());
    }
}
