package org.coursera.symptom.services;

import org.coursera.symptom.SymptomValues;

import android.app.IntentService;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

/**
 * This class represents a Base service class for Symptoms services. It have a common methods 
 * for all services
 * 
 */
public abstract class BaseService extends IntentService {

	public static final String TAG = "BaseService";
	protected static final int MY_NOTIFICATION_ID = 1;
	
	//https://developer.android.com/training/scheduling/wakelock.html#cpu
	private WakeLock mWakeLock;
	
	public BaseService(String name) {
		super(name);
	}

	@Override
    public void onDestroy(){
    	super.onDestroy();
    	releaseWakeLock(); //it is a must
    }
    
    /**
     * This method checks connectivity availability 
     * 
     * @return true if there is connectivity and false otherwise
     */
    protected boolean checkConnectivity(){    	
    	// check the global background data setting
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] nInfo = cm.getAllNetworkInfo();
        if (nInfo != null){
            for (int i = 0; i < nInfo.length; i++) 
                if (nInfo[i].getState() == NetworkInfo.State.CONNECTED){
                	Log.d(TAG, "Connectivity available");
                	return true;
                }
        }
    	Log.d(TAG, "Connectivity is not available");
        return false;
    }
    
    /**
     * This method acquire WakeLock in order to complete the work before the device goes to sleep.
     */
    protected void acquireLock(){
        //obtain the wake lock
        Log.d(TAG, "acquiring wakelock");
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mWakeLock.acquire();                
        Log.d(TAG, "acquiring wakelock");    	
    }
    
    /**
     * This method release the WakeLock. You have to call this method before the service finishes
     */
    protected void releaseWakeLock(){
    	if (mWakeLock != null){    		
    		mWakeLock.release();
    		Log.d(TAG, "releasing wakelock");
    	}
    }
    
    /**
     * Returns userId that is using this application. This information is read from SharedPreferences and it is
     * saved by LoginActivity when the user login to server
     * 
     * @return a long representing userId
     */
    protected long getUserId(){
    	final SharedPreferences prefs = getSharedPreferences(SymptomValues.SYMPTOM_PREFERENCES, MODE_PRIVATE);
		long userId = prefs.getLong(SymptomValues.ID_USER, 0);
		return userId;
    }
    
    /**
     * Returns the token saved in the app private preferences by the Login activity
     * 
     * @return a String that represents OAuth token
     */
    protected String getOAuthToken(){
    	final SharedPreferences prefs = getSharedPreferences(SymptomValues.SYMPTOM_PREFERENCES, MODE_PRIVATE);
    	String token = prefs.getString(SymptomValues.OAUTH_TOKEN, "");
    	return token;
    }
}
