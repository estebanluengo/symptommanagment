package org.coursera.symptom.activity.doctor;

import org.coursera.symptom.R;

import android.os.Bundle;
import android.view.Menu;

/**
 * This activity class shows a screen to allow doctor to manage Patient medications.
 * The activity loads a MedicationsFragment fragment
 */
public class MedicationsActivity extends BaseActivity {

	public static final String TAG = "MedicationsActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medications);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.medications, menu);
		return true;
	}
	
}