package org.coursera.symptom.activity.doctor;

import java.util.ArrayList;

import org.coursera.symptom.R;
import org.coursera.symptom.SymptomApplication;
import org.coursera.symptom.activity.PreferenceActivity;
import org.coursera.symptom.orm.Patient;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

/**
 * Base ListActivity class for Doctor ListActivity clases. This class handle with optionItemSelected on menu 
 * and other common methods
 *
 */
public abstract class BaseListActivity extends ListActivity{

	public static final String TAG = "BaseListActivity";
	
	protected ArrayList<Patient> patientList;
	protected ArrayAdapter<String> arrayAdapter;
	
	/**
	 * Convert ArrayList of Patient objects to ArrayLis of String objects with patient name
	 * 
	 * @param list ArrayList of Patient object we want to convert
	 * @return an ArrayList<String> containing patients' names
	 */
	protected ArrayList<String> getPatientNamesList(ArrayList<Patient> list){
		int sizeList = (list != null)?list.size():1;
		ArrayList<String> patientNames = new ArrayList<String>(sizeList);
		
		//convert ArrayList<Patient> to ArrayList<String>
		if (list != null){			
			for (Patient patient: list){
				patientNames.add(patient.getName()+" "+patient.getLastname());
			}
		}
		return patientNames;
	}
	
	/**
	 * Calls CheckinListActivity and saves a selected Patient object in Global SymptomApplication object  
	 * @param patient a Patient object to send
	 */
	protected void startCheckinListActivity(Context context, Patient patient) {
		Intent intent = new Intent(context, CheckinListActivity.class);
		((SymptomApplication)getApplicationContext()).setPatient(patient);
		Log.d(TAG,"Calling CheckinListActivity for patient:"+patient.getId());
		startActivity(intent);
	}	
	
	/**
	 * When the activity is resumed, it will call showPatientList in order to show patient list information
	 */
	@Override
	protected void onResume(){
		super.onResume();
		showPatientList();
	}
	
	/**
	 * Shows patients list information on screen. Every subclass is responsible to implement this
	 */
	protected abstract void showPatientList();
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, PreferenceActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_patient_list) {
			Intent intent = new Intent(this, PatientListActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
}