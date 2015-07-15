package org.coursera.symptom.orm;

import java.io.Serializable;
import java.util.ArrayList;

import org.coursera.symptom.provider.SymptomSchema;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * a Bean that contains a patients check-in's medications information
 */
public class CheckinMedication implements Serializable, Parcelable {
    private static final long serialVersionUID = 1L;
    
    private Long id; 
    private boolean takeit;
    private String takeitDate;
    private String takeitTime;    
    private Checkin checkin;
    private PatientMedication patientMedication;

    public CheckinMedication() {
    }

    public CheckinMedication(Long id) {
        this.id = id;
    }

    public CheckinMedication(Long id, boolean takeit, String takeitDate, String takeitTime) {
        this.id = id;
        this.takeit = takeit;
        this.takeitDate = takeitDate;
        this.takeitTime = takeitTime;
    }
    
    public CheckinMedication(Long id, boolean takeit, String takeitDate, String takeitTime, long checkinId, long patientMedicationId) {
        this.id = id;
        this.takeit = takeit;
        this.takeitDate = takeitDate;
        this.takeitTime = takeitTime;
        this.checkin = new Checkin(checkinId);
        this.patientMedication = new PatientMedication(patientMedicationId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
	
    public boolean isTakeit() {
        return takeit;
    }

    public void setTakeit(boolean takeit) {
        this.takeit = takeit;
    }

    public String getTakeitDate() {
        return takeitDate;
    }

    public void setTakeitDate(String takeitDate) {
        this.takeitDate = takeitDate;
    }
    
    public String getTakeitTime() {
        return takeitTime;
    }

    public void setTakeitTime(String takeitTime) {
        this.takeitTime = takeitTime;
    }

    public Checkin getCheckin() {
        return checkin;
    }

    public void setCheckin(Checkin checkin) {
        this.checkin = checkin;
    }
    
    public PatientMedication getPatientMedication() {
        return patientMedication;
    }

    public void setPatientMedication(PatientMedication patientMedication) {
        this.patientMedication = patientMedication;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CheckinMedication)) {
            return false;
        }
        CheckinMedication other = (CheckinMedication) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.coursera.symptom.beans.CheckinMedication[ id=" + id + " ]";
    }

    /**
     * Builds a ContentValues object using the properties of this object
     * @return a ContentValues object
     */
    public ContentValues getContentValues() {
		ContentValues rValue = new ContentValues();
		rValue.put(SymptomSchema.CheckinMedication.Cols.ID, getId());
		rValue.put(SymptomSchema.CheckinMedication.Cols.TAKEIT, isTakeit()?1:0);
		rValue.put(SymptomSchema.CheckinMedication.Cols.TAKEITDATE, getTakeitDate());
		rValue.put(SymptomSchema.CheckinMedication.Cols.TAKEITTIME, getTakeitTime());		
		rValue.put(SymptomSchema.CheckinMedication.Cols.ID_CHECKIN, getCheckin().getId());
		rValue.put(SymptomSchema.CheckinMedication.Cols.ID_PATIENTMEDICATION, getPatientMedication().getId());
		return rValue;
	}

    /**
     * Gets all information from cursor and returns an ArrayList of CheckinMedication objects
     * 
     * @param cursor a Cursor object that is mapped to the local database
     * @return a ArrayList<CheckinMedication>
     */
	public static ArrayList<CheckinMedication> getArrayListFromCursor(Cursor cursor) {
		ArrayList<CheckinMedication> rValue = new ArrayList<CheckinMedication>();
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					rValue.add(getDataFromCursor(cursor));
				} while (cursor.moveToNext() == true);
			}
		}
		return rValue;
	}

	/**
	 * This method creates a new CheckinMedication object getting the data from cursor object
	 * 
	 * @param cursor a Cursor object that is mapped to the local database
	 * @return a CheckinMedication object with data read from cursor
	 */
	public static CheckinMedication getDataFromCursor(Cursor cursor) {
		long rowID = cursor.getLong(cursor.getColumnIndex(SymptomSchema.CheckinMedication.Cols.ID));
		boolean taketit = cursor.getInt(cursor.getColumnIndex(SymptomSchema.CheckinMedication.Cols.TAKEIT)) == 1?true:false;
		String takeitDate = cursor.getString(cursor.getColumnIndex(SymptomSchema.CheckinMedication.Cols.TAKEITDATE));
		String takeitTime = cursor.getString(cursor.getColumnIndex(SymptomSchema.CheckinMedication.Cols.TAKEITTIME));		
		long checkinId = cursor.getLong(cursor.getColumnIndex(SymptomSchema.CheckinMedication.Cols.ID_CHECKIN));
		long patientMedicationId = cursor.getLong(cursor.getColumnIndex(SymptomSchema.CheckinMedication.Cols.ID_PATIENTMEDICATION));
		return new CheckinMedication(rowID,taketit,takeitDate,takeitTime,checkinId,patientMedicationId);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * This method is invoked when Android needs to save state to disk or needs to send the object
	 * to other activity or service.
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeInt(takeit?1:0);
		dest.writeString(takeitDate);
		dest.writeString(takeitTime);
		if (patientMedication != null){
			patientMedication.writeToParcel(dest, flags);
		}
	}
	
	/**
	 * This method is invoked when Android needs to create a new CheckinMedication object reading the state from disk 
	 * or from Bundle. 
	 */
	public static final Parcelable.Creator<CheckinMedication> CREATOR = new Parcelable.Creator<CheckinMedication>() {
		public CheckinMedication createFromParcel(Parcel in) {
			return new CheckinMedication(in);
		}

		public CheckinMedication[] newArray(int size) {
			return new CheckinMedication[size];
		}
	};
	
	private CheckinMedication(Parcel in) {
		this.id = in.readLong();
		this.takeit = in.readInt() == 1?true:false;
		this.takeitDate = in.readString();
		this.takeitTime = in.readString();    
	    this.patientMedication = PatientMedication.CREATOR.createFromParcel(in);
	}
    
}
