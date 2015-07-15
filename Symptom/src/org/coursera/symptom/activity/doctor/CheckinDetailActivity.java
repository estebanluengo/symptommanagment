package org.coursera.symptom.activity.doctor;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.coursera.symptom.R;
import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.client.CallableTask;
import org.coursera.symptom.client.TaskCallback;
import org.coursera.symptom.orm.Checkin;
import org.coursera.symptom.orm.CheckinMedication;
import org.coursera.symptom.orm.PatientMedication;
import org.coursera.symptom.orm.SymptomResolver;
import org.coursera.symptom.utils.Utils;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This Activity class shows a specific Patient Checkin information send by Patient. This information
 * contains the answers to checkin questions and maybe a photo taken by the patient. 
 *
 */
public class CheckinDetailActivity extends BaseActivity {

	public static final String TAG = "CheckinDataActivity";
	//Checkin object send by CheckinListActivity. This object does not contain Checkin Medication information.
	private Checkin checkin;
	private LinearLayout llTakeMedicationControl, llMain;
	//Texts views on screen
	private TextView tvPatientName, tvDateCheckin, tvHowBad, tvTakeMedication, tvPainStop;
	//Table layout where we write Checkin medication programmatically 
	private TableLayout tableLayout;
	private TableLayout.LayoutParams tableLayoutParams;
	private TableRow.LayoutParams tableRowMedicationParams, tableRowMedicationControlParams;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"Calling onCreate()");
		setContentView(R.layout.activity_checkin_detail);
		//Get all visual components		
		tvPatientName = (TextView)findViewById(R.id.tvPatientName);
		tvDateCheckin = (TextView)findViewById(R.id.tvCheckinDate);		
		llMain = (LinearLayout) findViewById(R.id.llCheckinDetail);
		llTakeMedicationControl = (LinearLayout) findViewById(R.id.llTakeMedicationControl);
		tableLayoutParams = new TableLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		tableLayoutParams.setMargins(0, (int) getResources().getDimension(R.dimen.field_top_margin), 0, 0);
		tableRowMedicationParams = new TableRow.LayoutParams();
		tableRowMedicationParams.setMargins(0, 6, 3, 6);
		tableRowMedicationParams.gravity = Gravity.CENTER;
		tableRowMedicationControlParams = new TableRow.LayoutParams();
		tableRowMedicationControlParams.setMargins(6, 6, 6, 6);
		tableRowMedicationControlParams.gravity = Gravity.END;
	    tableLayout = new TableLayout(this);
		tvHowBad = (TextView)findViewById(R.id.tvHowBad);
		tvPainStop = (TextView)findViewById(R.id.tvPainStop);
		tvTakeMedication = (TextView)findViewById(R.id.tvTakeMedication);
		Button btViewPhoto = (Button)findViewById(R.id.btViewPhoto);		
		btViewPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Shows a photo taken by patient in another activity
				showPhoto();
				
			}
		});
		Bundle extra = this.getIntent().getExtras();
		if (savedInstanceState == null){
			Log.d(TAG, "called from activity");
			checkin = (Checkin)extra.getParcelable(SymptomValues.CHECKIN_DATA);
			//we set patient object to checkin. This will not be saved on disk
			checkin.setPatient(getPatient());
			//In order to show checkin information, we need first to load checkin medication from local database 
			loadCheckinMedication();
		}else{
			Log.d(TAG, "loading state from Bundle");
			checkin = (Checkin)savedInstanceState.getParcelable(SymptomValues.CHECKIN_DATA);
			//we set patient object to checkin. This will not be saved on disk
			checkin.setPatient(getPatient());
			//showing checkin information
			showCheckinData();
		}		
	}
	
	/**
	 * Loads check-in medication data from local database in background
	 */
	private void loadCheckinMedication(){
		Log.d(TAG, "loadCheckinMedication() called");		
		final SymptomResolver resolver = new SymptomResolver(CheckinDetailActivity.this);
		CallableTask.invoke(new Callable<ArrayList<CheckinMedication>>() {
			@Override
			public ArrayList<CheckinMedication> call() throws Exception {
				//getting check-in medication objects from database related to checkin-in id
				ArrayList<CheckinMedication> cmList = resolver.getCheckinMedicationViaCheckinID(checkin.getId());
				//getting all patient's medication from database 
				ArrayList<PatientMedication> pmList = resolver.getPatientMedicationViaPatientID(checkin.getPatient().getId());
				if (cmList != null && pmList != null){
					Log.d(TAG, "Number of patient medications:"+pmList.size());
					//filling patient medication objects into check-in medication objects
					for (CheckinMedication cm: cmList){
						Long pmId = cm.getPatientMedication().getId();
						cm.setPatientMedication(getPatientMedication(pmId, pmList));
					}
				}
				return cmList;
			}
			/**
			 * Returns patient medication object from pmList whose id is pmId
			 * @param pmId patient medication id we are looking for
			 * @param pmList ArrayList of PatientMedication object
			 * @return a PatientMedication object
			 */
			private PatientMedication getPatientMedication(Long pmId, ArrayList<PatientMedication> pmList) {				
				for (PatientMedication pm: pmList){
					if (pmId.compareTo(pm.getId()) == 0){
						Log.d(TAG, "found patient medication object in local database with id:"+pmId);
						return pm;
					}
				}
				return null;
			}
		}, new TaskCallback<ArrayList<CheckinMedication>>() {

			@Override
			public void success(ArrayList<CheckinMedication> list) {
				//we save patient medication list into checkin object. Maybe it is null
				checkin.setCheckinMedicationList(list);
				Log.d(TAG, "Number of checkin medications recovered:"+list);
				showCheckinData();
			}
		
			@Override
			public void error(Exception e) {
				Log.d(TAG, "error getting check-in medication list", e);
				Toast.makeText(CheckinDetailActivity.this, getResources().getString(R.string.error_get_checkinmedication_list), Toast.LENGTH_LONG).show();
				finish();
			}
		});
	}
	
	/**
	 * Shows Check-in data on screen
	 */
	private void showCheckinData(){
		tvPatientName.setText(checkin.getPatient().getFullName());
		tvDateCheckin.setText(Utils.convertDatetoString(checkin.getCheckinDate()));		
		tvHowBad.setText(checkin.getHowbad());
		tvPainStop.setText(checkin.getPainstop());
		tvTakeMedication.setText(checkin.isTakemedication()?SymptomValues.YES_OPTION:SymptomValues.NO_OPTION);
		
		//Check-in medication information is displayed dinamically 
		ArrayList<CheckinMedication> list = checkin.getCheckinMedicationList();
		if (checkin.isTakemedication() && list != null && list.size() > 0){			
			llTakeMedicationControl.setVisibility(View.VISIBLE);
		    for (CheckinMedication cm: list){
			    tableLayout.addView(getTableRowMedicationName(cm.getPatientMedication()));
			    tableLayout.addView(getTableRowTakeMedicationControl(cm));	
			}		    		    
		    llMain.addView(tableLayout, tableLayoutParams);
		}else{
			llTakeMedicationControl.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * Builds a table row that contains Medication name
	 * @param pm
	 * @return a TableRow object
	 */
	private TableRow getTableRowMedicationName(PatientMedication pm){
		TableRow tableRow = new TableRow(this);
		TextView tvMedication = new TextView(this);
	    tvMedication.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) 15.0);		
	    tvMedication.setTypeface(null, Typeface.BOLD);
	    if (pm != null && pm.getName() != null){
	    	tvMedication.setText(pm.getName());
	    }else{
	    	tvMedication.setText(getResources().getString(R.string.unknown));
	    }
	    tvMedication.setLayoutParams(tableRowMedicationParams);
	    tableRow.addView(tvMedication);
	    return tableRow;
	}

	/**
	 * Builds a table row that contains check-in medication information
	 * 
	 * @param cm a CheckinMedication object with the information to be displayed
	 * @return a TableRow object
	 */
	private TableRow getTableRowTakeMedicationControl(CheckinMedication cm){
		TableRow tableRow = new TableRow(this);
		
		TextView tvLabelTakeIt = new TextView(this);
		tvLabelTakeIt.setText(getResources().getString(R.string.label_takeit));
		tvLabelTakeIt.setLayoutParams(tableRowMedicationControlParams);
		tableRow.addView(tvLabelTakeIt);
		
		TextView tvTakeIt = new TextView(this);
		tvTakeIt.setText(cm.isTakeit()?SymptomValues.YES_OPTION:SymptomValues.NO_OPTION);
		tvTakeIt.setLayoutParams(tableRowMedicationControlParams);
		tableRow.addView(tvTakeIt);
		
		if (cm.isTakeit()){
			TextView tvLabelDateTakeIt = new TextView(this);
			tvLabelDateTakeIt.setText(getResources().getString(R.string.label_date_medication));
			tvLabelDateTakeIt.setLayoutParams(tableRowMedicationControlParams);
			tableRow.addView(tvLabelDateTakeIt);
		
			TextView tvDateTakeIt = new TextView(this);
			tvDateTakeIt.setText(Utils.convertDatetoString(cm.getTakeitDate(), cm.getTakeitTime()));
			tvDateTakeIt.setLayoutParams(tableRowMedicationControlParams);
			tableRow.addView(tvDateTakeIt);		
		}
		return tableRow;
	}
	
	/**
	 * This method is called when the activity ShowPhotoActivity finish its work. It is needed to
	 * update Photo path on Checkin object.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    if (requestCode == SymptomValues.ACTION_TAKE_PHOTO) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	            Bundle bundle = data.getExtras();	  
	            checkin.setPhotoPath(bundle.getString(SymptomValues.PICTURE_PATH));
	        }
	    }
	}
	
	/**
	 * Starts new activity for result in order to show patient's photo. We need to know checkin photo path 
	 * where photo was saved in mobile local file system to update the Checkin object. 
	 */
	private void showPhoto(){
		Log.d(TAG, "showPhoto() called");
		if (checkin.getPhotoPath() != null){
			Intent intent = new Intent(CheckinDetailActivity.this, ShowPhotoActivity.class);
			intent.putExtra(SymptomValues.CHECKIN_DATA, (Parcelable)checkin);
			Log.d(TAG,"sending checkin with id:"+checkin.getId()+" to ShowPhotoActivity");					
			startActivityForResult(intent, SymptomValues.ACTION_TAKE_PHOTO);
		}else{
			Log.d(TAG, "No photo sent this time");
			Toast.makeText(CheckinDetailActivity.this, getResources().getString(R.string.no_photo), Toast.LENGTH_SHORT).show();			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.doctor, menu);
		return true;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "onSaveInstanceState called!!");        
		outState.putParcelable(SymptomValues.CHECKIN_DATA, (Parcelable)checkin);
	}
}