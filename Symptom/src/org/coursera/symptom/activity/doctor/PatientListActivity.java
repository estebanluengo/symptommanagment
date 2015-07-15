package org.coursera.symptom.activity.doctor;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.coursera.symptom.R;
import org.coursera.symptom.activity.LoginActivity;
import org.coursera.symptom.client.CallableTask;
import org.coursera.symptom.client.SymptomSvc;
import org.coursera.symptom.client.SymptomSvcApi;
import org.coursera.symptom.client.TaskCallback;
import org.coursera.symptom.orm.Patient;
import org.coursera.symptom.utils.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This Activity class shows a Doctor's Patient list. The list is always returned from the server
 * to avoid write medical patients information in the mobile device.
 *
 */
public class PatientListActivity extends BaseListActivity {

	public static final String TAG = "PatientListActivity";
	//Object to access to the server
	private SymptomSvcApi svc;
	//A FragmentDialog to show a field to search a Patient by name
	private SearchPatientDialogFragment mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"Calling onCreate()");
		setContentView(R.layout.activity_patient_list);
		//This activity uses a simple ArrayAdapter of String to show patient list
		arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_patient_item, new ArrayList<String>());
		setListAdapter(arrayAdapter);
		Log.d(TAG,"setListAdapter");
		
		ListView lv = getListView();
		
		// Enable filtering when the user types in the virtual keyboard
		lv.setTextFilterEnabled(true);

		// Set an setOnItemClickListener on the ListView
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//Gets a patient from position
				Patient patient = patientList.get(position);
				Log.d(TAG,"Patient pressed:"+patient.getId());
				startCheckinListActivity(PatientListActivity.this, patient);
			}			
		});		
		svc = SymptomSvc.getOrShowLogin(PatientListActivity.this, true);
	}
	
	
	/**
	 * Shows patients list on screen. This method calls server in order to download doctor's patients list. This
	 * call is made in background.
	 */
	@Override
	protected void showPatientList(){
		Log.d(TAG, "showPatientList() called");		
		CallableTask.invoke(new Callable<ArrayList<Patient>>() {
			@Override
			public ArrayList<Patient> call() throws Exception {	
				ArrayList<Patient> list = null;
				if (svc != null){
					try{
						//call server to download patient list
						list = svc.getPatients(Utils.getUserId(PatientListActivity.this));
					}catch(Exception e){
						Log.d(TAG, "Error getting patient list from server", e);
					}
				}
				return list;
			}
		}, new TaskCallback<ArrayList<Patient>>() {

			@Override
			public void success(ArrayList<Patient> list) {
				if (list != null){
					Log.d(TAG, "Patients list successfully returned from server");
					patientList = list;
					//convert List<Patient> into Lis<String> with patient names
					ArrayList<String> patientNames = getPatientNamesList(patientList);
					arrayAdapter.clear();
					arrayAdapter.addAll(patientNames);
					arrayAdapter.notifyDataSetChanged();
					Log.d(TAG, "Number of patients recover:"+list);
				}else{
					Log.d(TAG, "No patients were found");
					Toast.makeText(PatientListActivity.this, getResources().getString(R.string.no_patient_list), Toast.LENGTH_LONG).show();
					Intent i = new Intent(PatientListActivity.this, LoginActivity.class);
					//if there is no doctor's patients list then maybe it is better login again.
					startActivity(i);
					finish();					
				}
			}
		
			@Override
			public void error(Exception e) {
				Log.d(TAG, "error getting patient list", e);
				Toast.makeText(PatientListActivity.this, getResources().getString(R.string.error_get_patient_list), Toast.LENGTH_LONG).show();
				finish();
			}
		}
		);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		//action search a patient
		if (id == R.id.action_search_patient) {
			if (mDialog == null){
				Log.d(TAG, "SearchPatientDialogFragment new instance");
				mDialog = SearchPatientDialogFragment.newInstance();
			}
			Log.d(TAG,"Showing SearchPatientDialogFragment");
			mDialog.show(getFragmentManager(), getResources().getString(R.string.search_patient));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * This method searches a patient on server by name. The method is executed in background. Once the information
	 * is returned it calls CheckinListActivity with patient object returned from server.
	 * 
	 * @param patientName the patient name
	 */
	protected void searchPatient(final String patientName){
		Log.d(TAG,"Calling searchPatient");

		CallableTask.invoke(new Callable<Patient>() {
			@Override
			public Patient call() throws Exception {	
				//gets patient object from server that corresponds to patientName
				Patient patient = svc.getPatient(Utils.getUserId(PatientListActivity.this), patientName);
				return patient;
			}
		}, new TaskCallback<Patient>() {

			@Override
			public void success(Patient patient) {
				if (patient != null){
					mDialog.dismiss();
					Log.d(TAG,"Patient searched with id:"+patient.getId());
					startCheckinListActivity(PatientListActivity.this, patient);
				}else{
					Log.d(TAG, "Patient not found");
					Toast.makeText(PatientListActivity.this, getResources().getString(R.string.patient_not_found_by_name), Toast.LENGTH_SHORT).show();
				}
			}
		
			@Override
			public void error(Exception e) {
				Log.d(TAG, "error getting patient by name", e);
				Toast.makeText(PatientListActivity.this, getResources().getString(R.string.error_get_patient), Toast.LENGTH_LONG).show();
				finish();
			}
		}
		);
		
		
	}
	
	/**
	 * A class DialogFragment to show dialog with search patient screen
	 *
	 */
	@SuppressLint("InflateParams")
	public static class SearchPatientDialogFragment extends DialogFragment {

		public static SearchPatientDialogFragment newInstance() {
			return new SearchPatientDialogFragment();
		}

		// Build AlertDialog using AlertDialog.Builder
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			View view = inflater.inflate(R.layout.dialog_search_patient, null);
			final EditText etPatientSearch = (EditText)view.findViewById(R.id.etPatientSearch);
			builder.setView(view);			
			Log.d(TAG,"setting view dialog_search_patient to dialog");
			builder.setCancelable(true)
			   .setPositiveButton("Search",
							new DialogInterface.OnClickListener() {
								public void onClick(
										final DialogInterface dialog, int id) {
											String patientName = etPatientSearch.getText().toString();
											((PatientListActivity) getActivity()).searchPatient(patientName);
								}
							}).create();
			
			return builder.create();
		}
	}
}