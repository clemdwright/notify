package com.example.clemw.notify;

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
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;


public class MainActivity extends Activity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendNotification(getCurrentMinute());
            }
        });
    }

    public int getCurrentMinute() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }

    //https://developer.android.com/training/wearables/notifications/creating.html
    private void sendNotification(int minute) {

        // You specify the UI information and actions for a notification in a NotificationCompat.Builder object.
        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        //Sets the small icon
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        //Sets the title
                        .setContentTitle("Current time")
                        //Sets the text
                        .setContentText("It is " + minute + " past.")
                        //Set the priority to the max, making it more likely notification will be at top and will be expanded by default
                        .setPriority(2)
                        //Removes notification when it is opened on phone
                        .setAutoCancel(true);

        // Moves the big view style object into the notification
//        mBuilder.setStyle(createBigTextStyle());
//        mBuilder.setStyle(createInboxStyle());
        mBuilder.setStyle(createBigPictureStyle());

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ResultActivity.class);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
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
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



        // To create the notification itself, you call NotificationCompat.Builder.build(), which returns a Notification object containing your specifications.
        Notification notification = mBuilder.build();

        // Set sound, lights, and vibrate to default
        notification.defaults = Notification.DEFAULT_ALL;

        // mId allows you to update the notification later on.
        int mId = 001;

        // To issue the notification, you pass the Notification object to the system by calling NotificationManager.notify()
        mNotificationManager.notify(mId, notification);
    }

    private NotificationCompat.BigTextStyle createBigTextStyle() {
        NotificationCompat.BigTextStyle bigTextStyle =
                new NotificationCompat.BigTextStyle();

        // Sets a long string for the text
        bigTextStyle.bigText("This is a very long string that has lots to say about the world and all of the lovely things in it and all the things we will do one day when we have the time and the money and the freedom and the desire.");

        // Sets a title for the big text view
        bigTextStyle.setBigContentTitle("Words of inspiration");

        return bigTextStyle;
    }

    private NotificationCompat.BigPictureStyle createBigPictureStyle() {
        NotificationCompat.BigPictureStyle bigPictureStyle =
                new NotificationCompat.BigPictureStyle();

        // Create bitmap from resource
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lou);

        // Add the bitmap to the big picture style
        bigPictureStyle.bigPicture(bitmap);

        // Sets a title for the big picture view
        bigPictureStyle.setBigContentTitle("Picture of Lou Reed");

        return bigPictureStyle;
    }

    private NotificationCompat.InboxStyle createInboxStyle() {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = new String[6];

        events[0] = "Event 1";
        events[1] = "Event 2";
        events[2] = "Event 3";
        events[3] = "Event 4";
        events[4] = "Event 5";
        events[5] = "Event 6";

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("Event tracker details");

        // Sets summary text for the Inbox style big view.
        inboxStyle.setSummaryText("This is the summary text");

        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        // Note: you need to drag to expand this info on phone;
        // On watch, it comes already expanded
        return inboxStyle;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
