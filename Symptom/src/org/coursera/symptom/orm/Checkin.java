package org.coursera.symptom.orm;

import java.io.Serializable;
import java.util.ArrayList;

import org.coursera.symptom.provider.SymptomSchema;
import org.coursera.symptom.utils.CheckinQuestion;
import org.coursera.symptom.utils.Utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * a Beand that contains a patients' check-ins information.
 */
public class Checkin implements Serializable, Parcelable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String checkinDate;
    private boolean send;
    private String howbad;
    private String painstop;
    private boolean takemedication;
    private boolean alertDoctor;
    private String photoPath;
    private ArrayList<CheckinMedication> checkinMedicationList;
    private Patient patient;
    
    public Checkin() {
    	this.send = false;
    }

    public Checkin(Long id) {
        this.id = id;
        this.send = false;
    }

    public Checkin(Long id, String checkinDate, boolean send, String howbad, String painstop, boolean takemedication) {
        this.id = id;
        this.checkinDate = checkinDate;
        this.send = send;
        this.howbad = howbad;
        this.painstop = painstop;
        this.takemedication = takemedication;
    }
    
    public Checkin(Long id, String checkinDate, String howbad, String painstop, boolean takemedication, String photoPath, Long patientId) {
        this.id = id;
        this.checkinDate = checkinDate;
        this.howbad = howbad;
        this.painstop = painstop;
        this.takemedication = takemedication;
        this.photoPath = photoPath;
        this.patient = new Patient(patientId);
    }
    
    public Checkin(Long id, String checkinDate, boolean send, String howbad, String painstop, boolean takemedication, String photoPath,
    		Long patientId) {
        this.id = id;
        this.checkinDate = checkinDate;
        this.send = send;
        this.howbad = howbad;
        this.painstop = painstop;
        this.takemedication = takemedication;
        this.photoPath = photoPath;
        this.patient = new Patient(patientId);
    }

    /**
     * Only for prototype
     * 
     * @param date
     * @param time
     * @param howbad
     */
    public Checkin(String date, String time, String howbad) {
    	this.checkinDate = date + " " + time;
		this.howbad = howbad;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(String checkinDate) {
        this.checkinDate = checkinDate;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public String getHowbad() {
        return howbad;
    }

    public void setHowbad(String howbad) {
        this.howbad = howbad;
    }

    public String getPainstop() {
        return painstop;
    }

    public void setPainstop(String painstop) {
        this.painstop = painstop;
    }

    public boolean isTakemedication() {
        return takemedication;
    }

    public void setTakemedication(boolean takemedication) {
        this.takemedication = takemedication;
    }

    public boolean isAlertDoctor() {
		return alertDoctor;
	}

	public void setAlertDoctor(boolean alertDoctor) {
		this.alertDoctor = alertDoctor;
	}
	
	public String getPhotoPath() {
		return photoPath;
	}
	
	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public ArrayList<CheckinMedication> getCheckinMedicationList() {
        return checkinMedicationList;
    }

    public void setCheckinMedicationList(ArrayList<CheckinMedication> checkinMedicationList) {
        this.checkinMedicationList = checkinMedicationList;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    /**
     * Add new CheckinMedication object to to CheckinMedication list
     * @param cm CheckinMedication object
     */
    public void addCheckinMedication(CheckinMedication cm){
    	if (this.checkinMedicationList == null){
    		this.checkinMedicationList = new ArrayList<CheckinMedication>();
    	}
    	this.checkinMedicationList.add(cm);
    }
    
    /**
     * This method creates a new Checkin from a list of CheckinQuestion. Typically this 
     * method is invoked when the patient finishes the check-in process and the information 
     * needs to be sent to the server.
     * 
     * @param list a List<CheckinQuestion> object. Contains all the answers that patients did along
     * the Check-in process. 
     * @param userId the user Id that executes this method
     * @return the new Checkin object created
     */
    public static Checkin createCheckin(ArrayList<CheckinQuestion> list, long userId){
		Checkin checkin = new Checkin();
		checkin.setPatient(new Patient(userId));		
		checkin.setCheckinDate(Utils.getCurrentStringDate());
		checkin.setHowbad(list.get(0).getTextAnswer()); 
		checkin.setTakemedication(list.get(1).getAnswer() == 1?true:false);
		checkin.setPainstop(list.get(list.size()-2).getTextAnswer());
		CheckinQuestion cq;
		if (checkin.isTakemedication()){
			//The user takes his/her medications
			CheckinMedication cm;			
			PatientMedication pm;
			//for every check-in question: Did your take your medicationX?
			for (int i=2;i<(list.size()-2);i++){
				cq = list.get(i);
				cm = new CheckinMedication();
				cm.setTakeit(cq.getAnswer()== 1?true:false);
				cm.setTakeitDate(cq.getDateTakeMedication());
				cm.setTakeitTime(cq.getTimeTakeMedication());
				pm = new PatientMedication(cq.getMedicationId());
				cm.setPatientMedication(pm);
				checkin.addCheckinMedication(cm);
			}
		}
		cq = list.get(list.size()-1);
		checkin.setPhotoPath(cq.getPhotoPath());
		return checkin;
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
        if (!(object instanceof Checkin)) {
            return false;
        }
        Checkin other = (Checkin) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.coursera.symptom.beans.Checkin[ id=" + id + " ]";
    }

    /**
     * Builds a ContentValues object using the properties of this object
     * @return a ContentValues object
     */
    public ContentValues getContentValues() {
		ContentValues rValue = new ContentValues();
		rValue.put(SymptomSchema.Checkin.Cols.ID, getId());
		rValue.put(SymptomSchema.Checkin.Cols.HOWBAD, getHowbad());
		rValue.put(SymptomSchema.Checkin.Cols.PAINSTOP, getPainstop());
		rValue.put(SymptomSchema.Checkin.Cols.CHECKINDATE, getCheckinDate());
		rValue.put(SymptomSchema.Checkin.Cols.SEND, isSend()?1:0);
		rValue.put(SymptomSchema.Checkin.Cols.TAKEMEDICATION, isTakemedication()?1:0);
		rValue.put(SymptomSchema.Checkin.Cols.PHOTO_PATH, getPhotoPath());
		rValue.put(SymptomSchema.Checkin.Cols.ID_PATIENT, getPatient().getId());
		return rValue;
	}

    /**
     * Gets all information from cursor and returns an ArrayList of Checkin objects
     * 
     * @param cursor a Cursor object that is mapped to the local database
     * @return a ArrayList<Checkin>
     */
	public static ArrayList<Checkin> getArrayListFromCursor(Cursor cursor) {
		ArrayList<Checkin> rValue = new ArrayList<Checkin>();
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
	 * This method creates a new Checkin object getting the data from cursor object
	 * 
	 * @param cursor a Cursor object that is mapped to the local database
	 * @return a Checkin object with data read from cursor
	 */
	public static Checkin getDataFromCursor(Cursor cursor) {
		long rowID = cursor.getLong(cursor.getColumnIndex(SymptomSchema.Checkin.Cols.ID));
		String howbad = cursor.getString(cursor.getColumnIndex(SymptomSchema.Checkin.Cols.HOWBAD));
		String painstop = cursor.getString(cursor.getColumnIndex(SymptomSchema.Checkin.Cols.PAINSTOP));
		String checkinDate = cursor.getString(cursor.getColumnIndex(SymptomSchema.Checkin.Cols.CHECKINDATE));			
		boolean send = cursor.getInt(cursor.getColumnIndex(SymptomSchema.Checkin.Cols.SEND)) == 1?true:false;
		boolean takemedication =  cursor.getInt(cursor.getColumnIndex(SymptomSchema.Checkin.Cols.TAKEMEDICATION)) == 1?true:false;
		String photoPath = cursor.getString(cursor.getColumnIndex(SymptomSchema.Checkin.Cols.PHOTO_PATH));
		long patientId = cursor.getLong(cursor.getColumnIndex(SymptomSchema.Checkin.Cols.ID_PATIENT));		
		return new Checkin(rowID, checkinDate, send, howbad, painstop, takemedication, photoPath, patientId);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * This method is invoked when Android needs to save state to disk or needs to send the object
	 * to other activity or service. For security reasons the patient information is not persisted. 
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(checkinDate);
		dest.writeInt(send?1:0);
		dest.writeString(howbad);
		dest.writeString(painstop);
		dest.writeInt(takemedication?1:0);
		dest.writeInt(alertDoctor?1:0);
		dest.writeString(photoPath);
		if (checkinMedicationList != null){
			dest.writeInt(checkinMedicationList.size());
			for (CheckinMedication cm: checkinMedicationList){
				cm.writeToParcel(dest, flags);
			}
		}else{
			dest.writeInt(0);
		}
//		if (patient != null){  //this object is not write to disk due security reasons
//			patient.writeToParcel(dest, flags);  
//		}
	}
	
	/**
	 * This method is invoked when Android needs to create a new Checkin object reading the state from disk 
	 * or from Bundle. For security reasons the patient information is not persisted and it is not allowed. 
	 */
	public static final Parcelable.Creator<Checkin> CREATOR = new Parcelable.Creator<Checkin>() {
		public Checkin createFromParcel(Parcel in) {
			return new Checkin(in);
		}

		public Checkin[] newArray(int size) {
			return new Checkin[size];
		}
	};
	
	private Checkin(Parcel in) {
		this.id = in.readLong();
	    this.checkinDate = in.readString();
	    this.send = in.readInt() == 1?true:false;
	    this.howbad = in.readString();
	    this.painstop = in.readString();
	    this.takemedication = in.readInt() == 1?true:false;
	    this.alertDoctor = in.readInt() == 1?true:false;
	    this.photoPath = in.readString();
	    int cmListSize = in.readInt();
	    if (cmListSize > 0){
		    this.checkinMedicationList = new ArrayList<CheckinMedication>(cmListSize);
		    for (int i=0;i<cmListSize;i++){
		    	this.checkinMedicationList.add(CheckinMedication.CREATOR.createFromParcel(in));
		    }
	    }
//	    this.patient = Patient.CREATOR.createFromParcel(in);  //Patient object is not read from disk due security reasons
	}
    
}