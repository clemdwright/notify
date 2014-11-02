package com.clemwright.clemw.notify;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.widget.RadioGroup;
import android.widget.Spinner;

public class MainActivity extends Activity {

    private Button send;
    private RadioGroup style;
    private Spinner priority;
    private CheckBox lights;
    private CheckBox vibrate;
    private CheckBox sound;
    private CheckBox autocancel;

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

        style = (RadioGroup) findViewById(R.id.bigStyle);

        priority = (Spinner) findViewById(R.id.priority);
        Integer[] priorities = new Integer[] {-2,-1,0,1,2};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, priorities);

        priority.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(0);
        priority.setSelection(spinnerPosition);

        lights = (CheckBox) findViewById(R.id.lights);
        vibrate = (CheckBox) findViewById(R.id.vibrate);
        sound = (CheckBox) findViewById(R.id.sound);
        autocancel = (CheckBox) findViewById(R.id.autocancel);

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

    private NotificationCompat.BigTextStyle createBigTextStyle() {
        NotificationCompat.BigTextStyle bigTextStyle =
                new NotificationCompat.BigTextStyle();
        // Sets a long string for the text
        bigTextStyle.bigText(getString(R.string.long_text));

        return bigTextStyle;
    }

    private NotificationCompat.InboxStyle createInboxStyle(NotificationCompat.Builder builder) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = new String[6];

        events[0] = "Jeff Goldblum";
        events[1] = "Chaka Khan";
        events[2] = "Nicki Minaj";
        events[3] = "Weird Al";
        events[4] = "Nessie";
        events[5] = "Joe Biden";

        // Sets summary text for the Inbox style big view.
        inboxStyle.setSummaryText("6 friends tagged you in photos.");

        builder.setContentText("6 friends tagged you in photos.");

        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        // Note: you need to drag to expand this info on phone;
        // On watch, it comes already expanded
        return inboxStyle;
    }


//    private NotificationCompat.WearableExtender wearableExtender =
//            new NotificationCompat.WearableExtender()
//                    .addAction(newAction);





    private  NotificationCompat.Action createVoiceReplyAction() {

        String replyLabel = getResources().getString(R.string.reply_label);
        String[] replyChoices = getResources().getStringArray(R.array.reply_choices);

        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(replyLabel)
                .setChoices(replyChoices)
                .build();

        // Create an intent for the reply action
        Intent replyIntent = new Intent(this, ReplyActivity.class);
        PendingIntent replyPendingIntent =
                PendingIntent.getActivity(this, 0, replyIntent,
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

        /*
         * You specify the UI information and actions for a notification in a
         * NotificationCompat.Builder object.
         */
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(getString(R.string.content_title))
                .setContentText(getString(R.string.content_text))
                .setPriority((Integer) priority.getSelectedItem())

                //Adding actions for testing
                .addAction(R.drawable.ic_notification_icon, "Like", resultPendingIntent)
                .addAction(createVoiceReplyAction())
                .setAutoCancel(autocancel.isChecked());

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int selectedBigStyle = style.getCheckedRadioButtonId();

        switch (selectedBigStyle) {
            case R.id.bigPictureStyle: mBuilder.setStyle(createBigPictureStyle());
                break;
            case R.id.bigTextStyle: mBuilder.setStyle(createBigTextStyle());
                break;
            case R.id.inboxStyle: mBuilder.setStyle(createInboxStyle(mBuilder));
            default:
                break;
        }

        /*
         * To create the notification itself, you call NotificationCompat.Builder.build(),
         * which returns a Notification object containing your specifications.
         */
        Notification mNotification = mBuilder.build();

        if (sound.isChecked()) mNotification.defaults = Notification.DEFAULT_SOUND;
        if (lights.isChecked()) mNotification.defaults = Notification.DEFAULT_LIGHTS;
        if (vibrate.isChecked()) mNotification.defaults = Notification.DEFAULT_VIBRATE;

        /*
         * To issue the notification, you pass the Notification object to the system by
         * calling NotificationManager.notify()
         */
        mNotificationManager.notify(mNotificationId, mNotification);
    }
}
