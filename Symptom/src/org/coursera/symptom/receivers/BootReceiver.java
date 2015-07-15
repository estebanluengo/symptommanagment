package org.coursera.symptom.receivers;

import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.utils.Utils;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * This BroadcastReceiver starts the Alarms When the Device Boots. The receiver is not enable by default and
 * the login activity needs to enable
 * 
 * https://developer.android.com/training/scheduling/alarms.html#boot
 *
 * am broadcast -a android.intent.action.BOOT_COMPLETED for testing
 */
public class BootReceiver extends BroadcastReceiver{
	public static final String TAG = "BootReceiver";
	
	/**
	 * When the device boots it will send a Broadcast with the intent android.intent.action.BOOT_COMPLETED 
	 * and this BroadcastReceiver will receive this broadcast in order to restart the alarms. 
	 * Once alarm for download data from server and if the user is a Patient, another one for the check-in reminders.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive called()");
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
			SharedPreferences prefs = context.getSharedPreferences(SymptomValues.SYMPTOM_PREFERENCES, Context.MODE_PRIVATE);
			String userRole = prefs.getString(SymptomValues.ROLE_USER, SymptomValues.ROLE_UNKNOWN);
			if (userRole.equals(SymptomValues.ROLE_PATIENT)){
				AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				Utils.setCheckinReminderAlarm(mAlarmManager, context);
				Log.d(TAG, "Checkin reminder set");	
			}		
			Utils.setDownloadAlarm(userRole, context);			
		}
	}

}
