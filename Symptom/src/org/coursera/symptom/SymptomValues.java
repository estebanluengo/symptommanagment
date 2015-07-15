package org.coursera.symptom;

/**
 * This class contains all Constants that are used in the Android app 
 *
 */
public class SymptomValues {
	public static final String BASE_PACKAGE = "org.coursera.symptom.";

	public static final String SERVER = "https://10.215.215.246:8443";
//	public static final String SERVER = "https://192.168.1.2:8443";
	
	public static final String ROLE_DOCTOR = "DOCTOR";
	public static final String ROLE_PATIENT = "PATIENT";	
	public static final String ROLE_UNKNOWN = "UNKNOWN";
	public static final String CLIENT_ID = "symptom_client";
	
	public static final String SEVERE_OPTION = "Severe";
	public static final String MODERATE_OPTION = "Moderate";
	public static final String CONTROLLED_OPTION = "Well controlled";
	public static final String CANNOTEAT_OPTION = "I can not eat";
	public static final String NO_OPTION = "No";
	public static final String YES_OPTION = "Yes";
	public static final String SOME_OPTION = "Some";
	
	public static final String SYMPTOM_PREFERENCES = BASE_PACKAGE + "SYMPTOM_PREFERENCES";
	public static final String REMEMBER_PASSWORD = BASE_PACKAGE + "REMEMBER_PASSWORD";
	public static final String OAUTH_TOKEN = BASE_PACKAGE + "OAUTH_TOKEN";
	public static final String ROLE_USER = BASE_PACKAGE + "ROLE_USER";
	public static final String ID_USER = BASE_PACKAGE + "ID_USER";
	public static final String CHECKIN_REMINDER = BASE_PACKAGE + "CHECKIN_REMINDER";
	public static final String UPLOAD_DATA = BASE_PACKAGE + "UPLOAD_DATA";
	public static final String TYPE_DOWNLOAD = BASE_PACKAGE + "TYPE_DOWNLOAD";
	public static final String DOWNLOAD_MEDICATIONS = BASE_PACKAGE + "DOWNLOAD_MEDICATIONS";
	public static final String DOWNLOAD_CHECKINS = BASE_PACKAGE + "DOWNLOAD_CHECKINS";
	public static final String DOWNLOAD_PICTURE = BASE_PACKAGE + "DOWNLOAD_PICTURE";
	public static final String MESSENGER = BASE_PACKAGE + "MESSENGER";
	public static final String PICTURE_PATH = BASE_PACKAGE + "PICTURE_PATH";
	public static final String MINUTES_TO_NEXT_REMINDER = BASE_PACKAGE + "MINUTES_TO_NEXT_REMINDER";
	public static final String CHECKIN_INDEX_QUESTION = BASE_PACKAGE + "CHECKIN_INDEX_QUESTION";
	public static final String WIZARD = BASE_PACKAGE + "WIZARD";
	public static final String NUMBER_OF_MEDICATIONS = BASE_PACKAGE + "NUMBER_OF_MEDICATIONS";
	public static final String MONITOR_PATIENT_LIST = BASE_PACKAGE + "MONITOR_PATIENT_LIST";
	public static final String MONITOR_PATIENT = BASE_PACKAGE + "MONITOR_PATIENT";
	public static final String CHECKIN_DATA = BASE_PACKAGE + "CHECKIN_DATA";
	public static final String CHECKIN_SELECTED_INDEX = BASE_PACKAGE + "CHECKIN_SELECTED_INDEX";
	public static final String ACTION_INSERT = BASE_PACKAGE + "INSERT";
	public static final String ACTION_DELETE = BASE_PACKAGE + "DELETE";
	public static final String ACTION_UPDATE = BASE_PACKAGE + "UPDATE";
	public static final int ACTION_TAKE_PHOTO = 1;
	public static final String ALBUM_NAME = "Symptoms Management";
}
