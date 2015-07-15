package org.coursera.symptom.services;

import java.io.File;
import java.util.ArrayList;

import org.coursera.symptom.R;
import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.client.SymptomSvc;
import org.coursera.symptom.client.SymptomSvcApi;
import org.coursera.symptom.orm.Checkin;
import org.coursera.symptom.orm.SymptomResolver;
import org.coursera.symptom.utils.CameraUtils;
import org.coursera.symptom.utils.CheckinQuestion;

import retrofit.mime.TypedFile;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This service allows the app to send a new patient's check-in information to the server and save 
 * this information in the local database. This service is started by CheckinActivity when the user
 * finishes their checkin process.
 * The service dies once it has completed its work.
 * 
 */
public class SendIntentService extends BaseService{

	public static final String TAG = "SendIntentService";
	
	public SendIntentService() {
		super("SendIntentService Worker Thread"); 
	}
	
	public SendIntentService(String name) {
		super(name);
	}
	
	/**
	 * Makes an Intent in order to start the service later.
	 * 
	 * @param context The context of the calling component
	 * @param wizard an ArrayList<CheckinQuestion> that contains all answers from checkin process
	 * 
	 * @return an Intent object
	 */
	public static Intent makeSendIntent(Context context, ArrayList<CheckinQuestion> wizard) {
    	Intent intent = new Intent(context, SendIntentService.class);
    	intent.putParcelableArrayListExtra(SymptomValues.WIZARD, wizard);
    	return intent;
    }
	
	/**
	 * This method is called when the activity starts the service
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent() called");
		if (intent != null){
			acquireLock();
			Log.d(TAG, "lock acquired");
			if (checkConnectivity()){	
	    		Log.d(TAG, "Internet Connectivity is ok.");
	    		//Getting object to access to the server. The method uses a saved OAuth token
	    		SymptomSvcApi svc = SymptomSvc.init(SymptomValues.SERVER, getOAuthToken());
				if (svc != null){
					//Get CheckinQuestion list
					ArrayList<CheckinQuestion> list = intent.getParcelableArrayListExtra(SymptomValues.WIZARD);
					//What is my UserId?
		    		long userId = getUserId();
		    		//Convert Wizard to Checkin object
		    		Checkin checkin = Checkin.createCheckin(list, userId);
		    		Log.d(TAG, "Object checkin created from QuestionsWizard");
		    		Checkin serverCheckin = null;
		    		try {
		    			//sends the checkin object to the server
		    			serverCheckin = svc.createCheckin(userId, checkin);
		    			Log.d(TAG, "Checkin send to server");
		    		}catch (Exception e) {
						Log.d(TAG, "Error sending checkin to the server", e);						
						sendErrorNotification(); //Send error notification to user
						return;
					}
		    		//Did the patient take a photo for this checkin?
		    		if (checkin.getPhotoPath() != null){
			    		try {				    		
				    		Log.d(TAG, "Sending photo to server");
				    		svc.sendCheckinPhoto(userId, serverCheckin.getId(), new TypedFile(CameraUtils.CONTENT_TYPE, new File(checkin.getPhotoPath())));
				    		Log.d(TAG, "Photo successfully send");
				    		
			    		}catch (Exception e) {
							Log.d(TAG, "Error sending photo to the server", e);
							//it does not matter if photo can not be sent to server. The important thing is check-in data
			    		}
		    		}
		    		//object to access to the local database
					SymptomResolver resolver = new SymptomResolver(SendIntentService.this);	    		
		    		try {
		    			Log.d(TAG, "Inserting checkin into local database");
						resolver.insert(serverCheckin);
					} catch (Exception e) {
						Log.d(TAG, "Error inserting checkin in local database. It does not matter.", e);
						//it does not matter if check-in data can not be saved locally. The important thing is sent check-in data to the server
					}		    		
				}
			}else{
				Log.d(TAG, "We can't send data to server");
				//Send error notification to user
				sendErrorNotification();
			}
		}
	}
	
	/**
	 * This method sends a notification error to device's notification area in case of error
	 */
	private void sendErrorNotification(){
		Notification.Builder notificationBuilder = new Notification.Builder(this)
		.setTicker(getResources().getString(R.string.noti_error_checkin_title))
		.setContentText(getResources().getString(R.string.noti_error_checkin))
		.setContentTitle(getResources().getString(R.string.noti_error_checkin_title))
		.setSmallIcon(android.R.drawable.stat_sys_warning)
		.setAutoCancel(true);
		// Pass the Notification to the NotificationManager:
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(MY_NOTIFICATION_ID, notificationBuilder.build());
		Log.d(TAG, "Error notification send to user");
	}

}