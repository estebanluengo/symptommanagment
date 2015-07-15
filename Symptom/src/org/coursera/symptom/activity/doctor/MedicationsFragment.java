package org.coursera.symptom.activity.doctor;

import java.util.concurrent.Callable;

import org.coursera.symptom.R;
import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.client.CallableTask;
import org.coursera.symptom.client.SymptomSvc;
import org.coursera.symptom.client.SymptomSvcApi;
import org.coursera.symptom.client.TaskCallback;
import org.coursera.symptom.orm.PatientMedication;
import org.coursera.symptom.orm.SymptomResolver;
import org.coursera.symptom.provider.SymptomSchema;
import org.coursera.symptom.utils.Utils;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This fragment manage all work that allow doctor to manage patient medication list. 
 * The fragment uses a LoaderManager with Cursor object to retrieve PatientMedication information from
 * local database. This is a great advantage because LoaderManager handles the data load in a very
 * efficient way. Additionally the class implements ViewBinder to format active field before it is shown in the list and
 * PopupMenu.OnMenuItemClickListener to show popup menu when the user press a long time in any row.
 * 
 */
public class MedicationsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, 
																PopupMenu.OnMenuItemClickListener, 
																SimpleCursorAdapter.ViewBinder{
	private static final String TAG = "MedicationsFragment";
	//LOADER ID
	private static final int LOADER_ID = 1;
	//Object to access sever
	private SymptomSvcApi svc;
	//Object to access local database
	private SymptomResolver resolver;
	//Index row selected by doctor
	private int mCurrIdx = -1;
	//ListView header. We assign this header to the fragment view programmatically
	private View header;
	//ListView where the patient medication information will be shown
	private ListView listView;
	//TextView to assign the patient name
	private TextView tvPatientName;
	//EditText with the name of a new medication inserted by doctor
	private EditText etNewMedication;
	//Button to add new medication to patient medication list
	private Button btNewMedication;
	//SimpleCursorAdapter that contains checkin information to be shown on list
	private SimpleCursorAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * This method gets a header with patient name and inject into parent view
	 */
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		header = (View) inflater.inflate(R.layout.header_medications, null);		
		tvPatientName = (TextView)header.findViewById(R.id.tvPatientName);
		etNewMedication = (EditText)header.findViewById(R.id.etNewMedication);
		btNewMedication = (Button)header.findViewById(R.id.btNewMedication);
		View v = super.onCreateView(inflater, container, savedInstanceState);
	    ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.fragment_medications, container, false);
	    parent.addView(v, 0);
	    return parent;
	}

	/**
	 * Once the activity is created, the method prepares adapter to load data from database
	 */
	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);

		listView = getListView();
		if (getListAdapter() == null){
			listView.addHeaderView(header);
		}
		// Set the list choice mode to allow only one selection at a time
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        org.coursera.symptom.orm.Patient patient = ((MedicationsActivity)getActivity()).getPatient();
        tvPatientName.setText(patient.getFullName());
        Log.d(TAG, "setting patient name to textview on the header");
		
        //The columns that adapter will show
		String[] columns = {
				SymptomSchema.PatientMedication.Cols.NAME, SymptomSchema.PatientMedication.Cols.ACTIVE
		};
		//The textViews where these columns will be shown
		int[] to = new int[] { 
				R.id.tvMedicationNameItem, R.id.tvMedicationStateItem 
		 };
		// At the moment, set the list adapter for the ListView with empty data set
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_medication_item, null, columns, to, 0);
		adapter.setViewBinder(this);
		setListAdapter(adapter);
		//don't show list yet, we are waiting for the results
        setListShown(false);
        
        //Prepare button to onClick listener
        btNewMedication.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {	
				//a new medication will be insert
				String medicationName = etNewMedication.getText().toString();
				if ("".equals(medicationName)){
					Toast.makeText(getActivity(), getResources().getString(R.string.error_empty_patient_medication), Toast.LENGTH_LONG).show();					
					Log.d(TAG, "Medication name will no be empty");
				}else{
					//Call this method to insert the new patient medication in the server and in the local database
					setPatientMedication(SymptomValues.ACTION_INSERT);
				}
			}
		});
        
        //Prepare listview to OnItemLongClickListener
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, "pressed at:"+position);
				//show popup menu with options delete and activate
				showPopup(view, position);
				return false;
			}
		});
        svc = SymptomSvc.getOrShowLogin(getActivity(), true);
        resolver = new SymptomResolver(getActivity());
	}
	
	/**
	 * Loader is initialize here.
	 */
	@Override
    public void onResume(){
    	super.onResume();
    	Log.d(TAG, "onResume() called");
        LoaderManager lm = getLoaderManager();
        if (getLoaderManager().getLoader(LOADER_ID) == null) {
        	Log.d(TAG, "Initializing the new Loader...");
        } else {
        	Log.d(TAG, "Reconnecting with existing Loader (id '1')...");
        }	        
        lm.initLoader(LOADER_ID, null, this);
    }	
	
	/**
	 * This method can make three different actions depending action parameter value.
	 * If action parameter is equals to SymptomValues.ACTION_INSERT then the method adds new patient medication in the system.
	 * To do this the method first sends the new medication to the server and once the server returns  
	 * the information was successfully saved, it's saved in the local database and it is shown on screen.
	 * 
	 * If action parameter is equals to SymptomValues.ACTION_DELETE or SymptomValues.ACTION_UPDATE then the method update
	 * the state of patient medication in the server, deactivating or activating depending of the value, and once the
	 * server returns that the information was successfully updated, it's updated in the local database and it is
	 * shown on screen.
	 * All this work is made in background 
	 * 
	 */
	private void setPatientMedication(final String action){
		Log.d(TAG, "addNewMedication() called");
		CallableTask.invoke(new Callable<PatientMedication>() {
			@Override
			public PatientMedication call() throws Exception {	
				PatientMedication pm = null;
				if (svc != null){
					//get global patient object
					org.coursera.symptom.orm.Patient patient = ((MedicationsActivity)getActivity()).getPatient();
					if (action.equals(SymptomValues.ACTION_INSERT)){
						String medicationName = etNewMedication.getText().toString();			
						//create patient medication in the server
						pm = svc.createPatientMedication(Utils.getUserId(getActivity()), patient.getId(), medicationName);
						Log.d(TAG, "Patient medication saved successfully in the server");
						pm.setPatient(patient);
						//insert new patient medication object in the local database
						Uri uri = resolver.insert(pm);
						Log.d(TAG, "Patient medication saved in local database with uri:"+uri);
					}else{
						if (mCurrIdx != -1){
							//gets cursor selected by user
							Cursor cursor = (Cursor) listView.getItemAtPosition(mCurrIdx);
							//gets patient medication from cursor
							PatientMedication patientMedication = PatientMedication.getDataFromCursor(cursor);
							Log.d(TAG, "Patient medication to update:"+patientMedication.getName());
							String[] selectionArgs = { String.valueOf(patientMedication.getId()) };
							String selection = SymptomSchema.PatientMedication.Cols.ID + " = ? ";
							if (action.equals(SymptomValues.ACTION_DELETE) && patientMedication.isActive()){
								//first call server to deactivate patient medication
								svc.deletePatientMedication(Utils.getUserId(getActivity()), patient.getId(), patientMedication.getId());
								Log.d(TAG, "patient medication successfully deleted on server");
								patientMedication.setActive(false);								
								//then update state in the local database
								resolver.updatePatientMedication(patientMedication, selection, selectionArgs);	
								Log.d(TAG, "Patient medication successfully inactivated on local database");	
								pm = patientMedication;
							}else if (action.equals(SymptomValues.ACTION_UPDATE) && !patientMedication.isActive()){
								//first call server to activate patient medication
								svc.activatePatientMedication(Utils.getUserId(getActivity()), patient.getId(), patientMedication.getId());
								Log.d(TAG, "patient medication successfully activated on server");
								patientMedication.setActive(true);
								//then update state in the local database
								resolver.updatePatientMedication(patientMedication, selection, selectionArgs);	
								Log.d(TAG, "Patient medication successfully activated on local database");	
								pm = patientMedication;
							}
						}
					}
				}
				return pm;
			}
		}, new TaskCallback<PatientMedication>() {

			@Override
			public void success(PatientMedication pm) {
				if (pm != null){
					//once method finish
					if (action.equals(SymptomValues.ACTION_INSERT)){
						//empty the EditText new medication and request focus to it
						etNewMedication.setText("");
						etNewMedication.requestFocus();
					}										
					//notify adapter for changes in the local database
					adapter.notifyDataSetChanged();
				}else{
					Log.d(TAG, "Patient medication could not be updated");
				}
			}
		
			@Override
			public void error(Exception e) {
				Log.d(TAG, "error saving patient medication", e);
				Toast.makeText(getActivity(), getResources().getString(R.string.error_updating_patient_medication), Toast.LENGTH_LONG).show();
			}
		}
		);
	}
	
	/**
	 * This method shows a pop up menu with option delete and activate patient medication
	 * @param v Row associated to pop up menu
	 * @param id index row
	 */
	private void showPopup(View v, int id) {
		Log.d(TAG, "Showing popup menu at:"+id);
		mCurrIdx = id;
	    PopupMenu popup = new PopupMenu(getActivity(), v);
	    popup.setOnMenuItemClickListener(this);
	    MenuInflater inflater = popup.getMenuInflater();
	    inflater.inflate(R.menu.action_medication, popup.getMenu());
	    popup.show();
	}	
	
	/**
	 * Controls what action was selected by user from options Delete or Activate patient medication
	 */
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
        case R.id.action_delete_medication:
        	Log.d(TAG, "Action delete medication preseed");
        	setPatientMedication(SymptomValues.ACTION_DELETE);
            return true;	
        case R.id.action_activate_medication:
        	Log.d(TAG, "Action delete medication preseed");
        	setPatientMedication(SymptomValues.ACTION_UPDATE);
            return true;	    
        default:
            return false;
		}
	}
	
	/**
	 * This method creates loader to load patient medication objects from local database order by name
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "onCreateLoader() called");
		String[] projection = SymptomSchema.PatientMedication.ALL_COLUMN_NAMES;
		String selection = SymptomSchema.PatientMedication.Cols.ID_PATIENT + " = ?";
		org.coursera.symptom.orm.Patient patient = ((MedicationsActivity)getActivity()).getPatient();
		String[] selectionArgs = { String.valueOf(patient.getId()) };
		String sortOrder = SymptomSchema.PatientMedication.Cols.NAME + " ASC";
		CursorLoader cursorLoader = new CursorLoader(getActivity(), SymptomSchema.PatientMedication.CONTENT_URI, projection, selection, selectionArgs, sortOrder);		
		return cursorLoader;
	}

	/**
	 * Once loader finish to load data it calls this method
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		Log.d(TAG, "onLoadFinished() called");
		adapter.swapCursor(data);
		if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }		
	}

	/**
	 * Reset loader and empty data
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		Log.d(TAG, "onLoaderReset() called");
		 if (adapter != null) {
			 adapter.swapCursor(null);
		 }		
	}

	/**
	 * This method allows to format patient medication state to human readble values before showing it on the list
	 */
	@Override
	public boolean setViewValue(View view, Cursor cursor, int index) {
		if (index == cursor.getColumnIndex(SymptomSchema.PatientMedication.Cols.ACTIVE)){
			int active = cursor.getInt(index);
			((TextView) view).setText(active == 1?getResources().getString(R.string.active):getResources().getString(R.string.inactive));
			return true;
		}
		return false;
	}

}