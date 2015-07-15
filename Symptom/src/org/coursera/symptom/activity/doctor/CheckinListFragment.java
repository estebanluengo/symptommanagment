package org.coursera.symptom.activity.doctor;

import org.coursera.symptom.ListSelectionListener;
import org.coursera.symptom.R;
import org.coursera.symptom.orm.Checkin;
import org.coursera.symptom.provider.SymptomSchema;
import org.coursera.symptom.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * This Fragment class handles the patient checkin list. The fragment uses a LoaderManager with Cursor object to retrieve
 * checkin information from local database. This is a great advantage because LoaderManager handles the data load in a very 
 * efficient way. Additionally the class implements ViewBinder to format data befere it is shown in the list.
 * 
 */
public class CheckinListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, 
																SimpleCursorAdapter.ViewBinder{	
	private static final String TAG = "CheckinListFragment";
	//LOADER ID
	private static final int LOADER_ID = 1;
	//Listener to call when doctor select a checkin row
	private ListSelectionListener mListener = null;
	//Index row selected by doctor
	private int mCurrIdx = -1;
	//ListView header. We assign this header to the fragment view programmatically
	private View header;
	//ListView where the checkin information will be shown
	private ListView listView;
	//TextView to assign the patient name
	private TextView tvPatientName;
	//SimpleCursorAdapter that contains patient medication information to be shown on list
	private SimpleCursorAdapter adapter;
	
	/**
	 * In this method we save locally a reference to parent Activity to allow this fragment to communicate with the activity
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {			
			// Set the ListSelectionListener for communicating with the Activity
			mListener = (ListSelectionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setRetainInstance(true); //We don't need this
	} 

	/**
	 * This method gets a header with patient name and inject into parent view
	 */
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		header = (View) inflater.inflate(R.layout.header_checkin_list, null);		
		tvPatientName = (TextView)header.findViewById(R.id.tvPatientName);
		View v = super.onCreateView(inflater, container, savedInstanceState);
		//we inject the view to parent
	    ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.fragment_checkin_list, container, false);
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
        org.coursera.symptom.orm.Patient patient = ((CheckinListActivity)getActivity()).getPatient();
        tvPatientName.setText(patient.getFullName());
		Log.d(TAG, "setting patient name to textview on the header");
				
		//The columns that adapter will show
		String[] columns = {
				SymptomSchema.Checkin.Cols.CHECKINDATE, SymptomSchema.Checkin.Cols.PAINSTOP, SymptomSchema.Checkin.Cols.HOWBAD
		};
		//The textViews where these columns will be shown
		int[] to = new int[] { 
				R.id.tvDateCheckingItem, R.id.tvPainStop, R.id.tvHowbadCheckingItem 
		 };
		// At the moment, set the list adapter for the ListView with empty data set
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_checkin_item, null, columns, to, 0);
		adapter.setViewBinder(this);
		setListAdapter(adapter);
		//don't show list yet, we are waiting for the results
        setListShown(false);
	}
	
	/**
	 * Loader is initialize here.
	 */
	@Override
    public void onResume(){
    	super.onResume();
    	Log.d(TAG, "onResume() called");
    	// Initialize the Loader with id '1' and callbacks 'this'. If the loader doesn't already exist, one is created. Otherwise,
        // the already created Loader is reused. In either case, the LoaderManager will manage the Loader across the Activity/Fragment
        // lifecycle, will receive any new loads once they have completed, and will report this new data back to the 'mCallbacks' object.
        LoaderManager lm = getLoaderManager();
        //Ups! http://stackoverflow.com/questions/11293441/android-loadercallbacks-onloadfinished-called-twice
        if (getLoaderManager().getLoader(LOADER_ID) == null) {
        	Log.d(TAG, "Initializing the new Loader...");
        } else {
        	Log.d(TAG, "Reconnecting with existing Loader (id '1')...");
        }	        
        lm.initLoader(LOADER_ID, null, this);
    }
	
	/**
	 * This method returns Checkin object that corresponds with row selected
	 * @return a Checkin object or null if no row is selected
	 */
	private Checkin getPressedItem(){
		Checkin checkin = null;
		if (-1 != mCurrIdx){
			Cursor cursor = (Cursor) listView.getItemAtPosition(mCurrIdx);
			checkin = Checkin.getDataFromCursor(cursor);
			listView.setItemChecked(mCurrIdx, true);			
		}
		return checkin;
	}
	
	/**
	 * This method is called when the user selects an item from the List
	 */
	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {
		Log.d(TAG, "onListItemClick() called");
		if (mCurrIdx != pos) {
			mCurrIdx = pos;			
			// Inform the Activity that the item in position pos has been selected
			Checkin checkin = getPressedItem();
			if (checkin != null){
				mListener.onListSelection(mCurrIdx, checkin);
			}
		}
	}

	/**
	 * This method creates loader to load checkin objects from local database order by checkin create date
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "onCreateLoader() called");
		String[] projection = SymptomSchema.Checkin.ALL_COLUMN_NAMES;
		String selection = SymptomSchema.Checkin.Cols.ID_PATIENT + " = ?";
		org.coursera.symptom.orm.Patient patient = ((CheckinListActivity)getActivity()).getPatient();
		String[] selectionArgs = { String.valueOf(patient.getId()) };
		String sortOrder = "date("+SymptomSchema.Checkin.Cols.CHECKINDATE + ") DESC";
		CursorLoader cursorLoader = new CursorLoader(getActivity(), SymptomSchema.Checkin.CONTENT_URI, projection, selection, selectionArgs, sortOrder);		
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
		getPressedItem();			
	}

	/**
	 * Reset loader and empty data
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		 Log.d(TAG, "onLoaderReset() called");
		 if (adapter != null) {
			 adapter.swapCursor(null);
		 }
	}

	/**
	 * This method allows to format date before showing it on the list
	 */
	@Override
	public boolean setViewValue(View view, Cursor cursor, int index) {
		if (index == cursor.getColumnIndex(SymptomSchema.Checkin.Cols.CHECKINDATE)){
			String checkinDate = cursor.getString(index);
			((TextView) view).setText(Utils.convertDatetoString(checkinDate));
			return true;
		}
		return false;
	}
}
