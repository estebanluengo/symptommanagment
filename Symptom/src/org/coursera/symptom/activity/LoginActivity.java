package org.coursera.symptom.activity;

import java.util.concurrent.Callable;

import org.coursera.symptom.R;
import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.activity.doctor.PatientListActivity;
import org.coursera.symptom.activity.patient.NextCheckinActivity;
import org.coursera.symptom.client.CallableTask;
import org.coursera.symptom.client.SymptomSvc;
import org.coursera.symptom.client.SymptomSvcApi;
import org.coursera.symptom.client.TaskCallback;
import org.coursera.symptom.orm.Status;
import org.coursera.symptom.orm.SymptomResolver;
import org.coursera.symptom.receivers.BootReceiver;
import org.coursera.symptom.utils.Utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity class that shows Login screen. It will be the first screen that user will see 
 * when they starts app. This Activity makes the User first time login process and it programs
 * checkin reminder alarms if user is a patient and download data alarms in both cases.
 *
 */
public class LoginActivity extends Activity {

	public static final String TAG = "LoginActivity";
	
	private EditText etLogin;
	private EditText etPassword;
	//Object to access local database
	private SymptomResolver resolver;
	//Object to access server via REST API
	private SymptomSvcApi clientSvc;
	
	//LOG: ^(?!.*(nativeGetEnabledTags)).*$
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate() called");
		setContentView(R.layout.activity_login);
		resolver = new SymptomResolver(LoginActivity.this);
		final Button loginButton = (Button) findViewById(R.id.btLogin);
		etLogin = (EditText)findViewById(R.id.etLogin);
		etPassword = (EditText)findViewById(R.id.etPassword);
		loginButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Login onClick");
				login(); //User press login button
			}
		});		
	}
	
	/**
	 * This method makes the login process in background 
	 */
	private void login() {
		final String user = etLogin.getText().toString();
		final String pass = etPassword.getText().toString();

		//We init SymptomSvc object to access server with OAUTH mechanism 
		clientSvc = SymptomSvc.init(SymptomValues.SERVER, user, pass, true);
		//we inform user that wait until process finsh
		Toast.makeText(LoginActivity.this, getResources().getString(R.string.wait), Toast.LENGTH_SHORT).show();
		CallableTask.invoke(new Callable<Status>() {

			@Override
			public Status call() throws Exception {				
				Log.d(TAG, "Login into the server");
//				svc.login(user, pass); //Using OAUTH. We don't need this anymore
				Log.d(TAG, "Login ok. finding out who is this user");
				Status status = clientSvc.status();	
				if (status != null && resolver.whoAmI(status) == null){	//we have server information and is different from local database							
					Log.d(TAG, "Inserting status into local database for the first time");
					resolver.insert(status);
					if (status.isAPatient()){
						Log.d(TAG, "user is a patient. Calling insert patient");							
						resolver.insert(status.getPatient());
						return status;
					}
					if (status.isADoctor()){
						Log.d(TAG, "user is a doctor. Calling insert doctor");
						resolver.insert(status.getDoctor());
						return status;
					}								
				}else{
					Log.d(TAG, "Wellcome back user.");
				}
				return status;
			}
		}, new TaskCallback<Status>() {

			@Override
			public void success(Status result) {
				// OAuth 2.0 grant was successful and we can talk to the server, open up the patient list and init alarms
				Log.d(TAG, "Successful status returned");
				Intent intent;				
				if (result != null){
					//We need SharedPreferences to save User ID and User Role information
					final SharedPreferences prefs = getSharedPreferences(SymptomValues.SYMPTOM_PREFERENCES, MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					Log.d(TAG, "saving into sharedPreferences user_id:"+result.getId());					
					editor.putLong(SymptomValues.ID_USER, result.getId());
					//Write OAuth token in private Peferences object to be used for the services
					editor.putString(SymptomValues.OAUTH_TOKEN, SymptomSvc.getOAuthToken());
					//an AlarmManager to starts the alarms
					AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
					if (result.isAPatient()){
						//Setting CheckinReminder alarms and receives how minutes are left to next reminder
						int minutesToNextReminder = Utils.setCheckinReminderAlarm(mAlarmManager, LoginActivity.this);
						//Setting download alarm to download new patient medications
						Utils.setDownloadAlarm(mAlarmManager, SymptomValues.ROLE_PATIENT, LoginActivity.this);
						//Call this to be prepared if the user reboot their mobile
						setBootReceiver();
						Log.d(TAG, "The user is a patient. Calling NextCheckinActivity");
						editor.putString(SymptomValues.ROLE_USER, SymptomValues.ROLE_PATIENT);						
						editor.commit();
						intent = new Intent(LoginActivity.this, NextCheckinActivity.class);
						//We send minutesToNextReminder to NextChecinActivity to avoid calculate it again
						intent.putExtra(SymptomValues.MINUTES_TO_NEXT_REMINDER, minutesToNextReminder);
						startActivity(intent);
						return;
					}
					if (result.isADoctor()){
						//Setting download alarm to download new patients' checkins
						Utils.setDownloadAlarm(mAlarmManager, SymptomValues.ROLE_DOCTOR, LoginActivity.this);
						//Call this to be prepared if the user reboot their mobile
						setBootReceiver();
						Log.d(TAG, "The user is a doctor. Calling PatientListActivity");										
						editor.putString(SymptomValues.ROLE_USER, SymptomValues.ROLE_DOCTOR);
						editor.commit();
						intent = new Intent(LoginActivity.this, PatientListActivity.class);
						startActivity(intent);
						return;
					}
				}
				Log.d(TAG, "Error login message showed");
				Toast.makeText(LoginActivity.this, getResources().getString(R.string.user_unknown), Toast.LENGTH_LONG).show();				
			}

			@Override
			public void error(Exception e) {
				Log.e(TAG, "Error logging.", e);				
				Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	/**
	 * This method enables the BroadcastReceiver to receive an android.intent.action.BOOT_COMPLETED when the device boots because
	 * the user restart the device for example. 
	 * By default the BroadcastReceiver is not enabled in AndroidManifest.xml and it will be enable only if the alarms are set.
	 */
	private void setBootReceiver(){
		Log.d(TAG, "setBootReceiver called()");
		ComponentName receiver = new ComponentName(this, BootReceiver.class);
		PackageManager pm = this.getPackageManager();
		pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
	}
	
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.d(TAG, "onDestroy() called");
		resolver = null;
	}
	
}
