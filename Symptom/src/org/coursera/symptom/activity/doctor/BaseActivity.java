package org.coursera.symptom.activity.doctor;

import org.coursera.symptom.R;
import org.coursera.symptom.SymptomApplication;
import org.coursera.symptom.activity.PreferenceActivity;
import org.coursera.symptom.orm.Patient;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

/**
 * Base Activity class for Doctor Activity clases. This class handle with optionItemSelected on menu and returns
 * Patient selected by doctor on PatientList activity
 *
 */
public class BaseActivity extends Activity {
	public static final String TAG = "BaseActivity";
		
	/**
	 * Returns patient saved globally in memory. This is a form to share an object with all activities without saving data
	 * to disk. 
	 * 
	 * @return an object Patient.
	 */
	public Patient getPatient(){
		SymptomApplication sApp = (SymptomApplication)getApplicationContext();
		Patient patient = null;
		if (sApp != null){
			patient = sApp.getPatient();
		}
		if (patient == null){
			patient = new Patient();
			patient.setName(getResources().getString(R.string.unknown));			
		}
		return patient;
	}
	
	/**
	 * Handles menu option pressed on Action Bar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(BaseActivity.this, PreferenceActivity.class);
			startActivity(intent);
		}		
		if (id == R.id.action_monitor_patient) {
			Intent intent = new Intent(BaseActivity.this, MonitorActivity.class);
			Log.d(TAG, "starting MonitorActivity");
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_medications) {
			Intent intent = new Intent(BaseActivity.this, MedicationsActivity.class);
			Log.d(TAG, "starting MedicationsActivity");
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
	
}
