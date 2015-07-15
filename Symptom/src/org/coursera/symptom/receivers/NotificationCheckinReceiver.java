package org.coursera.symptom.receivers;

import org.coursera.symptom.R;
import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.activity.patient.CheckinActivity;
import org.coursera.symptom.utils.Utils;

import com.google.common.base.Optional;
import com.google.common.primitives.Ints;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * This BroadcastReceiver receives an Intent org.coursera.symptom.CHECKIN_REMINDER
 * The target for this class is to send a notification reminder to the patient.
 * This receiver will receive the broadcast sent via repeating alarm.
 * 
 */
public class NotificationCheckinReceiver extends BroadcastReceiver{
	public static final String TAG = "NotificationCheckinReceiver";
	private static final int MY_NOTIFICATION_ID = 1;
	private Intent mNotificationIntent;
	private PendingIntent mContentIntent;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive called()");
		String action = intent.getAction();
		Log.d(TAG, "Action received:"+action);
		if (SymptomValues.CHECKIN_REMINDER.equals(action)){
			//notification will start CheckinActivity in a new activity
			mNotificationIntent = new Intent(context, CheckinActivity.class);
			mContentIntent = PendingIntent.getActivity(context, 0, mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
			
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
			Integer firstHour = Optional.fromNullable(Ints.tryParse(prefs.getString("firstHour", "8"))).or(8); // 8:00 AM by default
			Integer lastHour = Optional.fromNullable(Ints.tryParse(prefs.getString("lastHour", "23"))).or(23); // 23:00 PM by default
			//checkin if we receive the alarm inside boundary hours.
			if (Utils.isInBoundaryHours(firstHour * 60, lastHour * 60, Utils.currentTimeInMinutes())){
				Log.d(TAG, "calling BroadcastReceiver in boundary hours");
				Notification.Builder notificationBuilder = new Notification.Builder(context)
					.setTicker(context.getResources().getString(R.string.noti_start_checkin_title))
					.setContentText(context.getResources().getString(R.string.noti_start_checkin))
					.setContentTitle(context.getResources().getString(R.string.noti_start_checkin_title))
					.setSmallIcon(R.drawable.ic_stat_notify_reminders)
					.setAutoCancel(true)
					.setContentIntent(mContentIntent);
				// Pass the Notification to the NotificationManager:
				NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(MY_NOTIFICATION_ID, notificationBuilder.build());
				Log.d(TAG, "notification send to user");
			}else{
				Log.d(TAG, "calling BroadcastReceiver out boundary hours");
			}			
		}
		
	}

}
