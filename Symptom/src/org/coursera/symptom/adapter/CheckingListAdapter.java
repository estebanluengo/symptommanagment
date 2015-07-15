package org.coursera.symptom.adapter;

import java.util.List;

import org.coursera.symptom.orm.Checkin;
import org.coursera.symptom.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * This Adapter is deprecated due to the fact the app uses LoaderManager and SimpleCursorAdapter to load Checkin data
 * @deprecated
 */
public class CheckingListAdapter extends ArrayAdapter<Checkin>{
	public static final String TAG = "CheckingListAdapter";
    int layoutResourceId;    
    List<Checkin> data = null;
    Context context;
    
    public CheckingListAdapter(Context _context, int _layoutResourceId, List<Checkin> _items) {
        super(_context, _layoutResourceId, _items);
        Log.d(TAG, "CheckingListAdapter constructor()");        
        context = _context;
        layoutResourceId = _layoutResourceId;
        this.data = _items;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CheckinHolder holder = null;
        
        if(row == null)
        {
        	LayoutInflater inflater = ((Activity)context).getLayoutInflater();          
            row = inflater.inflate(layoutResourceId, parent, false);            
            holder = new CheckinHolder();
            holder.tvDate = (TextView)row.findViewById(R.id.tvDateCheckingItem);
//            holder.tvTime = (TextView)row.findViewById(R.id.tvTimeCheckingItem);
            holder.tvHowbad = (TextView)row.findViewById(R.id.tvPainStop);
            holder.tvHowbad = (TextView)row.findViewById(R.id.tvHowbadCheckingItem);            
            row.setTag(holder);
        }
        else
        {
            holder = (CheckinHolder)row.getTag();
        }
        
        Checkin checkin = data.get(position);
        String checkinDate = checkin.getCheckinDate();
        holder.tvDate.setText(checkinDate.split(" ")[0]);
        holder.tvTime.setText(checkinDate.split(" ")[1]);
        holder.tvHowbad.setText(checkin.getHowbad());
        return row;
    }
        
    public void setData(List<Checkin> data) {
        clear();
        this.data = data;
        if (data != null) {
          for (int i = 0; i < data.size(); i++) {
            add(data.get(i));
          }
        }
      }
    
    static class CheckinHolder
    {
       TextView tvDate;
       TextView tvTime;
       TextView tvHowbad;
    }
}
