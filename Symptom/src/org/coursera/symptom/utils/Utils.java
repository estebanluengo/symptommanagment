package org.coursera.symptom.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.services.DownloadIntentService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Optional;
import com.google.common.primitives.Ints;

/**
 * This class has a group of methods to work with dates in order to make conversions and calculations.
 * It has also a set of methods to make the calculations for next check-in reminder and finally a set of
 * methods to set the download and check-in reminder alarms. 
 */
public class Utils {
	
	public static final String TAG = "Utils";
	public static final String formatDate = "yyyy-MM-dd";
	public static final String formatHour = "HH:mm";
	
	/*
	 * Date Utils
	 */	 	
	public static Date getCurrentDate(){
		Calendar c = Calendar.getInstance();
		return c.getTime();
	}
	
	public static String getCurrentStringDate(){
		return dateAndTimeToString(getCurrentDate());
	}
	
	public static String dateToString(Date date, String format){
		String aDate = new SimpleDateFormat(format, Locale.getDefault()).format(date);
		return aDate;
	}

	public static String dateToString(Date date) {
		return dateToString(date, formatDate);
	}

	public static String timeToString(Date time) {
		return dateToString(time, formatHour);
	}    
	
	//ISO_8601
	public static String dateAndTimeToString(Date date){
		DateTime dt = new DateTime(date);
		return dt.toString();
	}
	
	public static Date stringToDate(String date, String format){
		DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
		DateTime dt = formatter.parseDateTime(date);
		return dt.toDate();
	}

	public static Date stringToDate(String date){
		return stringToDate(date, formatDate);
	}
	
	public static Date stringToTime(String time){
		return stringToDate(time, formatHour);
	}
	
	public static Date stringToDateAndTime(String date, String time){
		return stringToDate(date + " " + time, formatDate + " " + formatHour);
	}
	
	public static Date stringToDateAndTime(String dateAndtime){
		DateTime dt = new DateTime(dateAndtime);
		return dt.toDate();
	}
	
	public static String convertDatetoString(String dateAndTime){
		Date aDate = stringToDateAndTime(dateAndTime);
		String result = dateToString(aDate) + " " + timeToString(aDate);
		return result;
	}
	
	public static String convertDatetoString(String date, String time){
		return convertDatetoString(date + "T" + time);
	}
	
	public static String convertDateToISO(String date, String time) {
		Date aDate = stringToDateAndTime(date, time);
		String newDate = dateToString(aDate);
		return newDate;
	
	}
	
	/**
	 * Return a String with two digits. If n is < 10 the method adds a 0 value
	 * @param n number to be formated
	 * @return a String with two digits
	 */
	public static String getTwoDigits(int n){
		if (n < 10){
			return "0" + n;
		}else{
			return "" + n;
		}
	}

	/**
	 * Return a date with the format yyyy-mm-dd
	 * 
	 * @param year 
	 * @param month
	 * @param day
	 * @return a String with format yyyy-mm-dd
	 */
	public static String getFormatDate(int year, int month, int day) {
		return new StringBuilder().append(year).append("-").append(getTwoDigits(month)).append("-").append(getTwoDigits(day)).toString();
	}
	
	/**
	 * Return a time with format HH:mm
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static String getFormatHour(int hour, int minute){
		return new StringBuilder(getTwoDigits(hour)).append(":").append(getTwoDigits(minute)).toString();
	}
	
	/*
	 * Activity utils
	 */
	
	/**
	 * Returns database user id that is using this app. The methods gets this information from SharedPreferences object
	 * @return a long with user id information
	 */
	public static long getUserId(Context context){
    	final SharedPreferences prefs = context.getSharedPreferences(SymptomValues.SYMPTOM_PREFERENCES, Context.MODE_PRIVATE);
		long userId = prefs.getLong(SymptomValues.ID_USER, 0);
		return userId;
    }
	
	/*
	 * Alarms utils 
	 */
	
	/**
	 * Return current time in minutes
	 * 
	 * @return the method returns a int with current time in minutes
	 */
	public static int currentTimeInMinutes(){
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);		
		int minutes = calendar.get(Calendar.MINUTE);
		return hour * 60 + minutes;
	}
	
	/**
	 * Return true if currentTime is >= firstHour and currentTime <= lastHour
	 * 
	 * @param firstHour represents first time in minutes
	 * @param lastHour represents last time in minutes
	 * @param currentTime represents current time in minutes
	 * @return a boolean 
	 */
	public static boolean isInBoundaryHours(int firstHour, int lastHour, int currentTime){
		return (currentTime >= firstHour && currentTime <= lastHour);
	}
	
	/**
	 * Returns how many minutes are left until the next reminder
	 * 
	 * @param firstHour The first hour in minutes in morning the user can receive a reminder
	 * @param lastHour The last hour in minutes in night the user can receive a reminder
	 * @param intervalReminder interval period in minutes the user will receive a reminder
	 * 
	 * @return function returns a long that represents how many minutes are left for next reminder
	 */
	public static int getMinutesToNextReminder(int firstHour, int lastHour, int intervalReminder) {
		int currentTime = currentTimeInMinutes(); // 
		int mNextTime = 0;
		//if currentTime is not in the user's desired hours 
		if (!isInBoundaryHours(firstHour, lastHour, currentTime)){
			if (currentTime < firstHour){ //after 24:00 PM
				mNextTime = firstHour - currentTime; //first hour checkin
			}else{ //before 24:00 PM
				mNextTime = firstHour + (24*60 - currentTime); //last hour checkin
			}
		}else{
			int nReminders = (currentTime - firstHour) / intervalReminder;
			mNextTime = (firstHour + (intervalReminder * (nReminders+1))) - currentTime;
			if (mNextTime == 0){ //if next time is now we delay 1 minute
				mNextTime = 1;
			}
		}
		return mNextTime;
	}
	
	/**
	 * Returns minutes to next reminder based on user preferences and current time
	 * 
	 * @param context Activity, BroadcastReceiver or Service that calls this method
	 * @return a Integer with the minutes to next reminder
	 */
	public static int getMinutesToNextReminder(Context context){
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		Integer intervalReminder = Optional.fromNullable(Ints.tryParse(prefs.getString("remainderOptions", "4"))).or(4); //four times every day by default
		Integer firstHour = Optional.fromNullable(Ints.tryParse(prefs.getString("firstHour", "8"))).or(8); // 8:00 AM by default
		Integer lastHour = Optional.fromNullable(Ints.tryParse(prefs.getString("lastHour", "23"))).or(23); // 23:00 PM by default
		
		int repeat = ((lastHour - firstHour) * 60)  / (intervalReminder - 1); //every repeat minutes the alarm will fire a broadcast 
		Log.d(TAG, "repeat interval in minutes:"+repeat);
		int minutesToNextReminder = Utils.getMinutesToNextReminder(firstHour * 60, lastHour * 60, repeat);
		Log.d(TAG, "Minutes until next checkin reminder:"+minutesToNextReminder);
		return minutesToNextReminder;
	}	
	
	/**
	 * Get next time reminder in human format HH:MM. The method adds minutesToNextReminder parameter
	 * to current time in order to calculate the time.
	 * 
	 * @param minutesToNextReminder the time in minutes to the next reminder
	 * @return a String with the next time reminder in human format HH:MM 
	 */
	@SuppressLint({ "SimpleDateFormat" })
	public static String getNextTimeReminder(int minutesToNextReminder) {
		//add this milliseconds to current time
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, minutesToNextReminder);
		//convert to human time
		SimpleDateFormat df = new SimpleDateFormat(formatHour);
		String newTime = df.format(calendar.getTime());
		Log.d(TAG, "next reminder converted:"+newTime);
		return newTime;
	}	
	
	/**
	 * It sets a repeating alarm at patient adjustable times to makes the check-in reminders to patients. This is done
	 * sending a broadcast org.coursera.symptom.CHECKIN_REMINDER when the alarm is fired. When the broadcast is
	 * sent, the BroadcastReceiver org.coursera.symptom.receivers.NotificationCheckinReceiver receives this broadcast
	 * to send a notification to patient mobile's notification area.
	 *   
	 * @param mAlarmManager a AlarmManaget to schedule the alarm
	 * @param context who calls this method
	 * @return an int that represents how many minutes are left to the next reminder
	 * @see org.coursera.symptom.receivers.NotificationCheckinReceiver
	 */
	public static int setCheckinReminderAlarm(AlarmManager mAlarmManager, Context context){
		Log.d(TAG, "setCheckinReminderAlarm() called");
		if (context != null){
			//The intent associated to org.coursera.symptom.CHECKIN_REMINDER
			Intent checkinReminderIntent = new Intent(SymptomValues.CHECKIN_REMINDER);
			//Pending intent to send a broadcast
			PendingIntent checkinReminderPendingIntent = PendingIntent.getBroadcast(context, 0, checkinReminderIntent, 0);
			//First cancel previous alarms
			mAlarmManager.cancel(checkinReminderPendingIntent);
			//Get user preferences time-adjustable times 
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
			Integer intervalReminder = Optional.fromNullable(Ints.tryParse(prefs.getString("remainderOptions", "4"))).or(4); //four times every day by default
			Integer firstHour = Optional.fromNullable(Ints.tryParse(prefs.getString("firstHour", "8"))).or(8); // 8:00 AM by default
			Integer lastHour = Optional.fromNullable(Ints.tryParse(prefs.getString("lastHour", "23"))).or(23); // 23:00 PM by default
			//Calculates the minutes to repeat the alarm
			int repeat = ((lastHour - firstHour) * 60)  / (intervalReminder - 1); //every repeat minutes the alarm will fire a broadcast 
			Log.d(TAG, "repeat interval in minutes:"+repeat);
			//Calculates the minutes to the next reminder
			int minutesToNextReminder = Utils.getMinutesToNextReminder(firstHour * 60, lastHour * 60, repeat);
			Log.d(TAG, "Minutes until next checkin reminder:"+minutesToNextReminder);
			int milliToNextReminder = minutesToNextReminder * 60 * 1000;
			//schedule the alarm with the pendingIntent to send a broadcast. The alarm wakeup the device to alert patient
			mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + milliToNextReminder, 
					repeat * 60 * 1000,	checkinReminderPendingIntent);
			return minutesToNextReminder;
		}else{
			Log.d(TAG, "Context is null. We can not set checkin reminder alarm");
			return 0;
		}
	} 
	
	/**
	 * It sets a repeating alarm at patient adjustable times to makes the check-in reminders to patients. This is done
	 * sending a broadcast org.coursera.symptom.CHECKIN_REMINDER when the alarm is fired. When the broadcast is
	 * sent, the BroadcastReceiver org.coursera.symptom.receivers.NotificationCheckinReceiver receives this broadcast
	 * to send a notification to patient mobile's notification area.
	 *   
	 * @param context who calls this method
	 * @return an int that represents how many minutes are left to the next reminder
	 * @see org.coursera.symptom.receivers.NotificationCheckinReceiver
	 */
	public static int setCheckinReminderAlarm(Context context){
		if (context != null){
			return setCheckinReminderAlarm((AlarmManager)context.getSystemService(Context.ALARM_SERVICE), context);
		}else{
			return 0;
		}
	}
	
	/**
	 * It sets download alarm in order to download checkins or medications in the future via alarm. The role parameter
	 * determines if method will activate a service to download checkins or medications. 
	 * This method is called by LoginActivity and Utils.setDownloadAlarm(Context)
	 * 
	 * @param mAlarmManager AlarmManager
	 * @param role represents user role. DOCTOR for doctors or PATIENT for patients. This parameter allows method to
	 * make the correct intent to startService via the alarm 
	 * @param context BroadcastReceiver or Activity that calls this method. 
	 */
	public static void setDownloadAlarm(AlarmManager mAlarmManager, String role, Context context){
		Log.d(TAG, "setDownloadAlarm called()" );
		if (context != null){
			//Makes the intent to start the service DownloadIntentService
			Intent pendingIntent = DownloadIntentService.makeDownloadIntent(context, role);
			PendingIntent downloadPendingIntent = PendingIntent.getService(context, 0, pendingIntent, 0);
			//we cancel previous alarms
			mAlarmManager.cancel(downloadPendingIntent);
			//Get user preferences time-adjustable times 
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
			long next =  30 * 1000;  //in 30 seconds the service will download data
			Log.d(TAG, "next download data in 30 seconds");			
			long repeat = Optional.fromNullable(Ints.tryParse(prefs.getString("intervalDownloadOptions", "60"))).or(60); //every hour by default	
			Log.d(TAG, "repeat download data every "+repeat+" minutes");
			//schedule the alarm every repeat minutes
			mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + next, repeat * 60 * 1000, downloadPendingIntent);
		}else{
			Log.d(TAG, "Context is null. We can not set the alarm");
		}
	}
	
	/**
	 * It sets download alarm in order to download checkins or medications. The method recovers user role from sharedPreferences.
	 * It is possible that role information can not be recover from sharedPreferences and in this case method will not be able 
	 * to start the alarm.
	 * This method is called from DoctorPreferenceFragment, PatientPreferenceFragment and BootReceiver classes
	 * 
	 * @param context BroadcastReceiver or Activity that calls this method. It can be null
	 */
	public static void setDownloadAlarm(String role, Context context){
		Log.d(TAG, "setDownloadAlarm called()" );
		if (context != null){
			Log.d(TAG, "from role:"+role);
			if (!role.equals(SymptomValues.ROLE_UNKNOWN)){
				setDownloadAlarm((AlarmManager)context.getSystemService(Context.ALARM_SERVICE), role, context);
				Log.d(TAG, "Alarm download data set");
			}else{
				Log.d(TAG, "User role unknown. We can not set alaram download data");
			}
		}else{
			//This avoid the problem that Android called multiple times when user start PreferenceActivity more than once.
			Log.d(TAG, "Context is null. We can not update alarm");
		}
	}
	
}