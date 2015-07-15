package org.coursera.symptom.activity.patient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Callable;

import org.coursera.symptom.R;
import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.activity.PreferenceActivity;
import org.coursera.symptom.client.CallableTask;
import org.coursera.symptom.client.TaskCallback;
import org.coursera.symptom.orm.PatientMedication;
import org.coursera.symptom.orm.SymptomResolver;
import org.coursera.symptom.services.SendIntentService;
import org.coursera.symptom.utils.CameraUtils;
import org.coursera.symptom.utils.CheckinQuestion;
import org.coursera.symptom.utils.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * This Activity class allow patient to make a new Checkin. The Activity shows a Wizard with all questions that
 * needs to be answered. Once the user finish the wizard, the activity starts a new Service to send all check-in
 * information answered by user and maybe a Photo taken to server. This is made to allow user to turn off the app and
 * not to wait until all data has been successfully sent it.
 *
 */
public class CheckinActivity extends Activity implements OnClickListener, OnDateSetListener, OnTimeSetListener{

	public static final String TAG = "CheckinActivity";
	private int questionIndex = 0;  //what question is showing now?
	private ArrayList<CheckinQuestion> wizard; //data structure with all questions and possible answers
	private int numberOfMedications; //number of pain medications that user has got assigned
	private TextView tvQuestion;  //TextView with question that is showed in every wizard step.
	private Button btOption1, btOption2, btOption3, btBack;
	private DialogFragment fragmentTime; //A frament to show TimePicker
	private DialogFragment fragmentDate; //A fragment to show A DatePicker
	private int noOfTimesDatePickerCalled = 0; //WTF! https://code.google.com/p/android/issues/detail?id=64895
	private int noOfTimesTimePickerCalled = 0;
	private CameraUtils cameraUtils; //an Object to manage photo taken by patient
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");
		setContentView(R.layout.activity_checkin);
		tvQuestion = (TextView)findViewById(R.id.tvQuestion);
		btOption1 = (Button)findViewById(R.id.btOption1);
		btOption2 = (Button)findViewById(R.id.btOption2);
		btOption3 = (Button)findViewById(R.id.btOption3);
		btBack = (Button)findViewById(R.id.btBack);
		btOption1.setOnClickListener(this);
		btOption2.setOnClickListener(this);
		btOption3.setOnClickListener(this);		
		btBack.setOnClickListener(this);		
		//Time and date Picker fragments to get time and date taking medications 
		fragmentTime = new TimePickerFragment();
		fragmentDate = new DatePickerFragment();
		//built a Camera Utils object to handle take photos
		cameraUtils = new CameraUtils(CheckinActivity.this, CameraUtils.getAlgumFactory(), SymptomValues.ALBUM_NAME);
		
		if (savedInstanceState == null) { //we arrive from NextCheckinActiviy
			Log.d(TAG,"called from activity");
			//we need to find patient medications to build the wizard
			findMedications();
		}else{
			Log.d(TAG, "loading state from Bundle");
			questionIndex = savedInstanceState.getInt(SymptomValues.CHECKIN_INDEX_QUESTION);
			wizard = savedInstanceState.getParcelableArrayList(SymptomValues.WIZARD);
			numberOfMedications = savedInstanceState.getInt(SymptomValues.NUMBER_OF_MEDICATIONS);
			Log.d(TAG, "questionIndex retrieved with value:"+questionIndex);
			showQuestion();
		}						
	}
	
	/**
	 * Finds pain medications from the local database in a Background process
	 */
	private void findMedications() {
		Log.d(TAG, "findMedications() called");
		//an object to access local database
		final SymptomResolver resolver = new SymptomResolver(CheckinActivity.this);
		CallableTask.invoke(new Callable<ArrayList<PatientMedication>>() {

			@Override
			public ArrayList<PatientMedication> call() throws Exception {
				//query local database for active medications
				return resolver.getActivePatientMedication();
			}
		}, new TaskCallback<ArrayList<PatientMedication>>() {

			@Override
			public void success(ArrayList<PatientMedication> list) {
				if (list.size() == 0){
					Log.d(TAG, "There are not pain medications");
					//Activity can not be shown because there are no patient medications
					Toast.makeText(CheckinActivity.this, getResources().getString(R.string.error_find_medications), Toast.LENGTH_LONG).show();	
					finish();
				}else{
					Log.d(TAG, "Sucess finding medications");
					buildWizard(list);
					showQuestion();
				}
			}
		
			@Override
			public void error(Exception e) {
				Log.d(TAG, "error finding medications", e);
				//Activity can not be shown because there was an error
				Toast.makeText(CheckinActivity.this, getResources().getString(R.string.error_find_medications), Toast.LENGTH_LONG).show();
				finish();
			}
		}
		);
	}
	
	/**
	 * This method builds the wizard with all possible check-in questions and holds the future responses.
	 * The wizard that has been built is saved in wizard class member
	 *  
	 * @param list ArrayList of PatientMedication. We need this list in order to make question:
	 * Did your take your medicationX? 
	 * @see org.coursera.symptom.utils.CheckinQuestion
	 */
	private void buildWizard(ArrayList<PatientMedication> list){
		Log.d(TAG, "buildWizard called");
		wizard = new ArrayList<CheckinQuestion>(list.size()+3);
		CheckinQuestion q = new CheckinQuestion(getResources().getString(R.string.question_howbad),
				getResources().getString(R.string.option_wellcontrolled), 
				getResources().getString(R.string.option_moderate),
				getResources().getString(R.string.option_severe), false);
		wizard.add(q);
		q = new CheckinQuestion(getResources().getString(R.string.question_takemedication),
				getResources().getString(R.string.option_yes),
				getResources().getString(R.string.option_no),
				null,false);
		wizard.add(q);
		numberOfMedications = list.size(); //this property will be useful to jump to last question if patient did not take his/her medication
		for (PatientMedication pm: list){
			q = new CheckinQuestion(getResources().getString(R.string.question_pre_takemedication) + " " + pm.getName() + "?",
					getResources().getString(R.string.option_yes), 
					getResources().getString(R.string.option_no),
					null,true); //set to true to show Date Time Pickers
			q.setMedicationId(pm.getId()); //we save medicationId in order to save CheckinMedication into the database
			wizard.add(q);
		}
		q = new CheckinQuestion(getResources().getString(R.string.question_painstop),
				getResources().getString(R.string.option_no),
				getResources().getString(R.string.option_some),
				getResources().getString(R.string.option_icannoteat), false);
		wizard.add(q);
		q = new CheckinQuestion(getResources().getString(R.string.question_takephoto),
				getResources().getString(R.string.option_yes), 
				getResources().getString(R.string.option_no),
				null,false);		
		wizard.add(q);
	}

	/**
	 * This method shows current question into the screen. To do this, the method uses questionIndex and wizard
	 * structure
	 */
	private void showQuestion() {
		Log.d(TAG, "showQuestion called. Showing question number:"+questionIndex);
		CheckinQuestion q = wizard.get(questionIndex); //get current question
		tvQuestion.setText(q.getQuestion());
		btOption1.setText(q.getOption1());
		btOption2.setText(q.getOption2());
		if (q.getOption3() != null){
			//this question has a third option
			btOption3.setText(q.getOption3());
			btOption3.setVisibility(View.VISIBLE);
		}else{
			btOption3.setVisibility(View.INVISIBLE);
		}				
		if (questionIndex == 0){ //first question
			btBack.setVisibility(View.INVISIBLE);
		}else{
			btBack.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * This method controls navigation between questions
	 */
	@Override
	public void onClick(View button) {
		int id = button.getId();
		CheckinQuestion q = wizard.get(questionIndex);
		//the user answers first option
		if (id == R.id.btOption1){
			q.setAnswer(1);
			//show date and time picker if question is did you take your medicationX?
			if (q.isDateTimeRequired()){
				Log.d(TAG, "Showing Date and time picker");
				noOfTimesDatePickerCalled = 0; //reset values to control this issue: https://code.google.com/p/android/issues/detail?id=64895
				noOfTimesTimePickerCalled = 0;
				fragmentDate.show(getFragmentManager(), "Date medication");
				return; //we don't want to showQuestion until user answers date and time picker
			}
			questionIndex++;
			//last question answer yes to take photo
			if (questionIndex == wizard.size()){
				if (!showCamara(q)){
					Toast.makeText(CheckinActivity.this, getResources().getString(R.string.error_setting_camera), Toast.LENGTH_SHORT).show();
					questionIndex--;
				}else{
					Log.d(TAG, "It finishes the activity waiting for the result");
					return;
				}
			}
		//the user answers second option					
		}else if (id == R.id.btOption2){
			q.setAnswer(2);								
			if (questionIndex == 1){ //the patient did not take his/her medication. Jumping to next to last question
				questionIndex += numberOfMedications+1;				
			}else{
				questionIndex++;
			}
			//last question answers no
			if (questionIndex == wizard.size()){
				q.setPhotoPath(null); //patient does not want to take any photo
				Log.d(TAG, "The user does not want to take any photo");
				finishWizard();
				return;
			}
		//the user answers third option					
		}else if (id == R.id.btOption3){
			q.setAnswer(3);			
			questionIndex++;	
		//the user presses back button on wizard						
		}else if (id == R.id.btBack){
			//it's the next to last question and he/she did not take his/her medication, jump to second question
			if ((questionIndex+2) == wizard.size() && wizard.get(1).getAnswer() == 2){ 
				questionIndex -= numberOfMedications+1;
			}else{
				questionIndex--;
			}
		}
		showQuestion();
	}
	
	/**
	 * Shows the camara to take some photo to send to doctor
	 */
	private boolean showCamara(CheckinQuestion q){
		Log.d(TAG, "showCamara called!!");
		//We create the intent to start the camera
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File photoFile;
		try {
			//Get the path where photo will be saved
			photoFile = cameraUtils.setUpPhotoFile();
			String photoPath = photoFile.getPath();
			//save this path into CheckinQuestion to send later to server
			q.setPhotoPath(photoPath);
		} catch (IOException e) {
			Log.d(TAG, "it was an error setting photo file", e);
			return false;
		}
		takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		Log.d(TAG, "starting taking photo activity for result");
		startActivityForResult(takePhotoIntent, SymptomValues.ACTION_TAKE_PHOTO);		
		return true;
	}
	
	/**
	 * This method starts the SentIntentService to send check-in information to the server 
	 * and photo if the user took the photo
	 */
	private void finishWizard(){
		Log.d(TAG, "starting service");
		Intent sendIntent = SendIntentService.makeSendIntent(CheckinActivity.this, wizard);
		startService(sendIntent);
		Log.d(TAG, "Service SendIntent started in order to send check-in to the server");
		Toast.makeText(CheckinActivity.this, getResources().getString(R.string.thank_you), Toast.LENGTH_LONG).show();
		finish();
	}
	
	/**
	 * This method is called when the camera finishes its activity. If the user accepts the photo, then the wizard
	 * is finished.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult called.");
		if (resultCode == Activity.RESULT_OK){
			//The user accepts the photo that was taken
			if (SymptomValues.ACTION_TAKE_PHOTO == requestCode) {
				cameraUtils.galleryAddPic(); //Add photo to gallery
				Log.d(TAG, "Taking Photo ok");
				finishWizard();
				return;
			}
		}
		//if we are here, then the user did not accept their photo or something wrong happens with the camera
		Log.d(TAG, "Photo is not good");
		questionIndex--;
		//show question again Did you want to take a photo()
		showQuestion();
	}
	
	/**
	 * Method called from datePicker to set calendar date for saving into CheckinQuestion
	 */
	public void onDateSet(DatePicker view, int year, int month, int day) {
		if(noOfTimesDatePickerCalled%2==0){ //https://code.google.com/p/android/issues/detail?id=64895
			CheckinQuestion q = wizard.get(questionIndex);
			q.setDateTakeMedication(Utils.getFormatDate(year, ++month, day)); //save date selected			
			fragmentTime.show(getFragmentManager(), "Time of medication");
		}
		noOfTimesDatePickerCalled++;
		fragmentDate.dismiss();
	}
	
	/**
	 * Method called from timePicker to set time for saving into CheckinQuestion
	 */
	public void onTimeSet(TimePicker view, int hourOfDay, int minute){
		if(noOfTimesTimePickerCalled%2==0){ //https://code.google.com/p/android/issues/detail?id=64895
			CheckinQuestion q = wizard.get(questionIndex);
			q.setTimeTakeMedication(Utils.getFormatHour(hourOfDay,minute)); //save time selected
			questionIndex++;  //jumping to following question
			showQuestion();
		}
		noOfTimesTimePickerCalled++;
		fragmentTime.dismiss();		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "onSaveInstanceState called");
		outState.putInt(SymptomValues.CHECKIN_INDEX_QUESTION, questionIndex);
		Log.d(TAG, "questionIndex saved with value:"+questionIndex);
		outState.putParcelableArrayList(SymptomValues.WIZARD, wizard);
		Log.d(TAG, "Wizard saved");		
		outState.putInt(SymptomValues.NUMBER_OF_MEDICATIONS, numberOfMedications);		
		Log.d(TAG, "numberOfMedications saved with value:"+numberOfMedications);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(CheckinActivity.this, PreferenceActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	/**
	 * DialogFragment that shows datePicker
	 *
	 */
	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);			
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			Log.d(TAG, "onDateSet called");
			((OnDateSetListener) getActivity()).onDateSet(view, year, month, day);
		}
	}

	/**
	 * DialogFragment that shows timePicker
	 *
	 */
	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);			
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Log.d(TAG, "onTimeSet called");
			((OnTimeSetListener) getActivity()).onTimeSet(view, hourOfDay, minute);
		}
	}
		
}