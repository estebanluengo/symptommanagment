package org.coursera.symptom.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * A ContentProvider for access local database. This class uses a SymptomDataDBAdapter to access the database
 * and SymptomSchema for all schema definitions
 *
 */
public class SymptomProvider extends ContentProvider {

	private final static String LOG_TAG = "SymptomProvider";
    // Local backend DB
	SymptomDataDBAdapter mDB;
    // shorten variable names for easier readability
    // ST:createShortContentURIforRelations:begin
    public final static Uri DOCTOR_CONTENT_URI = SymptomSchema.Doctor.CONTENT_URI;
    public final static Uri PATIENT_CONTENT_URI = SymptomSchema.Patient.CONTENT_URI;
    public final static Uri CHECKIN_CONTENT_URI = SymptomSchema.Checkin.CONTENT_URI;
    public final static Uri PATIENTMEDICATION_CONTENT_URI = SymptomSchema.PatientMedication.CONTENT_URI;
    public final static Uri CHECKINMEDICATION_CONTENT_URI = SymptomSchema.CheckinMedication.CONTENT_URI;
    public final static Uri STATUS_CONTENT_URI = SymptomSchema.Status.CONTENT_URI;    
    // ST:createShortContentURIforRelations:finish
    public static String AUTHORITY = SymptomSchema.AUTHORITY;
    // ST:createShortURIMatchingTokens:begin
    public static final int DOCTOR_ALL_ROWS = SymptomSchema.Doctor.PATH_TOKEN;
    public static final int DOCTOR_SINGLE_ROW = SymptomSchema.Doctor.PATH_FOR_ID_TOKEN;
    public static final int PATIENT_ALL_ROWS = SymptomSchema.Patient.PATH_TOKEN;
    public static final int PATIENT_SINGLE_ROW = SymptomSchema.Patient.PATH_FOR_ID_TOKEN;
    public static final int CHECKIN_ALL_ROWS = SymptomSchema.Checkin.PATH_TOKEN;
    public static final int CHECKIN_SINGLE_ROW = SymptomSchema.Checkin.PATH_FOR_ID_TOKEN;
    public static final int PATIENTMEDICATION_ALL_ROWS = SymptomSchema.PatientMedication.PATH_TOKEN;
    public static final int PATIENTMEDICATION_SINGLE_ROW = SymptomSchema.PatientMedication.PATH_FOR_ID_TOKEN;
    public static final int CHECKINMEDICATION_ALL_ROWS = SymptomSchema.CheckinMedication.PATH_TOKEN;
    public static final int CHECKINMEDICATION_SINGLE_ROW = SymptomSchema.CheckinMedication.PATH_FOR_ID_TOKEN;    
    public static final int STATUS_ALL_ROWS = SymptomSchema.Status.PATH_TOKEN;
    public static final int STATUS_SINGLE_ROW = SymptomSchema.Status.PATH_FOR_ID_TOKEN;    
    
    // ST:createShortURIMatchingTokens:finish
    private static final UriMatcher uriMatcher = SymptomSchema.URI_MATCHER;
	
	@Override
	public boolean onCreate() {
		Log.d(LOG_TAG, "onCreate()");
        mDB = new SymptomDataDBAdapter(getContext());
        mDB.open();
        return true;
	}

	@Override
	public String getType(Uri uri) {
		Log.d(LOG_TAG, "getType() for uri:"+uri);
        switch (uriMatcher.match(uri)) {
        // ST:createContentTypeReturnsforRelations:begin
        case DOCTOR_ALL_ROWS:
            return SymptomSchema.Doctor.CONTENT_TYPE_DIR;
        case DOCTOR_SINGLE_ROW:
            return SymptomSchema.Doctor.CONTENT_ITEM_TYPE;
        case PATIENT_ALL_ROWS:
            return SymptomSchema.Patient.CONTENT_TYPE_DIR;
        case PATIENT_SINGLE_ROW:
            return SymptomSchema.Patient.CONTENT_ITEM_TYPE;
        case CHECKIN_ALL_ROWS:
            return SymptomSchema.Checkin.CONTENT_TYPE_DIR;
        case CHECKIN_SINGLE_ROW:
            return SymptomSchema.Checkin.CONTENT_ITEM_TYPE;
        case PATIENTMEDICATION_ALL_ROWS:
            return SymptomSchema.PatientMedication.CONTENT_TYPE_DIR;
        case PATIENTMEDICATION_SINGLE_ROW:
            return SymptomSchema.PatientMedication.CONTENT_ITEM_TYPE;
        case CHECKINMEDICATION_ALL_ROWS:
            return SymptomSchema.CheckinMedication.CONTENT_TYPE_DIR;
        case CHECKINMEDICATION_SINGLE_ROW:
            return SymptomSchema.CheckinMedication.CONTENT_ITEM_TYPE;      
        case STATUS_ALL_ROWS:
            return SymptomSchema.Status.CONTENT_TYPE_DIR;
        case STATUS_SINGLE_ROW:
            return SymptomSchema.Status.CONTENT_ITEM_TYPE;             
            // ST:createContentTypeReturnsforRelations:finish
        default:
            throw new UnsupportedOperationException("URI " + uri + " is not supported.");
        }
	}

	@Override
	synchronized public Uri insert(Uri uri, ContentValues assignedValues) {
		Log.d(LOG_TAG, "insert called for uri:"+uri);
        final int match = uriMatcher.match(uri);
        switch (match) {
	        // ST:createUpsertForRelations:begin
	        case DOCTOR_ALL_ROWS: {
	        	Log.d(LOG_TAG, "Doctor all rows called");
	            final ContentValues values = SymptomSchema.Doctor.initializeWithDefault(assignedValues);
//	            values.remove(SymptomSchema.Doctor.Cols.ID);
	            final long rowID = mDB.insert(SymptomSchema.Doctor.TABLE_NAME, values);
	            if (rowID < 0) {
	                Log.d(LOG_TAG, "Bad id. Not inserted");
	                return null;
	            }
	            final Uri insertedID = ContentUris.withAppendedId(DOCTOR_CONTENT_URI, rowID);
	            getContext().getContentResolver().notifyChange(insertedID, null);
	            return ContentUris.withAppendedId(DOCTOR_CONTENT_URI, rowID);
	        }
	        case PATIENT_ALL_ROWS: {
	        	Log.d(LOG_TAG, "Patient all rows called");
	            final ContentValues values = SymptomSchema.Patient.initializeWithDefault(assignedValues);
//	            values.remove(SymptomSchema.Patient.Cols.ID);
	            final long rowID = mDB.insert(SymptomSchema.Patient.TABLE_NAME, values);
	            if (rowID < 0) {
	                Log.d(LOG_TAG, "Bad id. Not inserted");
	                return null;
	            }
	            final Uri insertedID = ContentUris.withAppendedId(PATIENT_CONTENT_URI,rowID);
	            getContext().getContentResolver().notifyChange(insertedID, null);
	            return ContentUris.withAppendedId(PATIENT_CONTENT_URI, rowID);
	        }
	        case CHECKIN_ALL_ROWS: {
	        	Log.d(LOG_TAG, "Checkin all rows called");
	            final ContentValues values = SymptomSchema.Checkin.initializeWithDefault(assignedValues);
//	            values.remove(SymptomSchema.Checkin.Cols.ID);
	            final long rowID = mDB.insert(SymptomSchema.Checkin.TABLE_NAME, values);
	            if (rowID < 0) {
	                Log.d(LOG_TAG, "Bad id. Not inserted");
	                return null;
	            }
	            final Uri insertedID = ContentUris.withAppendedId(CHECKIN_CONTENT_URI,rowID);
	            getContext().getContentResolver().notifyChange(insertedID, null);
	            return ContentUris.withAppendedId(CHECKIN_CONTENT_URI, rowID);
	        }
	        case PATIENTMEDICATION_ALL_ROWS: {
	        	Log.d(LOG_TAG, "Patient medication all rows called");
	            final ContentValues values = SymptomSchema.PatientMedication.initializeWithDefault(assignedValues);
//	            values.remove(SymptomSchema.PatientMedication.Cols.ID);
	            final long rowID = mDB.insert(SymptomSchema.PatientMedication.TABLE_NAME, values);
	            if (rowID < 0) {
	                Log.d(LOG_TAG, "Bad id. Not inserted");
	                return null;
	            }
	            final Uri insertedID = ContentUris.withAppendedId(PATIENTMEDICATION_CONTENT_URI,rowID);
	            getContext().getContentResolver().notifyChange(insertedID, null);
	            return ContentUris.withAppendedId(PATIENTMEDICATION_CONTENT_URI, rowID);
	        }
	        case CHECKINMEDICATION_ALL_ROWS: {
	        	Log.d(LOG_TAG, "Checkin medication all rows called");
	            final ContentValues values = SymptomSchema.CheckinMedication.initializeWithDefault(assignedValues);
//	            values.remove(SymptomSchema.CheckinMedication.Cols.ID);
	            final long rowID = mDB.insert(SymptomSchema.CheckinMedication.TABLE_NAME, values);
	            if (rowID < 0) {
	                Log.d(LOG_TAG, "Bad id. Not inserted");
	                return null;
	            }
	            final Uri insertedID = ContentUris.withAppendedId(CHECKINMEDICATION_CONTENT_URI,rowID);
	            getContext().getContentResolver().notifyChange(insertedID, null);
	            return ContentUris.withAppendedId(CHECKINMEDICATION_CONTENT_URI, rowID);
	        }
	        case STATUS_ALL_ROWS: {
	        	Log.d(LOG_TAG, "Status all rows called");
	            final ContentValues values = SymptomSchema.Status.initializeWithDefault(assignedValues);
//	            values.remove(SymptomSchema.Status.Cols.ID);
	            final long rowID = mDB.insert(SymptomSchema.Status.TABLE_NAME, values);
	            if (rowID < 0) {
	                Log.d(LOG_TAG, "Bad id. Not inserted");
	                return null;
	            }
	            final Uri insertedID = ContentUris.withAppendedId(STATUS_CONTENT_URI,rowID);
	            getContext().getContentResolver().notifyChange(insertedID, null);
	            return ContentUris.withAppendedId(STATUS_CONTENT_URI, rowID);
	        }
	        // ST:createUpsertForRelations:finish
	
	        // breaks intentionally omitted
	        case DOCTOR_SINGLE_ROW:
	        case PATIENT_SINGLE_ROW:
	        case CHECKIN_SINGLE_ROW: 
	        case PATIENTMEDICATION_SINGLE_ROW:
	        case CHECKINMEDICATION_SINGLE_ROW: 
	        case STATUS_SINGLE_ROW:{
	        	Log.d(LOG_TAG, "Unsupported URI, unable to insert into specific row:"+uri);
	            throw new IllegalArgumentException("Unsupported URI, unable to insert into specific row:" + uri);
	        }
	        default: {
	        	Log.d(LOG_TAG, "Unsupported URI:"+uri);
	            throw new IllegalArgumentException("Unsupported URI:" + uri);
	        }
        }
	}

	@Override
	synchronized public int update(Uri uri, ContentValues values, String whereClause, String[] whereArgs) {
		Log.d(LOG_TAG, "delete called for uri:"+uri);
		switch (uriMatcher.match(uri)) {
	        // ST:createDeleteforRelations:begin
			case STATUS_SINGLE_ROW:
	            whereClause = whereClause + SymptomSchema.Status.Cols.ID + " = " + uri.getLastPathSegment();
	            // no break here on purpose
	        case STATUS_ALL_ROWS: {
	            return updateAndNotify(uri, SymptomSchema.Status.TABLE_NAME, values, whereClause, whereArgs);
	        }
	        case DOCTOR_SINGLE_ROW:
	            whereClause = whereClause + SymptomSchema.Doctor.Cols.ID + " = " + uri.getLastPathSegment();
	            // no break here on purpose
	        case DOCTOR_ALL_ROWS: {
	            return updateAndNotify(uri, SymptomSchema.Doctor.TABLE_NAME, values, whereClause, whereArgs);
	        }
	        case PATIENT_SINGLE_ROW:
	            whereClause = whereClause + SymptomSchema.Patient.Cols.ID + " = " + uri.getLastPathSegment();
	            // no break here on purpose
	        case PATIENT_ALL_ROWS: {
	            return updateAndNotify(uri, SymptomSchema.Patient.TABLE_NAME, values, whereClause, whereArgs);
	        }
	        case CHECKIN_SINGLE_ROW:
	            whereClause = whereClause + SymptomSchema.Checkin.Cols.ID + " = " + uri.getLastPathSegment();
	            // no break here on purpose
	        case CHECKIN_ALL_ROWS: {
	            return updateAndNotify(uri, SymptomSchema.Checkin.TABLE_NAME, values, whereClause, whereArgs);
	        }
	        case PATIENTMEDICATION_SINGLE_ROW:
	            whereClause = whereClause + SymptomSchema.PatientMedication.Cols.ID + " = " + uri.getLastPathSegment();
	            // no break here on purpose
	        case PATIENTMEDICATION_ALL_ROWS: {
	            return updateAndNotify(uri, SymptomSchema.PatientMedication.TABLE_NAME, values, whereClause, whereArgs);
	        }
	        case CHECKINMEDICATION_SINGLE_ROW:
	            whereClause = whereClause + SymptomSchema.CheckinMedication.Cols.ID + " = " + uri.getLastPathSegment();
	            // no break here on purpose
	        case CHECKINMEDICATION_ALL_ROWS: {
	            return updateAndNotify(uri, SymptomSchema.CheckinMedication.TABLE_NAME, values, whereClause, whereArgs);
	        }
	        // ST:createDeleteforRelations:finish
	        default:
	            throw new IllegalArgumentException("Unsupported URI: " + uri);
	        }
	}
	
    /*
     * private update function that updates based on parameters, then notifies
     * change
     */
    private int updateAndNotify(final Uri uri, final String tableName,
            final ContentValues values, final String whereClause,
            final String[] whereArgs) {
        int count = mDB.update(tableName, values, whereClause, whereArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }	
	
	@Override
	synchronized public int delete(Uri uri, String whereClause, String[] whereArgs) {
		Log.d(LOG_TAG, "delete called for uri:"+uri);
		switch (uriMatcher.match(uri)) {
	        // ST:createDeleteforRelations:begin
			case STATUS_SINGLE_ROW:
	            whereClause = whereClause + SymptomSchema.Status.Cols.ID + " = " + uri.getLastPathSegment();
	            // no break here on purpose
	        case STATUS_ALL_ROWS: {
	            return deleteAndNotify(uri, SymptomSchema.Status.TABLE_NAME, whereClause, whereArgs);
	        }
	        case DOCTOR_SINGLE_ROW:
	            whereClause = whereClause + SymptomSchema.Doctor.Cols.ID + " = " + uri.getLastPathSegment();
	            // no break here on purpose
	        case DOCTOR_ALL_ROWS: {
	            return deleteAndNotify(uri, SymptomSchema.Doctor.TABLE_NAME, whereClause, whereArgs);
	        }
	        case PATIENT_SINGLE_ROW:
	            whereClause = whereClause + SymptomSchema.Patient.Cols.ID + " = " + uri.getLastPathSegment();
	            // no break here on purpose
	        case PATIENT_ALL_ROWS: {
	            return deleteAndNotify(uri, SymptomSchema.Patient.TABLE_NAME, whereClause, whereArgs);
	        }
	        case CHECKIN_SINGLE_ROW:
	            whereClause = whereClause + SymptomSchema.Checkin.Cols.ID + " = " + uri.getLastPathSegment();
	            // no break here on purpose
	        case CHECKIN_ALL_ROWS: {
	            return deleteAndNotify(uri, SymptomSchema.Checkin.TABLE_NAME, whereClause, whereArgs);
	        }
	        case PATIENTMEDICATION_SINGLE_ROW:
	            whereClause = whereClause + SymptomSchema.PatientMedication.Cols.ID + " = " + uri.getLastPathSegment();
	            // no break here on purpose
	        case PATIENTMEDICATION_ALL_ROWS: {
	            return deleteAndNotify(uri, SymptomSchema.PatientMedication.TABLE_NAME, whereClause, whereArgs);
	        }
	        case CHECKINMEDICATION_SINGLE_ROW:
	            whereClause = whereClause + SymptomSchema.CheckinMedication.Cols.ID + " = " + uri.getLastPathSegment();
	            // no break here on purpose
	        case CHECKINMEDICATION_ALL_ROWS: {
	            return deleteAndNotify(uri, SymptomSchema.CheckinMedication.TABLE_NAME, whereClause, whereArgs);
	        }
	        // ST:createDeleteforRelations:finish
	        default:
	            throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
    /*
     * Private method to both attempt the delete command, and then to notify of
     * the changes
     */
    private int deleteAndNotify(final Uri uri, final String tableName,
            final String whereClause, final String[] whereArgs) {
        int count = mDB.delete(tableName, whereClause, whereArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }	

	@Override
	synchronized public Cursor query(final Uri uri, final String[] projection,
			final String selection, final String[] selectionArgs,
			final String sortOrder) {
		Log.d(LOG_TAG, "query called for uri:"+uri);
        String modifiedSelection = selection;
        switch (uriMatcher.match(uri)) {
	        // ST:createPublicQueryforRelations:begin
	        case DOCTOR_SINGLE_ROW: {
	            modifiedSelection = modifiedSelection + SymptomSchema.Doctor.Cols.ID + " = " + uri.getLastPathSegment();
	        }
	        case DOCTOR_ALL_ROWS: {
	            return query(uri, SymptomSchema.Doctor.TABLE_NAME, projection, modifiedSelection, selectionArgs, sortOrder);
	        }
	        case PATIENT_SINGLE_ROW: {
	            modifiedSelection = modifiedSelection + SymptomSchema.Patient.Cols.ID + " = " + uri.getLastPathSegment();
	        }
	        case PATIENT_ALL_ROWS: {
	            return query(uri, SymptomSchema.Patient.TABLE_NAME, projection, modifiedSelection, selectionArgs, sortOrder);
	        }
	        case CHECKIN_SINGLE_ROW: {
	            modifiedSelection = modifiedSelection + SymptomSchema.Checkin.Cols.ID + " = " + uri.getLastPathSegment();
	        }
	        case CHECKIN_ALL_ROWS: {
	            return query(uri, SymptomSchema.Checkin.TABLE_NAME, projection, modifiedSelection, selectionArgs, sortOrder);
	        }
	        case PATIENTMEDICATION_SINGLE_ROW: {
	            modifiedSelection = modifiedSelection + SymptomSchema.PatientMedication.Cols.ID + " = " + uri.getLastPathSegment();
	        }
	        case PATIENTMEDICATION_ALL_ROWS: {
	            return query(uri, SymptomSchema.PatientMedication.TABLE_NAME, projection, modifiedSelection, selectionArgs, sortOrder);
	        }
	        case CHECKINMEDICATION_SINGLE_ROW: {
	            modifiedSelection = modifiedSelection + SymptomSchema.CheckinMedication.Cols.ID + " = " + uri.getLastPathSegment();
	        }
	        case CHECKINMEDICATION_ALL_ROWS: {
	            return query(uri, SymptomSchema.CheckinMedication.TABLE_NAME, projection, modifiedSelection, selectionArgs, sortOrder);
	        }
	        case STATUS_SINGLE_ROW: {
	            modifiedSelection = modifiedSelection + SymptomSchema.Status.Cols.ID + " = " + uri.getLastPathSegment();
	        }
	        case STATUS_ALL_ROWS: {
	            return query(uri, SymptomSchema.Status.TABLE_NAME, projection, modifiedSelection, selectionArgs, sortOrder);
	        }
	        // ST:createPublicQueryforRelations:finish
	        default:
	            return null;
        }
	}	
	
	/*
     * Private query that does the actual query based on the table
     */
    synchronized private Cursor query(final Uri uri, final String tableName,
            final String[] projection, final String selection,
            final String[] selectionArgs, final String sortOrder) {

        // TODO: Perform a query on the database with the given parameters
    	Cursor cursor = mDB.query(tableName, projection, selection, selectionArgs, sortOrder);
    	cursor.setNotificationUri(getContext().getContentResolver(), uri);
    	
    	return cursor;
    }
}