package org.coursera.symptom.orm;

import java.io.Serializable;
import java.util.ArrayList;

import org.coursera.symptom.provider.SymptomSchema;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * A bean that contains Patients' medication information.
 */
public class PatientMedication implements Serializable, Parcelable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private boolean send;
    private boolean active;
    private Patient patient;

    public PatientMedication() {
    }

    public PatientMedication(Long id) {
        this.id = id;
    }

    public PatientMedication(Long id, Long serverId,String name, boolean send, boolean active) {
        this.id = id;
        this.name = name;
        this.send = send;
        this.active = active;
    }
    
    public PatientMedication(Long id, String name, boolean send, boolean active, long patientId) {
        this.id = id;
        this.name = name;
        this.send = send;
        this.active = active;
        this.patient = new Patient(patientId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
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
        if (!(object instanceof PatientMedication)) {
            return false;
        }
        PatientMedication other = (PatientMedication) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.coursera.symptom.beans.PatientMedication[ id=" + id + " ]";
    }

    /**
     * Builds a ContentValues object using the properties of this object
     * @return a ContentValues object
     */
	public ContentValues getContentValues() {
		ContentValues rValue = new ContentValues();
		rValue.put(SymptomSchema.PatientMedication.Cols.ID, getId());
		rValue.put(SymptomSchema.PatientMedication.Cols.NAME, getName());
		rValue.put(SymptomSchema.PatientMedication.Cols.SEND, isSend()?1:0);
		rValue.put(SymptomSchema.PatientMedication.Cols.ACTIVE, isActive()?1:0);
		rValue.put(SymptomSchema.PatientMedication.Cols.ID_PATIENT, getPatient().getId());
		return rValue;
	}

	/**
     * Gets all information from cursor and returns an ArrayList of PatientMedication objects
     * 
     * @param cursor a Cursor object that is mapped to the local database
     * @return a ArrayList<PatientMedication>
     */
	public static ArrayList<PatientMedication> getArrayListFromCursor(Cursor cursor) {
		ArrayList<PatientMedication> rValue = new ArrayList<PatientMedication>();
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
	 * This method creates a new PatientMedication object getting the data from cursor object
	 * 
	 * @param cursor a Cursor object that is mapped to the local database
	 * @return a PatientMedication object with data read from cursor
	 */
	public static PatientMedication getDataFromCursor(Cursor cursor) {
		long rowID = cursor.getLong(cursor.getColumnIndex(SymptomSchema.PatientMedication.Cols.ID));
		String name = cursor.getString(cursor.getColumnIndex(SymptomSchema.PatientMedication.Cols.NAME));
		boolean send = cursor.getInt(cursor.getColumnIndex(SymptomSchema.PatientMedication.Cols.SEND)) == 1?true:false;
		boolean active = cursor.getInt(cursor.getColumnIndex(SymptomSchema.PatientMedication.Cols.ACTIVE)) == 1?true:false;
		long patientId = cursor.getLong(cursor.getColumnIndex(SymptomSchema.PatientMedication.Cols.ID_PATIENT));
		return new PatientMedication(rowID,name,send,active,patientId);
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
		dest.writeString(name);
		dest.writeInt(send?1:0);
		dest.writeInt(active?1:0);		
	}
	
	/**
	 * This method is invoked when Android needs to create a new PatientMedication object reading the state from disk 
	 * or from Bundle.  
	 */
	public static final Parcelable.Creator<PatientMedication> CREATOR = new Parcelable.Creator<PatientMedication>() {
		public PatientMedication createFromParcel(Parcel in) {
			return new PatientMedication(in);
		}

		public PatientMedication[] newArray(int size) {
			return new PatientMedication[size];
		}
	};
	
	protected PatientMedication(Parcel in){
		this.id = in.readLong();
	    this.name = in.readString();
	    this.send = in.readInt() == 1?true:false;
	    this.active = in.readInt() == 1?true:false;
	}
}
