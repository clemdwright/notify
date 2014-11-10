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

    //Send button
    private Button sendButton;

    //Notification enhancements
    private NotificationCompat.BigPictureStyle bigPictureStyle;
    private NotificationCompat.Action voiceReplyAction;
    private NotificationCompat.Action likeAction;

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
     * createBigPictureStyle
     */
    private NotificationCompat.BigPictureStyle createBigPictureStyle() {
        NotificationCompat.BigPictureStyle bigPictureStyle =
                new NotificationCompat.BigPictureStyle();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lou);
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

        // Create the reply action and add the remote input
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_action_chat, replyLabel, replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        return action;

    }

    private PendingIntent createPendingIntent(String extraValue, int requestCode) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(Utils.EXTRA_NAME, extraValue);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private NotificationCompat.Action createLikeAction() {
        PendingIntent likePendingIntent = createPendingIntent(LIKED_VALUE, LIKED_REQUEST_CODE);
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


        //TODO: abstract this out and use create pending intent code i just wrote. will need to pass activity as a param

        //TODO: see if i need this back stack stuff... if so add it to creatependingintent method


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
