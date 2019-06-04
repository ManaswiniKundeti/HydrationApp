package com.example.android.background.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.android.background.MainActivity;
import com.example.android.background.R;
import com.example.android.background.sync.ReminderTasks;
import com.example.android.background.sync.WaterReminderIntentService;

public class NotificationUtils {

    //used to access our notifi after we've displyed it. Handy when we need to cancel the notifi
    //or update it. Num can be set to anything you like.
    private static final int WATER_REMINDER_NOTIFICATION_ID = 1222;

    //uniquely reference PI
    private static final int WATER_REMINDER_PENDING_INTENT_ID = 3417;

    //uniquely reference PI of action
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 3440;
    //uniquely reference PI of action
    private static final int ACTION_DRINK_PENDING_INTENT_ID = 3450;

    //used to link notifcations to this channel
    private static final String WATER_REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";

    //notification action : clear all notiifcations
    public static void clearAllNotifications(Context context){
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


    //this will create a notification for charging phn and also the channel notification belongs to. Also rep for displaying notif
    //this will tell user to drink water
    public static void remindUserBecausePhoneCharging(Context context){
        //notification manager is a system service
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(context.NOTIFICATION_SERVICE);
        //from Oreo, we cant launcha  notification, without it belonging to a notification channel
        //the constructor takes ID(declared above), name for the channel, importance level( high - force notif to popup using headsup display
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel(WATER_REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        //build the actual notification here. attributes sre to define how the notification looks like
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, WATER_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_drink_notification)
                .setLargeIcon(largeIcon(context))
                .setContentText(context.getString(R.string.charging_reminder_notification_body))
                .setContentTitle(context.getString(R.string.charging_reminder_notification_title))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.charging_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(drinkWaterAction(context))
                .addAction(ignoreReminderAction(context))
                .setAutoCancel(true);

        //If the buildVersion is greater than Jelly_bean and lower than Oreo, set priority high
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
            Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        //trigger the notification by calling the notify. pass Unique ID or your choosing and notificationBuilder.build()
        notificationManager.notify(WATER_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    //action to be done when the user ignores the notiifcation reminder
    private static NotificationCompat.Action ignoreReminderAction(Context context){
        //create intent to launch service and set action to it
        Intent ignoreReminderIntent = new Intent(context, WaterReminderIntentService.class);
        ignoreReminderIntent.setAction(ReminderTasks.ACTION_DISMISS_NOTIFICATION);

        //wrap intent with PI to help be be triggered by Notification manager
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //create action for user to ignore the notification & return action
        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.drawable.ic_cancel_black_24px,
                "No, thanks.",
                ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }

    //action to be done when the user clicks on drinks water action on notification
    private static NotificationCompat.Action drinkWaterAction(Context context){
        //create intent to launch service and set action to it
        Intent incrementWaterCountIntent = new Intent(context, WaterReminderIntentService.class);
        incrementWaterCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_WATER_COUNT);

        //wrap intent with PI to help be be triggered by Notification manager
        PendingIntent incrementWaterPendingIntent = PendingIntent.getService(context,
                ACTION_DRINK_PENDING_INTENT_ID,
                incrementWaterCountIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //create action for user to ignore the notification & return action
        NotificationCompat.Action drinkWaterAction = new NotificationCompat.Action(R.drawable.ic_cancel_black_24px,
                "I did it!",
                incrementWaterPendingIntent);
        return drinkWaterAction;
    }




    //creates & returns a pending intent. This PI will trigger when the notification
    //button is presses. PI should open up the MainActivity
    private static PendingIntent contentIntent(Context context){

        //creating explicit intent that allows us to launch the MainActivity
        Intent startActivityIntent = new Intent(context, MainActivity.class);

        //call getActivity to wrap the above created intent as the PI
        //it takes, context, An ID(declared & init above), the explicit intent created abv, flag)
        return PendingIntent.getActivity(context, WATER_REMINDER_PENDING_INTENT_ID,
                startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //necessary to decode a bitmapsize image of appropriate size needed for the notification
    private static Bitmap largeIcon(Context context) {
    //get resource object from context and create&run a bitmap factory.decodeResource in resourceobj and ic_local_drink_black_24px
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_local_drink_black_24px);
        return largeIcon;

    }

}
