package org.coursera.symptom.activity.doctor;

import java.util.List;

import org.coursera.symptom.ListSelectionListener;
import org.coursera.symptom.R;
import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.orm.Checkin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

/**
 * This Activity class shows a Patient checkin list. From this Activity the doctor can press a Checkin row to
 * see Checkin information. The activity loads a CheckinListFragment fragment
 *
 */
public class CheckinListActivity extends BaseActivity implements ListSelectionListener{

	public static final String TAG = "CheckinListActivity";
	
	protected static List<Checkin> checkingList;	
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"Calling onCreate()");
		setContentView(R.layout.activity_checkin_list);
	}

	/**
	 * This method is called when doctor press on a row.
	 */
	@Override
	public void onListSelection(int index, Checkin checkin) {
		Log.d(TAG, "onListSelection() called at index:"+index);
		if (checkin == null){
			Log.d(TAG, "Ups. Check-in object is null");
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_data_row), Toast.LENGTH_SHORT).show();
		}else{
			//We call CheckinDatailActivity with checkin object
			Intent intent = new Intent(CheckinListActivity.this, CheckinDetailActivity.class);			
			intent.putExtra(SymptomValues.CHECKIN_DATA, (Parcelable)checkin);
			Log.d(TAG,"sending checkin with id:"+checkin.getId()+" to CheckinActivity");					
			startActivity(intent);
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.doctor, menu);
		return true;
	}
	
}