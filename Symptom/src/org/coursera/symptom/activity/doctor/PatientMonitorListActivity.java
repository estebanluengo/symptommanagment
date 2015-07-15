package org.coursera.symptom.activity.doctor;

import java.util.ArrayList;

import org.coursera.symptom.R;
import org.coursera.symptom.SymptomApplication;
import org.coursera.symptom.orm.Patient;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * This activity shows the patients list that needs monitor needs. It is invoked from notification area when an alert arrives because the 
 * download service analyze check-in information and finds out that some patients needs monitor needs.
 * 
 */
public class PatientMonitorListActivity extends BaseListActivity {

	public static final String TAG = "PatientListActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"Calling onCreate()");
		setContentView(R.layout.activity_patient_list);
		//recover patient monitor list from SymptomApplication
		SymptomApplication sApp = (SymptomApplication)getApplicationContext();
		patientList = sApp.getMonitorList();
//		if (savedInstanceState == null){
//			Bundle extra = this.getIntent().getExtras();
//			if (extra != null){
//				Log.d(TAG, "recovering monitor patient list from call");
//				patientList = extra.getParcelableArrayList(SymptomValues.MONITOR_PATIENT_LIST);
//			}
//		}
//		//trying to recover from savedInstanceState
//		if (patientList == null && savedInstanceState != null){
//			Log.d(TAG, "recovering monitor patient list from saveinstanve");
//			patientList = savedInstanceState.getParcelableArrayList(SymptomValues.MONITOR_PATIENT_LIST);
//		}			
		ArrayList<String> patientNames = getPatientNamesList(patientList);
		arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_patient_item, patientNames);
		setListAdapter(arrayAdapter);
		Log.d(TAG,"setListAdapter");
		
		ListView lv = getListView();

		// Enable filtering when the user types in the virtual keyboard
		lv.setTextFilterEnabled(true);

		// Set an setOnItemClickListener on the ListView
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Patient patient = patientList.get(position);
				Log.d(TAG,"Patient pressed:"+patient.getId());
				startCheckinListActivity(PatientMonitorListActivity.this, patient);
			}			
		});
	}
	
	@Override
	protected void showPatientList(){
		//do nothing
	}
	
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		Log.d(TAG, "onSaveInstanceState called");
//		if (this.patientList != null){
//			Log.d(TAG, "saving monitor patient list");
//			outState.putParcelableArrayList(SymptomValues.MONITOR_PATIENT_LIST, this.patientList);
//		}
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient_monitor_list, menu);
		return true;
	}
	
}
