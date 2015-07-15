package org.coursera.symptom.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class contains the answer for a Checkin question.
 *
 */
public class CheckinQuestion implements Parcelable{
	//question that the user answer
	private String question;
	//option 1 that the user can answer
	private String option1;
	//option 2 that the user can answer
	private String option2;
	//option 3 that the user can answer
	private String option3;
	//option that user answer: 1 or 2 or 3	
	private int answer;
	//Needs to show a date time picker for this question?
	boolean isDateTimeRequired;
	//Date the user picked in case of date time is required
	private String dateTakeMedication;
	//Time the user picked in case of date time is required
	private String timeTakeMedication;
	//Patient medication Id in the case the question asked for Did your take your XXX medication?
	private Long medicationId;
	//Photo path where the photo was saved locally in the case this is the last question of checkin process
	private String photoPath;
	
	public CheckinQuestion(){
		
	}
	
	public CheckinQuestion(String question, String option1, String option2,
			String option3, boolean isDateTimeRequired) {
		super();
		this.question = question;
		this.option1 = option1;
		this.option2 = option2;
		this.option3 = option3;
		this.isDateTimeRequired = isDateTimeRequired;
	}
	
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getOption1() {
		return option1;
	}
	public void setOption1(String option1) {
		this.option1 = option1;
	}
	public String getOption2() {
		return option2;
	}
	public void setOption2(String option2) {
		this.option2 = option2;
	}
	public String getOption3() {
		return option3;
	}
	public void setOption3(String option3) {
		this.option3 = option3;
	}
	public int getAnswer() {
		return answer;
	}
	public void setAnswer(int answer) {
		this.answer = answer;
	}
	public String getDateTakeMedication() {
		return dateTakeMedication;
	}
	public void setDateTakeMedication(String dateTakeMedication) {
		this.dateTakeMedication = dateTakeMedication;
	}
	public String getTimeTakeMedication() {
		return timeTakeMedication;
	}
	public void setTimeTakeMedication(String timeTakeMedication) {
		this.timeTakeMedication = timeTakeMedication;
	}
	public boolean isDateTimeRequired() {
		return isDateTimeRequired;
	}
	public void setDateTimeRequired(boolean isDateTimeRequired) {
		this.isDateTimeRequired = isDateTimeRequired;
	}
	public Long getMedicationId() {
		return medicationId;
	}
	public void setMedicationId(Long medicationId) {
		this.medicationId = medicationId;
	}
	public String getPhotoPath() {
		return photoPath;
	}
	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	/**
	 * Return answer in textual form
	 * @return String with the answer
	 */
	public String getTextAnswer(){
		switch(this.answer){
			case 1: return this.option1;
			case 2: return this.option2;
			case 3: return this.option3;
			default: return null;
		}
	}

	@Override
	public String toString() {
		return "CheckinQuestion [question=" + question + ", option1=" + option1
				+ ", option2=" + option2 + ", option3=" + option3 + ", answer="
				+ answer + ", isDateTimeRequired=" + isDateTimeRequired
				+ ", dateTakeMedication=" + dateTakeMedication
				+ ", timeTakeMedication=" + timeTakeMedication
				+ ", medicationId=" + medicationId + ", photoPath="
				+ photoPath + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Used for writing a copy of this object to a Parcel, do not manually call.
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(question);
		dest.writeString(option1);
		dest.writeString(option2);
		dest.writeString(option3);
		dest.writeInt(answer);
		dest.writeInt(isDateTimeRequired?1:0);
		dest.writeString(dateTakeMedication);
		dest.writeString(timeTakeMedication);
		if (medicationId != null){
			dest.writeLong(medicationId);
		}else{
			dest.writeLong(0);
		}
		dest.writeString(photoPath);
	}	
	
	public static final Parcelable.Creator<CheckinQuestion> CREATOR = new Parcelable.Creator<CheckinQuestion>() {
		public CheckinQuestion createFromParcel(Parcel in) {
			return new CheckinQuestion(in);
		}

		public CheckinQuestion[] newArray(int size) {
			return new CheckinQuestion[size];
		}
	};

	/**
	 * Used for writing a copy of this object to a Parcel, do not manually call.
	 */
	private CheckinQuestion(Parcel in) {
		question = in.readString();
		option1 = in.readString();
		option2 = in.readString();
		option3 = in.readString();
		answer = in.readInt();
		isDateTimeRequired = in.readInt() == 1?true:false;
		dateTakeMedication = in.readString();
		timeTakeMedication = in.readString();
		medicationId = in.readLong();
		photoPath = in.readString();
	}
	
}
