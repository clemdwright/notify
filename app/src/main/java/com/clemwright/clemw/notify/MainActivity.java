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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

public class MainActivity extends Activity {

    private Button send;
//    private RadioGroup style;
    private Spinner priority;
//    private CheckBox lights;
    private CheckBox vibrate;
//    private CheckBox sound;
//    private CheckBox autocancel;
    private CheckBox remoteInput;
//    private CheckBox wearableLike;
    private CheckBox bridgedLike;
    private CheckBox bigPictureStyle;
//    private CheckBox backgroundImage;
    private CheckBox largeIcon;
    private SharedPreferences sharedPreferences;
    private boolean vibrateDefault = true;
    SharedPreferences.Editor editor;

    // Key for the string that's delivered in the action's intent
    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = (Button) findViewById(R.id.button);
        send.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendNotification();
            }
        });

//        style = (RadioGroup) findViewById(R.id.bigStyle);

        priority = (Spinner) findViewById(R.id.priority);
        Integer[] priorities = new Integer[] {2,1,0,-1,-2};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, priorities);

        priority.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(0);
        priority.setSelection(spinnerPosition);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

//        lights = (CheckBox) findViewById(R.id.lights);
        vibrate = (CheckBox) findViewById(R.id.vibrate);
//        sound = (CheckBox) findViewById(R.id.sound);
//        autocancel = (CheckBox) findViewById(R.id.autocancel);
        remoteInput = (CheckBox) findViewById(R.id.remoteInput);
//        wearableLike = (CheckBox) findViewById(R.id.wearableLike);
        bridgedLike = (CheckBox) findViewById(R.id.bridgedLike);
        bigPictureStyle = (CheckBox) findViewById(R.id.bigPictureStyle);
//        backgroundImage = (CheckBox) findViewById(R.id.backgroundImage);
        largeIcon = (CheckBox) findViewById(R.id.largeIcon);

        vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    editor.putBoolean(getString(R.string.vibrate), isChecked);
                    editor.commit();

//                    Toast.makeText(getApplicationContext(), (String.valueOf(isChecked)),
//                            Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean vibrateChecked = sharedPreferences.getBoolean(getString(R.string.vibrate), vibrateDefault);

        vibrate.setChecked(vibrateChecked);
//        if (vibrateChecked) mNotification.defaults = Notification.DEFAULT_VIBRATE;
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

    private  NotificationCompat.Action createVoiceReplyAction() {

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
                PendingIntent.getActivity(this, 2, replyIntent,
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

    /*
     * Sends a notification.
     * Adapted from https://developer.android.com/training/wearables/notifications/creating.html
     */
    private void sendNotification() {

        // Allows you to update the notification later on.
        int mNotificationId = 1;

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
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        // Create an intent for the reply action
        Intent likeIntent = new Intent(this, ResultActivity.class);

        likeIntent.putExtra("IntentType", "Liked");

        PendingIntent likePendingIntent =
                PendingIntent.getActivity(this, 1, likeIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        //Creates a like action that can be added to either wearable extender or normal notification
        NotificationCompat.Action likeAction =
                new NotificationCompat.Action(R.drawable.ic_action_like,
                        getString(R.string.like_label), likePendingIntent);


        // Create bitmap from resource
        Bitmap profilePhoto = BitmapFactory.decodeResource(getResources(), R.drawable.nico);


        /*
         * You specify the UI information and actions for a notification in a
         * NotificationCompat.Builder object.
         */
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(getString(R.string.content_title))
                .setContentText(getString(R.string.content_text))

//                        .setAutoCancel(autocancel.isChecked())
//                        .setPriority((Integer) priority.getSelectedItem())
                .setPriority(2);

        if (largeIcon.isChecked()) {
            mBuilder.setLargeIcon(profilePhoto);
        }

        if (bridgedLike.isChecked()) {
            mBuilder.addAction(likeAction);
        }

        if (bigPictureStyle.isChecked()) {
            mBuilder.setStyle(createBigPictureStyle());
        }

        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender();

        // If selected, adds the like action to the wearable extender
//        if (wearableLike.isChecked()) {
//            wearableExtender.addAction(likeAction);
//        }

        // If selected, set background image
//        if (backgroundImage.isChecked()) {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lou);
//            wearableExtender.setBackground(bitmap);
//        }

        if (bridgedLike.isChecked() && remoteInput.isChecked()) {
            wearableExtender.addAction(likeAction);
        }

        // If selected, adds the voice reply action to the wearable extender
        if (remoteInput.isChecked()) {
            wearableExtender.addAction(createVoiceReplyAction());
        }


        if (remoteInput.isChecked()) {
            mBuilder.extend(wearableExtender);
        }

        mBuilder.extend(wearableExtender);


        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        int selectedBigStyle = style.getCheckedRadioButtonId();
//
//        switch (selectedBigStyle) {
//            case R.id.bigPictureStyle: mBuilder.setStyle(createBigPictureStyle());
//                break;
//            case R.id.bigTextStyle: mBuilder.setStyle(createBigTextStyle());
//                break;
//            case R.id.inboxStyle: mBuilder.setStyle(createInboxStyle(mBuilder));
//            default:
//                break;
//        }

        /*
         * To create the notification itself, you call NotificationCompat.Builder.build(),
         * which returns a Notification object containing your specifications.
         */
        Notification mNotification = mBuilder.build();

//        if (sound.isChecked()) mNotification.defaults = Notification.DEFAULT_SOUND;
//        if (lights.isChecked()) mNotification.defaults = Notification.DEFAULT_LIGHTS;

        //        int defaultValue = getResources().getInteger(R.string.saved_high_score_default);
//        long highScore = sharedPref.getInt(getString(R.string.saved_high_score), defaultValue);

        //TODO: set default value with a variable instead of "false"

        boolean vibrateChecked = sharedPreferences.getBoolean(getString(R.string.vibrate), vibrateDefault);

        if (vibrateChecked) mNotification.defaults = Notification.DEFAULT_VIBRATE;

        /*
         * To issue the notification, you pass the Notification object to the system by
         * calling NotificationManager.notify()
         */
        mNotificationManager.notify(mNotificationId, mNotification);
    }
}
