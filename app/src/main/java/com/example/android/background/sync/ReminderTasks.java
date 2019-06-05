package com.example.android.background.sync;

import android.content.Context;

import com.example.android.background.utilities.NotificationUtils;
import com.example.android.background.utilities.PreferenceUtilities;

public class ReminderTasks{

    //can be used for handling notification actions : increment count or dismiss notification
    public static final String ACTION_INCREMENT_WATER_COUNT = "increment-water-count";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";

    //charging reminder
    public static final String ACTION_CHARGING_REMINDER = "charging-reminder";

    public static void executeTask(Context context, String action){
        if(ACTION_INCREMENT_WATER_COUNT.equals(action)){
            incrementWaterCount(context);
        }else if(ACTION_DISMISS_NOTIFICATION.equals(action)){
            NotificationUtils.clearAllNotifications(context);
        } else if(ACTION_CHARGING_REMINDER.equals(action)){
            issueChargingReminder(context);
        }
    }

//update the charging reminder count and send out the notification
    private static void issueChargingReminder(Context context) {
        PreferenceUtilities.incrementChargingReminderCount(context);
        NotificationUtils.remindUserBecausePhoneCharging(context);
    }

    public static void incrementWaterCount(Context context) {
        PreferenceUtilities.incrementWaterCount(context);
        NotificationUtils.clearAllNotifications(context);
    }
}

/*** Adding firebase job dispatcher
 *  gradle dependency for firbaseJobDispatcher
 *  Create a new chargingremindertask to create charging reminder and update the charging count on screen
 *  Create new service that extends from JobService.Runs the chargingremindertask made above
 *  Add Jobservice to manifest
 *  Usr firebase job dispatcher to schedule the job you made according to constraints
 *  */