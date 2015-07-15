package org.coursera.symptom.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.coursera.symptom.R;
import org.coursera.symptom.SymptomApplication;
import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.activity.LoginActivity;
import org.coursera.symptom.activity.doctor.PatientMonitorListActivity;
import org.coursera.symptom.client.SymptomSvc;
import org.coursera.symptom.client.SymptomSvcApi;
import org.coursera.symptom.orm.Checkin;
import org.coursera.symptom.orm.Patient;
import org.coursera.symptom.orm.PatientMedication;
import org.coursera.symptom.orm.SymptomResolver;
import org.coursera.symptom.utils.CameraUtils;

import retrofit.client.Response;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

/**
 * This service allows app to download data from server in a interval period of time defined by the user in their app
 * preferences. The service is started by a repeating alarm that it is defined in LoginActivity when the user log in to the server.
 * There are three possibilities of downloading data.
 * 
 * 1) Download patient medications. This is done by patient app in order to receive the updates of their medications to complete 
 * checkin process.
 * 2) Download patients' checkin information. This is done by doctor app in order to receive the new patients' checkins.
 * 3) Download checkin's photo taken by patient. In this case the service it is not started by a repeating alarm. Instead of this, 
 * this is done by doctor app when the user presses a button to see the photo in CheckinDetailActivity class and the ShowPhotoActivity 
 * is started.
 */
@SuppressLint("UseSparseArrays")
public class DownloadIntentService extends BaseService {
		
	public static final String TAG = "DownloadIntentService";
	// Notification Sound and Vibration on Arrival
	private Uri soundURI = Uri.parse("android.resource://org.coursera.symptom/" + R.raw.alarm_pain);
	private long[] mVibratePattern = { 0, 200, 200, 300 };
	
    /**
     * The default constructor for this service. Simply forwards
     * construction to IntentService, passing in a name for the Thread
     * that the service runs in.
     */
    public DownloadIntentService() { 
        super("DownloadIntentService Worker Thread"); 
    }

    /**
     * Optionally allow the instantiator to specify the name of the
     * thread this service runs in.
     */
    public DownloadIntentService(String name) {
        super(name);
    }

    /**
     * Makes an Intent for Download medications for Patient mobile app
     * @param context The context of the calling component
     * 
     * @return an Intent object to start service
     */
    private static Intent makeDownloadMedicationsIntent(Context context) {
    	Intent intent = new Intent(context, DownloadIntentService.class);
    	intent.putExtra(SymptomValues.TYPE_DOWNLOAD, SymptomValues.DOWNLOAD_MEDICATIONS);
    	return intent;
    }
    
    /**
     * Makes an Intent for Download check-ins for Doctor mobile app
     * @param context The context of the calling component
     * 
     * @return an Intent object to start service
     */
    private static Intent makeDownloadCheckinsIntent(Context context) {
    	Intent intent = new Intent(context, DownloadIntentService.class);
    	intent.putExtra(SymptomValues.TYPE_DOWNLOAD, SymptomValues.DOWNLOAD_CHECKINS);
    	return intent;
    }
    
    /**
     * Makes an Intent for Download check-ins for Doctor mobile app if role is DOCTOR
     * or for Download medications for Patient mobile app if role is PATIENT
     * 
     * @param context The context of the calling component
     * @param role user role: PATIENT or DOCTOR
     * 
     * @return an Intent object to start service
     */
	public static Intent makeDownloadIntent(Context context, String role) {
		Log.d(TAG, "makeDownloadIntent() called for role:"+role);
		if (SymptomValues.ROLE_PATIENT.equals(role)){
			return makeDownloadMedicationsIntent(context);
		}
		if (SymptomValues.ROLE_DOCTOR.equals(role)){
			return makeDownloadCheckinsIntent(context);
		}
		return null;
	}
	
	/**
	 * Makes an Intent for Download patient's photo
	 * 
	 * @param context The context of the calling component
	 * @param handler a Handler object to allow service to communicate with activity that started the service
	 * @param checkin a Checkin object that contains information to get photo from server
	 * 
	 * @return an Intent object to satart service
	 */
	public static Intent makeIntent(Context context, Handler handler, Checkin checkin){
		Messenger messenger = new Messenger(handler); //with this messenger the service we'll be able to send messages to activity
    	Intent intent = new Intent(context, DownloadIntentService.class);
    	intent.putExtra(SymptomValues.TYPE_DOWNLOAD, SymptomValues.DOWNLOAD_PICTURE);
    	intent.putExtra(SymptomValues.MESSENGER, messenger);
    	intent.putExtra(SymptomValues.CHECKIN_DATA, (Parcelable)checkin);
    	return intent;
	}

    /**
     * Method to handle the intent coming from activity. There are tree possibilities:
     * 1) Download check-ins. This is called from doctor app by a repeating alarm
     * 2) Download patient medications. This is called from patient app by a repeating alarm
     * 3) Download patient check-in photo. This is called from doctor app when doctor wants to view patient checkin photo 
     * and it has never been download
     */
    @Override
	protected void onHandleIntent (Intent intent) {
    	Log.d(TAG, "onHandleIntent() called");
    	Bundle bundle = intent.getExtras();    	
    	String typeDownloadRequest = bundle.getString(SymptomValues.TYPE_DOWNLOAD);
    	Log.d(TAG, "Calling downloadIntentService for:"+typeDownloadRequest);
    	acquireLock();
    	Log.d(TAG, "lock acquired");
    	if (checkConnectivity()){   
    		Log.d(TAG, "Internet Connectivity is ok.");
    		//object to access to the server. The method uses a saved OAuth token
    		SymptomSvcApi svc = SymptomSvc.init(SymptomValues.SERVER, getOAuthToken());
    		if (svc != null){
		    	//Who am I?
		    	long userId = getUserId();
		    	//object to access to the local database
    			SymptomResolver resolver = new SymptomResolver(DownloadIntentService.this);
    			try{
			    	if (SymptomValues.DOWNLOAD_CHECKINS.equals(typeDownloadRequest)){
			    		downloadCheckins(resolver, svc, userId);			    	
			    	}else
			    	if (SymptomValues.DOWNLOAD_MEDICATIONS.equals(typeDownloadRequest)){
			    		downloadMedications(resolver, svc, userId);		    		
			    	}
			    	if (SymptomValues.DOWNLOAD_PICTURE.equals(typeDownloadRequest)){
			    		//get messanger to comunicate back to activity
			    		Messenger messanger = (Messenger) bundle.get(SymptomValues.MESSENGER);
			    		//get checkin information
			    		Checkin checkin = bundle.getParcelable(SymptomValues.CHECKIN_DATA);
			    		//get patient from globalApp
			    		SymptomApplication sApp = (SymptomApplication)getApplicationContext();
			    		checkin.setPatient(sApp.getPatient());
			    		//Download photo an send new local path to activity
			    		sendPath(downloadPhoto(resolver, svc, checkin, userId), messanger);		    		
			    	}
    			}catch(Exception e){
    				Log.d(TAG, "Error downloading data", e);
    			}
    		}else{
				Log.d(TAG, "We can't download data from server. Login again");
				sendNotificationErrorLogin();
			}
    	}
    }
    
    /**
     * Downloads new patient medications from the server and then inserts them into local database
     * 
     * @param resolver a SymptomResolver object to insert these patient medications into local database.
     * @param svc a SymptomSvcApi object to access server to download patient medications
     * @param userId a long that represents user id that calls this method
     */
    private void downloadMedications(SymptomResolver resolver, SymptomSvcApi svc, long userId){
    	Log.d(TAG, "Download medications called");
		ArrayList<PatientMedication> list = svc.getPatientMedications(userId);
		Log.d(TAG, "Number of patient medication recover from server:"+list.size());
		if (list.size() > 0){
    		try {
    			//first delete all patient medications
				resolver.deleteAllPatientMedication();
				//insert again into local database. Normally there will be a few records to insert
				resolver.bulkInsertPatientMedication(list, new Patient(userId));
				Log.d(TAG, "medication inserted");
    		} catch (RemoteException e) {
    			Log.d(TAG, "error downloading checkins", e);
			}	
		}
    }
    
    /**
     * Downloads new doctor patient's check-ins from server and then inserts them into local database.
     * 
     * @param resolver a SymptomResolver object to insert these check-ins into local database.
     * @param svc a SymptomSvcApi object to access server to download check-ins
     * @param userId a long that represents user id that calls this method
     */
    private void downloadCheckins(SymptomResolver resolver, SymptomSvcApi svc, long userId){
    	Log.d(TAG, "Download checkins called");
		ArrayList<Checkin> list = svc.searchPatientCheckins(userId);
		Log.d(TAG, "Number of check-in recover:"+list.size());
    	if (list.size() > 0){
    		try {
    			//Insert all of them
				resolver.bulkInsertCheckin(list);
				Log.d(TAG, "checkins inserted");
				//Check if there are a checkin with alert needs
				checkDoctorNotification(list);
			} catch (RemoteException e) {
				Log.d(TAG, "error downloading checkins", e);
			}
		}
    }
    
    /**
     * Download patient check-in's photo from server and saves it in the local filesystem.
     * 
     * @param resolver a SymptomResolver object to update photo_path column on the check-in table.
     * This allow future access to this photo to local file system.  
     * @param svc a SymptomSvcApi object to access server to download the image
     * @param checkin a Checkin object that contains information to get the image from server
     * @param userId a long that represents user id that calls this method
     * 
     * @return a String with the photo path.
     */
    private String downloadPhoto(SymptomResolver resolver, SymptomSvcApi svc, Checkin checkin, long userId){
    	Log.d(TAG, "Download patient's photo called");
    	String photoPath = null;
    	try {
        	//call server to receive the image data
    		Response response = svc.getCheckinPhoto(userId, checkin.getPatient().getId(), checkin.getId());        	
    		//save file into disk
			InputStream in = response.getBody().in();			
			Log.d(TAG, "image data retrieved from server");
			//Get alumn directory to save the image
			File albumDir = CameraUtils.getAlbumDir(SymptomValues.ALBUM_NAME);
			//Photo path
			File filePhotoPath = new File(albumDir, CameraUtils.JPEG_FILE_PREFIX+checkin.getId()+CameraUtils.JPEG_FILE_SUFFIX);
			Log.d(TAG, "Image will be saved in:"+filePhotoPath);
			final OutputStream out = new FileOutputStream(filePhotoPath);	    	
			CameraUtils.copy(in, out);
			in.close();
			out.close();
			Log.d(TAG, "Image saved");
			photoPath = filePhotoPath.getPath();
		} catch (Exception e) {
			Log.d(TAG, "It was an error saving image", e);
			e.printStackTrace();
		}    	
    	//update checkin with the new file path only if process successfully finished 
    	if (photoPath != null){
    		checkin.setPhotoPath(photoPath);
    		try {
    			//update checkin's photo path column in the local database
				resolver.updateCheckinWithID(checkin);
				Log.d(TAG, "Photo path updated in local database");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	return photoPath;
    }

    /**
     * Checks if some patient's check-in has an alertDoctor with true value, in order to send a notification to alert doctor
     * 
     * @param list an ArrayList of Checkin objects
     */
	private void checkDoctorNotification(ArrayList<Checkin> list) {
		HashMap<Long, Patient> hmPatients = new HashMap<Long, Patient>(); //Patients HashMap that have been notified
		for (Checkin checkin: list){
			Patient patient;
			//Alert doctor?
			if (checkin.isAlertDoctor()){
				//Get who is the patient
				patient = checkin.getPatient();
				long patientId = patient.getId();
				//Put patient Id to hash map in order to build a list of patients to monitor
				if (hmPatients.get(patientId) == null){
					hmPatients.put(patientId, patient);
					Log.d(TAG, "Alert patient with id:"+patientId);
				}
			}
		}
		//Any patient with medical care?
		if (hmPatients.size() > 0){
			Log.d(TAG, "Number of patients with monitor needs:"+hmPatients.size());
			sendNotificationMonitorPatients(new ArrayList<Patient>(hmPatients.values()));			
		}
	}
	
	/**
	 * Sends notification to mobile device notification area to alert doctor that some patients needs medication control
	 * 
	 * @param listMonitorPatients an ArrayList of Patient objects representing patients with needs.
	 */
	private void sendNotificationMonitorPatients(ArrayList<Patient> listMonitorPatients){
		Log.d(TAG, "sendNotificationMonitorPatients called()");
		Intent patientIntent = new Intent(this, PatientMonitorListActivity.class);
		//we save this list in global app 
		SymptomApplication sApp = (SymptomApplication)getApplicationContext();
		sApp.setMonitorList(listMonitorPatients);
		PendingIntent patientPendingIntent = PendingIntent.getActivity(this, 0, patientIntent, Intent.FLAG_ACTIVITY_NEW_TASK);;
		
		Notification.Builder notificationBuilder = new Notification.Builder(this)
		.setTicker(getResources().getString(R.string.noti_monitor_patient_title))
		.setContentText(getResources().getString(R.string.noti_monitor_patient))
		.setContentTitle(getResources().getString(R.string.noti_monitor_patient_title))
		.setSmallIcon(android.R.drawable.stat_sys_warning)
		.setAutoCancel(true)
		.setSound(soundURI)
		.setVibrate(mVibratePattern)
		.setContentIntent(patientPendingIntent);
		
		// Pass the Notification to the NotificationManager:
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(MY_NOTIFICATION_ID, notificationBuilder.build());
		Log.d(TAG, "Patient monitor notification send to user");
	}
	
	/**
	 * Sends notification to mobile device notification area to alert user that it needs login again
	 * 
	 */
	private void sendNotificationErrorLogin(){
		Log.d(TAG, "sendNotificationErrorConnection called()");
		Intent patientIntent = new Intent(this, LoginActivity.class);
		//we send this list to Monitor patient list activity
		PendingIntent patientPendingIntent = PendingIntent.getActivity(this, 0, patientIntent, Intent.FLAG_ACTIVITY_NEW_TASK);;
		
		Notification.Builder notificationBuilder = new Notification.Builder(this)
		.setTicker(getResources().getString(R.string.noti_error_download_title))
		.setContentText(getResources().getString(R.string.noti_error_download))
		.setContentTitle(getResources().getString(R.string.noti_error_download_title))
		.setSmallIcon(android.R.drawable.stat_sys_warning)
		.setAutoCancel(true)
		.setContentIntent(patientPendingIntent);
		
		// Pass the Notification to the NotificationManager:
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(MY_NOTIFICATION_ID, notificationBuilder.build());
		Log.d(TAG, "Error conection notification send to user");
	}

	
	/**
	 * Sends the photo path to activity
	 *  
	 * @param outputPath a String that represents the photo path
	 * @param messenger a Messenger class that allow service to communicate to activity
	 */
	private static void sendPath (String outputPath, Messenger messenger) {
		Log.d(TAG, "sendPath called()");
		Message msg = Message.obtain();
		Bundle data = new Bundle();
		data.putString(SymptomValues.PICTURE_PATH, outputPath);
		//Make the Bundle the "data" of the Message.
		msg.setData(data);
		try {			
			messenger.send(msg); //Send the Message back to the client Activity.
			Log.d(TAG, "send Path to activity:"+outputPath);
		} catch (RemoteException e) {			
			e.printStackTrace();
		}
	}
}