package org.coursera.symptom.activity.doctor;

import java.util.ArrayList;

import org.coursera.symptom.R;
import org.coursera.symptom.orm.Checkin;
import org.coursera.symptom.provider.SymptomSchema;
import org.joda.time.DateTime;
import org.joda.time.Period;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

/**
 * This activity class presents a graph with patient's pain evolution. The class uses 
 * a LoaderManager with Cursor object to retrieve Checkin information from local database.
 * The Loader gets only checkins that have beend created since 96 hours and analyze the checkin information
 * to show the graph. The activity can show two graphs. Once that represents mouth pain question with
 * values: "Well-controlled, moderate, severe" and another that represents pain stop from eating
 * with values: "I can not eat, no, some"
 * 
 * The class uses a library called GraphView http://android-graphview.org/ to paint the information.
 */
public class MonitorActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>{
	public static final String TAG = "MonitorActivity";	
	//LOADER ID
	private static final int LOADER_ID = 1;
	//Graph that represents pain stop from eating
	private GraphView gvEatPain;
	//Graph that represents mouth pain
	private GraphView gvMouthPain;
	//LinearLoyout where the graph is shown. Only once graph can be shown at a time.
	private LinearLayout llPainEvolution;
	//Controls if gvMouthPain Graph is visible
	private boolean isMouthPainVisible;
	//Buton to change the graph that is visible
	private Button btViewGraph;
	//Contains a serie data for gvMouthPain graph
	private GraphViewSeries mouthPainSeries;
	//Contains a serie data for gvEatPain graph
	private GraphViewSeries eatPainSeries;
	
	/**
	 * Constructs graphs with empy data
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"Calling onCreate()");
		setContentView(R.layout.activity_monitor);
		TextView tvPatientName = (TextView)findViewById(R.id.tvPatientName);
		tvPatientName.setText(getPatient().getFullName());
		final GraphView.GraphViewData[] graphData = new GraphView.GraphViewData[] {
		};
		mouthPainSeries = new GraphViewSeries(graphData);
		eatPainSeries = new GraphViewSeries(graphData);
		
		gvMouthPain = new LineGraphView(this, getResources().getString(R.string.graph_mouth_pain_title));
		gvMouthPain.setHorizontalLabels(new String[] {
				getResources().getString(R.string.four_days), 
				getResources().getString(R.string.three_days), 
				getResources().getString(R.string.two_days), 
				getResources().getString(R.string.one_day)
				});
		gvMouthPain.setVerticalLabels(new String[] {
				getResources().getString(R.string.option_severe), 
				getResources().getString(R.string.option_moderate), 
				getResources().getString(R.string.option_wellcontrolled)
				});	
		gvMouthPain.addSeries(mouthPainSeries);
		gvMouthPain.setManualYMaxBound(2.0);
		gvMouthPain.setManualYMinBound(0);
		gvMouthPain.getGraphViewStyle().setNumHorizontalLabels(3);
		gvMouthPain.getGraphViewStyle().setNumVerticalLabels(4);
		gvMouthPain.getGraphViewStyle().setTextSize(getResources().getDimension(R.dimen.small));
		Log.d(TAG, "Mouth pain mouth graph has been built");	
		llPainEvolution = (LinearLayout) findViewById(R.id.llPainEvolution);
		llPainEvolution.addView(gvMouthPain);
		Log.d(TAG, "adding pain mouth graph to linear layout");	
		isMouthPainVisible = true;
			 
		gvEatPain = new LineGraphView(this, getResources().getString(R.string.graph_eat_pain_title));
		gvEatPain.setHorizontalLabels(new String[] {
				getResources().getString(R.string.four_days), 
				getResources().getString(R.string.three_days), 
				getResources().getString(R.string.two_days), 
				getResources().getString(R.string.one_day)
				});
		gvEatPain.setVerticalLabels(new String[] {
				getResources().getString(R.string.option_icannoteat), 
				getResources().getString(R.string.option_some),
				getResources().getString(R.string.option_no) 
				});	
		gvEatPain.addSeries(eatPainSeries);
		gvEatPain.setManualYMaxBound(2.0);
		gvEatPain.setManualYMinBound(0);
		gvEatPain.getGraphViewStyle().setTextSize(getResources().getDimension(R.dimen.small));
		Log.d(TAG, "Eat pain eat graph has been built");
		
		btViewGraph = (Button)findViewById(R.id.btViewGraph);
		OnClickListener on = new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				showGraph();			
			}
		};
		btViewGraph.setOnClickListener(on);		
	}
	
	/**
	 * This method shows mouthPain graph or eatPain graph depending on isMouthPainVisible value
	 */
	private void showGraph(){
		if (isMouthPainVisible){
			Log.d(TAG, "adding pain eat graph to linear layout");
			llPainEvolution.removeView(gvMouthPain);
			llPainEvolution.addView(gvEatPain);					
			isMouthPainVisible = false;
			btViewGraph.setText(getResources().getString(R.string.view_graph_mouth_pain));
		}else{
			Log.d(TAG, "adding pain mouth graph to linear layout");
			llPainEvolution.removeView(gvEatPain);
			llPainEvolution.addView(gvMouthPain);
			isMouthPainVisible = true;
			btViewGraph.setText(getResources().getString(R.string.view_graph_eat_pain));
		}	
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
	 * This method creates loader to load checkin objects from local database order by checkin date.
	 * The loader will only get the checkins that have been created since 96 hours
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "onCreateLoader() called");
		String[] projection = SymptomSchema.Checkin.ALL_COLUMN_NAMES;

		String selection = SymptomSchema.Checkin.Cols.ID_PATIENT + " = ? and "
				+ " date("+SymptomSchema.Checkin.Cols.CHECKINDATE + ") >= ?"; 		
		
		DateTime time96 = DateTime.now().minusHours(96); //96 hours ago		
		String[] selectionArgs = { String.valueOf(getPatient().getId()) , String.valueOf(time96.getMillis()) };		
		
		String sortOrder = "date("+SymptomSchema.Checkin.Cols.CHECKINDATE + ") ASC";
		
		CursorLoader cursorLoader = new CursorLoader(MonitorActivity.this, SymptomSchema.Checkin.CONTENT_URI, projection, 
				selection, selectionArgs, sortOrder);		
		return cursorLoader;
	}

	/**
	 * Once loader finish to load data it calls this method. The method analyze all checkins to build the serie of data
	 * to be shown in every graph.
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.d(TAG, "onLoadFinished() called");
		//get checkin list from cursor
		ArrayList<Checkin> checkinList = Checkin.getArrayListFromCursor(cursor);
		//GraphViewData[] to save the information 
		GraphView.GraphViewData[] graphMouthData = new GraphView.GraphViewData[4];
		GraphView.GraphViewData[] graphEatData = new GraphView.GraphViewData[4];
		DateTime checkinDate;
		String howbad, painstop;
		//The method sums the number of howbad values in every period of the four possibles: 4 days, 3 days, 2 days and 1 day
		int[] howbadScores = new int[]{0, 0, 0, 0};				
		//The method sums the number of pain stop values in every period of the four possibles: 4 days, 3 days, 2 days and 1 day
		int[] painstopScores = new int[]{0, 0, 0, 0};
		//It saves how many times how bad question was answered in every period
		int[] countHowbadScores = new int[]{0, 0, 0, 0};
		//It saves how many times pain stop question was answered in every period
		int[] countPainstopScores = new int[]{0, 0, 0, 0};		
		int days;
		DateTime now = DateTime.now();
		//For every checkin
		for (Checkin checkin: checkinList){
			//Gets checkin date
			checkinDate = new DateTime(checkin.getCheckinDate());
			howbad = checkin.getHowbad();
			painstop = checkin.getPainstop();
			//how many days since the checkin was created
			days = new Period(checkinDate, now).getDays();			
			if (days >= 3){
				howbadScores[0] += getHowBadScore(howbad);
				countHowbadScores[0]++;
				painstopScores[0] += getPainStopScore(painstop);
				countPainstopScores[0]++;
			}else if (days >= 2){
				howbadScores[1] += getHowBadScore(howbad);
				countHowbadScores[1]++;
				painstopScores[1] += getPainStopScore(painstop);
				countPainstopScores[1]++;
			}else if (days >= 1){
				howbadScores[2] += getHowBadScore(howbad);
				countHowbadScores[2]++;
				painstopScores[2] += getPainStopScore(painstop);
				countPainstopScores[2]++;
			}else{
				howbadScores[3] += getHowBadScore(howbad);
				countHowbadScores[3]++;
				painstopScores[3] += getPainStopScore(painstop);
				countPainstopScores[3]++;
			}			
		}
		//finally we put avarege points in every period to represents the graph
		int score1 = countHowbadScores[0] != 0?(howbadScores[0] / countHowbadScores[0]):0;
		int score2 = countHowbadScores[1] != 0?(howbadScores[1] / countHowbadScores[1]):score1;
		int score3 = countHowbadScores[2] != 0?(howbadScores[2] / countHowbadScores[2]):score2;
		int score4 = countHowbadScores[3] != 0?(howbadScores[3] / countHowbadScores[3]):score3;
		graphMouthData[0] = new GraphView.GraphViewData(1, score1);
		graphMouthData[1] = new GraphView.GraphViewData(2, score2);
		graphMouthData[2] = new GraphView.GraphViewData(3, score3);
		graphMouthData[3] = new GraphView.GraphViewData(4, score4);
		mouthPainSeries.resetData(graphMouthData);
		score1 = countPainstopScores[0] != 0?(painstopScores[0] / countPainstopScores[0]):0;
		score2 = countPainstopScores[1] != 0?(painstopScores[1] / countPainstopScores[1]):score1;
		score3 = countPainstopScores[2] != 0?(painstopScores[2] / countPainstopScores[2]):score2;
		score4 = countPainstopScores[3] != 0?(painstopScores[3] / countPainstopScores[3]):score3;
		graphEatData[0] = new GraphView.GraphViewData(1, score1); 
		graphEatData[1] = new GraphView.GraphViewData(2, score2);
		graphEatData[2] = new GraphView.GraphViewData(3, score3);
		graphEatData[3] = new GraphView.GraphViewData(4, score4);
		eatPainSeries.resetData(graphEatData);		
	}
	
	/**
	 * This method returns a score for how bad question. It returns 2 points if answer was Severe. 1 point if
	 * it was moderate and 0 points if it was well-controlled.
	 * 
	 * @param howbad a String that contains the answer for how bad question, It can be "Well-controlled, severe or moderate"
	 * @return a int with the points
	 */
	private int getHowBadScore(String howbad){
		if (howbad.equals(getResources().getString(R.string.option_severe))){
			return 2;
		}else if (howbad.equals(getResources().getString(R.string.option_moderate))){
			return 1;
		}else if (howbad.equals(getResources().getString(R.string.option_wellcontrolled)))
			return 0;
		return 0;			
	}
	
	/**
	 * This method returns a score for pain stop question. It returns 2 points if answer was I can no eat. 1 point if
	 * it was no and 0 points if it was some
	 * 
	 * @param painstop a String that contains the answer for pain stpop question, It can be "I can no eat, no or some"
	 * @return a int with the points
	 */
	private int getPainStopScore(String painstop){
		if (painstop.equals(getResources().getString(R.string.option_icannoteat))){
			return 2;
		}else if (painstop.equals(getResources().getString(R.string.option_some))){
			return 1;
		}else if (painstop.equals(getResources().getString(R.string.option_no))){
			return 0;
		}
		return 0;			
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.d(TAG, "onLoaderReset() called");				
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.monitor, menu);
		return true;
	}
}