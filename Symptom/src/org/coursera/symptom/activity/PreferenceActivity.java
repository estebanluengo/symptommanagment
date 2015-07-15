package org.coursera.symptom.activity;

import org.coursera.symptom.R;
import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.utils.Utils;

import com.google.common.primitives.Ints;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * This Activity class represents User preferences. The class uses two fragments, once for Patient Preferences and another
 * for Doctor Preferences. Only once is loaded depending of user role. 
 *
 */
public class PreferenceActivity extends Activity {

	public static final String TAG = "PreferenceActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final SharedPreferences prefs = getSharedPreferences(SymptomValues.SYMPTOM_PREFERENCES, MODE_PRIVATE);
		String userRole = prefs.getString(SymptomValues.ROLE_USER, SymptomValues.ROLE_UNKNOWN);		
		//getting the existing fragment if it exists
		Fragment existingFragment = getFragmentManager().findFragmentById(android.R.id.content);
		if (SymptomValues.ROLE_PATIENT.equals(userRole)){
			Log.d(TAG, "Setting patient preferences");			
//			setContentView(R.layout.fragment_patient_prefs);
			if (existingFragment == null || !existingFragment.getClass().equals(PatientPreferenceFragment.class)){
				getFragmentManager().beginTransaction().replace(android.R.id.content, new PatientPreferenceFragment()).commit();
			}
		}else
		if (SymptomValues.ROLE_DOCTOR.equals(userRole)){
			Log.d(TAG, "Setting doctor preferences");
//			setContentView(R.layout.fragment_doctor_prefs); 			
			if (existingFragment == null || !existingFragment.getClass().equals(DoctorPreferenceFragment.class)){
				getFragmentManager().beginTransaction().replace(android.R.id.content, new DoctorPreferenceFragment()).commit();
			}
		}else{
			Toast.makeText(this, getResources().getString(R.string.login_again), Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(PreferenceActivity.this, LoginActivity.class);
			startActivity(intent);
		}
	}
	
	/**
	 * PatientPreference Fragment to load Patient preferences
	 *
	 */
	public static class PatientPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{

		protected static final String TAG = "PatientPreferenceFragment";
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(true);
			Log.d(TAG, "Calling PatientPreferenceFragment onCreate");
			
			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.patient_prefs);	
			SharedPreferences prefs = getPreferenceManager().getSharedPreferences();

			// Register a listener on the SharedPreferences object
			prefs.registerOnSharedPreferenceChangeListener(this);
		}

		/**
		 * This method manages patient preferences changes. The Checkin reminder alarm could be reprogram again
		 * or download alarm too
		 */
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			Preference pref = findPreference(key);
			Log.d(TAG, "onSharedPreferenceChanged called for key:"+key);
		    if (pref instanceof ListPreference) {
		    	Log.d(TAG, "is ListPreference");
		        ListPreference listPref = (ListPreference) pref;
		    	Log.d(TAG, "preference value:"+listPref.getEntry());		        
		        pref.setSummary(listPref.getEntry());
		        if (key.equals("remainderOptions")){
		        	//checkin reminder is reprogram again due to values changes
		        	Utils.setCheckinReminderAlarm(getActivity());
		        }else if (key.equals("intervalDownloadOptions")){
		        	Utils.setDownloadAlarm(SymptomValues.ROLE_PATIENT, getActivity());
		        }
		    }else{
		    	String newValue = sharedPreferences.getString(key, "");
		    	Log.d(TAG, "new value for key:"+key+" "+newValue);
		    	Integer intValue = Ints.tryParse(newValue);
		    	if (intValue == null){
		    		//TODO: do rollback of data. Check if firstHour is < than lastHour
		    	}else{
		    		//checkin reminder is reprogram again due to values changes
		    		pref.setSummary(sharedPreferences.getString(key, ""));		    		
		    		Utils.setCheckinReminderAlarm(getActivity());
		    	}
		    }
			
		}
	}
	
	/**
	 * DoctorPreference Fragment to load Doctor preferences
	 *
	 */
	public static class DoctorPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{

		protected static final String TAG = "DoctorPreferenceFragment";
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(true);
			Log.d(TAG, "Calling DoctorPreferenceFragment onCreate");
			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.doctor_prefs);
			SharedPreferences prefs = getPreferenceManager().getSharedPreferences();

			// Register a listener on the SharedPreferences object
			prefs.registerOnSharedPreferenceChangeListener(this);			
		}
		
		/**
		 * This method manages doctor preferences changes. The download alarm is reprogram again
		 */
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			Preference pref = findPreference(key);
			Log.d(TAG, "onSharedPreferenceChanged called for key:"+key);
		    if (pref instanceof ListPreference) {
		    	Log.d(TAG, "is ListPreference");
		        ListPreference listPref = (ListPreference) pref;
		    	Log.d(TAG, "preference value:"+listPref.getEntry());		        
		        pref.setSummary(listPref.getEntry());
		        if (key.equals("intervalDownloadOptions")){
		        	Utils.setDownloadAlarm(SymptomValues.ROLE_DOCTOR, getActivity());
		        }
		    }			
		}

	}
}
