package org.coursera.symptom.activity.patient;

import org.coursera.symptom.R;
import org.coursera.symptom.activity.PreferenceActivity;
import org.coursera.symptom.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * This activity class show next checkin time information to patient. The user can starts
 * manually a new check-in if they want pressing a button on the screen
 *
 */
public class NextCheckinActivity extends Activity {

	public static final String TAG = "NextCheckinActivity";
	private TextView tvTimeNextChexkin;
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");
		setContentView(R.layout.activity_nextcheckin);			
		tvTimeNextChexkin = (TextView)findViewById(R.id.tvTimeNextChexkin);		
		Button btStartChecking = (Button)findViewById(R.id.btStartChecking);
		btStartChecking.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//User wants to start a new checkin
				Log.d(TAG, "Calling CheckinActivity");
				Intent intent = new Intent(NextCheckinActivity.this, CheckinActivity.class);
				startActivity(intent);
			}
		});	
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		//every time activity appears we need to calculate minutes to next reminder.
		//we can not save it in Instance State because user can change preferences parameters
		int minutesToNextReminder = Utils.getMinutesToNextReminder(NextCheckinActivity.this);
		String minutesText = Utils.getNextTimeReminder(minutesToNextReminder);
		Log.d(TAG, "next reminder in human time:"+minutesText);
		tvTimeNextChexkin.setText(minutesText);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(NextCheckinActivity.this, PreferenceActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
