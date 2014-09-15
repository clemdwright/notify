package com.example.clemw.notify;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PingService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
//    private static final String ACTION_FOO = "com.example.clemw.notify.action.FOO";
//    private static final String ACTION_BAZ = "com.example.clemw.notify.action.BAZ";

//    private static final String ACTION_BEEN = "com.example.clemw.notify.action.BEEN";
//    private static final String ACTION_SAVE = "com.example.clemw.notify.action.SAVE";

    private static final String ACTION_BEEN = "ACTION_BEEN";
    private static final String ACTION_SAVE = "ACTION_SAVE";

    // TODO: Rename parameters
//    private static final String EXTRA_PARAM1 = "com.example.clemw.notify.extra.PARAM1";
//    private static final String EXTRA_PARAM2 = "com.example.clemw.notify.extra.PARAM2";

    private static final String EXTRA_PARAM1 = "id";
    private static final String EXTRA_PARAM2 = "com.example.clemw.notify.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBeen(Context context, int param1) {
        Intent intent = new Intent(context, PingService.class);
        intent.setAction(ACTION_BEEN);
        intent.putExtra(EXTRA_PARAM1, param1);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, PingService.class);
        intent.setAction(ACTION_SAVE);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public PingService() {
        super("PingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_BEEN.equals(action)) {
//                Log.d("PingService", EXTRA_PARAM1);
//
//                Log.d("Intent", intent.toString());
//
//                Bundle extras = intent.getExtras();
//                Log.d("Extras", extras.toString());


                //For some reason, i'm having trouble extracting the notification id from the intent's extras
                //My thought now is it might be that I'm trying to extract it from a pending intent,
                //But what I need it from is an intent intent. I'm a bit confused about what gets passed.
                //Psych, I don't think PendingIntents can _have_ extras. Hmm.


                final int param1 = intent.getIntExtra(EXTRA_PARAM1, 0);

                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBeen(param1, param2);
            } else if (ACTION_SAVE.equals(action)) {
                final int param1 = intent.getIntExtra(EXTRA_PARAM1, 0);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionSave(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBeen(int param1, String param2) {
        // TODO: Handle action Foo

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        mNotificationManager.cancel(param1);

        mNotificationManager.cancelAll();

        Log.d("PingService", Integer.toString(param1));

//        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSave(int param1, String param2) {
        // TODO: Handle action Baz
//        throw new UnsupportedOperationException("Not yet implemented");


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }
}
