package org.coursera.symptom.activity.doctor;

import java.io.File;
import java.lang.ref.WeakReference;

import org.coursera.symptom.R;
import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.orm.Checkin;
import org.coursera.symptom.services.DownloadIntentService;
import org.coursera.symptom.utils.CameraUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.GestureDetector;

/**
 * This Activity class shows the Checkin photo that has been send by the patient.
 * The activity uses a service DownloadIntentService in order to download photo in background
 * and communicates with the service with a handler to receive the path where the photo was
 * saved locally. This is all done in background. 
 * When the photo is saved locally next time it will be loaded from disk.
 *
 */
public class ShowPhotoActivity extends Activity {

	public static final String TAG = "ShowPhotoActivity";
	public static final int MODE_DRAG = 1;
	public static final int MODE_PINCH = 2;
	//Checkin object where the photo was taken
	private Checkin checkin;
	//ImageView where the photo will be displayed
	private ImageView ivPatientPhoto;
	//class that allow activity to receive picture path from service
	private MessengerHandler handler = new MessengerHandler(this);
	//class that manage load from disk and draw the image into the imageView
	private CameraUtils cameraUtils;
	//Use a ScaleGestureDetector to scale the image
	private ScaleGestureDetector sgd;
	//Use a GestureDetector to scroll the image
	private GestureDetector gd;
	//Matrix to make transformations to the image
	private Matrix transMatrix = new Matrix();
	float lastFocusX;
	float lastFocusY;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate() called");
		setContentView(R.layout.activity_show_photo);
		ivPatientPhoto = (ImageView)findViewById(R.id.ivPatientPhoto);
		Bundle extra = this.getIntent().getExtras();
		if (savedInstanceState == null){
			Log.d(TAG, "called from activity");
			checkin = (Checkin)extra.getParcelable(SymptomValues.CHECKIN_DATA);
			Log.d(TAG, "loading picture");			
		}else{
			Log.d(TAG, "loading state from Bundle");
			checkin = (Checkin)savedInstanceState.getParcelable(SymptomValues.CHECKIN_DATA);
		}
		//Instantiate the ScheleGestureDetector and GestureDetector
		sgd = new ScaleGestureDetector(this,new ScaleGestureListener());
		gd = new GestureDetector(this, new GestureListener());
		//we instantiate the object to manage the image loaded from disk
		cameraUtils = new CameraUtils(ShowPhotoActivity.this, ivPatientPhoto, CameraUtils.getAlgumFactory(), SymptomValues.ALBUM_NAME);		
	}
	
	/**
	 * This method is called on every touch event
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		//up the responsibility to ScheduleGestureDetector
	    sgd.onTouchEvent(ev);
	    gd.onTouchEvent(ev);
	    return true;
	}
	
	/**
	 * Shows the photo that is saved in photoPath on ImageView. This method is called
	 * when the photo is loaded from disk on this activity
	 * 
	 * @param photoPath a String with photo path in local file system
	 */
	private void displayPhoto(String photoPath){
		//we inform to CameraUtils the path where the image is saved
		cameraUtils.setCurrentPhotoPath(photoPath);
		//we can draw the image into ImageView when ImageView let us do it 
		ivPatientPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw(){
				Log.d(TAG, "imageView onPreDraw called!!");
				//disable the listener in order to deactivate notifications constantly
				ivPatientPhoto.getViewTreeObserver().removeOnPreDrawListener(this);						
				cameraUtils.drawPhoto();
				return true;
			}
		});
	}
	
	/**
	 * Shows photo that is saved in photoPath on ImageView. This method is called
	 * when the photo is loaded from Server via a Service
	 * 
	 * @param photoPath a String with photo path in local file system
	 */
	private void displayPhotoFromService(String photoPath){
		checkin.setPhotoPath(photoPath);
		//we inform to CameraUtils the path where the image is saved
		cameraUtils.setCurrentPhotoPath(photoPath);
		//we can draw the image into ImageView when ImageView let us do it 
		cameraUtils.drawPhoto();		
	}
	
	/**
	 * This method load the photo from disk or starts service to download in case the photo
	 * has not been saved yet
	 */
	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG,"onResume() called");
		if (checkin != null){
			String picturePath = checkin.getPhotoPath();
			//verify if this path exists
			File filePhotoPath = new File(picturePath);
			if (filePhotoPath.exists()){
				Log.d(TAG, "File exits. Load from local disk");				
				displayPhoto(filePhotoPath.getPath());												
			}else{
				Toast.makeText(ShowPhotoActivity.this, getResources().getString(R.string.loading_photo), Toast.LENGTH_SHORT).show();				
				Log.d(TAG, "File does not exit. Load from Internet");
				//Call service to download the image and save it in local file system.				
				Intent intentService = DownloadIntentService.makeIntent(ShowPhotoActivity.this, handler, checkin);						
				startService(intentService);
				Log.d(TAG, "Service called");
			}
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "onSaveInstanceState called!!");        
		outState.putParcelable(SymptomValues.CHECKIN_DATA, (Parcelable)checkin);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		cameraUtils.recycleBitmap();		
	}
	
	/**
	 * When this activity finish its work it returns the photo path to CheckinDetailActivity activity
	 */
	@Override
	public void finish() {
	  // Prepare data intent 
	  Intent data = new Intent();
	  data.putExtra(SymptomValues.PICTURE_PATH, checkin.getPhotoPath());
	  // Activity finished ok, return the data
	  setResult(RESULT_OK, data);
	  super.finish();
	} 
	
	/**
	 * Handler to receive the message from service with the photo path
	 *
	 */
	static class MessengerHandler extends Handler {
	    
    	// A weak reference to the enclosing class
    	WeakReference<ShowPhotoActivity> outerClass;
    	
    	/**
    	 * A constructor that gets a weak reference to the enclosing class.
    	 * We do this to avoid memory leaks during Java Garbage Collection.
    	 * 
    	 * @see https://groups.google.com/forum/#!msg/android-developers/1aPZXZG6kWk/lIYDavGYn5UJ
    	 */
    	public MessengerHandler(ShowPhotoActivity outer) {
            outerClass = new WeakReference<ShowPhotoActivity>(outer);
    	}
    	
    	// Handle any messages that get sent to this Handler
    	@Override
		public void handleMessage(Message msg) {
    		
            // Get an actual reference to the ShowPhotoActivity from the WeakReference.
            final ShowPhotoActivity activity = outerClass.get();
    		
            // If ShowPhotoActivity hasn't been garbage collected (closed by user), display the sent image.
            if (activity != null) {
            	Bundle bundle = msg.getData();
            	String pathImage = (String) bundle.get(SymptomValues.PICTURE_PATH);
            	if (pathImage != null && !pathImage.equals("")){       
            		//call to display photo
            		activity.displayPhotoFromService(pathImage);
            	}else{
            		Toast.makeText(activity, activity.getResources().getString(R.string.error_download_image), Toast.LENGTH_SHORT).show();
            	}
            }
    	}
    }
	
	//A listener for scale gestures
	private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		
		@Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            lastFocusX = detector.getFocusX();
            lastFocusY = detector.getFocusY();
            return true;

        }
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			Matrix transformationMatrix = new Matrix();
		    float focusX = detector.getFocusX();
		    float focusY = detector.getFocusY();
		    //Zoom focus is where the fingers are centered, 
		    transformationMatrix.postTranslate(-focusX, -focusY);
		    transformationMatrix.postScale(detector.getScaleFactor(), detector.getScaleFactor());
		    //Adding focus shift to allow for scrolling with two pointers down. 
		    float focusShiftX = focusX - lastFocusX;
		    float focusShiftY = focusY - lastFocusY;
		    transformationMatrix.postTranslate(focusX + focusShiftX, focusY + focusShiftY);
		    transMatrix.postConcat(transformationMatrix);
		    lastFocusX = focusX;
		    lastFocusY = focusY;
		    ivPatientPhoto.setImageMatrix(transMatrix);
		    ivPatientPhoto.invalidate();
		    return true;						
		}
	}
	
	private class GestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {		
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			transMatrix.postTranslate(-distanceX, -distanceY);
			ivPatientPhoto.setImageMatrix(transMatrix);
		    return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent arg0) {
			ivPatientPhoto.setImageMatrix(new Matrix());
			ivPatientPhoto.invalidate();
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent arg0) {
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent arg0) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
			
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}
	}

}