// ST:BODY:startTransaction
package org.coursera.symptom.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This is the class that actually interacts with the SQLite3 database and does
 * the operations to manipulate the data within the database.
 * 
 * 
 */
public class SymptomDataDBAdapter {

    private static final String LOG_TAG = "SymptomDataDBAdapter";

    // ST:databaseTableVariableDeclaration:start
    static final String DATABASE_TABLE_DOCTOR = SymptomSchema.Doctor.TABLE_NAME;
    static final String DATABASE_TABLE_PATIENT = SymptomSchema.Patient.TABLE_NAME;
    static final String DATABASE_TABLE_CHECKIN = SymptomSchema.Checkin.TABLE_NAME;
    static final String DATABASE_TABLE_CHECKINMEDICATION = SymptomSchema.CheckinMedication.TABLE_NAME;
    static final String DATABASE_TABLE_PATIENTMEDICATION = SymptomSchema.PatientMedication.TABLE_NAME;
    static final String DATABASE_TABLE_STATUS = SymptomSchema.Status.TABLE_NAME;
    
    // The SHORT name of each column in your table
    // ST:createShortVariables:start
    private static final String Doctor_KEY_ID = SymptomSchema.Doctor.Cols.ID;
    private static final String Doctor_LastName = SymptomSchema.Doctor.Cols.LASTNAME;
    private static final String Doctor_Login = SymptomSchema.Doctor.Cols.LOGIN;
    private static final String Doctor_Name = SymptomSchema.Doctor.Cols.NAME;
    private static final String Patient_KEY_ID = SymptomSchema.Patient.Cols.ID;
    private static final String Patient_Name = SymptomSchema.Patient.Cols.NAME;
    private static final String Patient_LastName = SymptomSchema.Patient.Cols.LASTNAME;
    private static final String Patient_Login = SymptomSchema.Patient.Cols.LOGIN;
    private static final String Patient_BirthDate = SymptomSchema.Patient.Cols.BIRTHDATE;
    private static final String Patient_ID_DOCTOR = SymptomSchema.Patient.Cols.ID_DOCTOR;    
    private static final String Checkin_KEY_ID = SymptomSchema.Checkin.Cols.ID;
    private static final String Checkin_CheckinDate = SymptomSchema.Checkin.Cols.CHECKINDATE;
    private static final String Checkin_Howbad = SymptomSchema.Checkin.Cols.HOWBAD;
    private static final String Checkin_ID_Patient = SymptomSchema.Checkin.Cols.ID_PATIENT;
    private static final String Checkin_PainStop = SymptomSchema.Checkin.Cols.PAINSTOP;
    private static final String Checkin_Send = SymptomSchema.Checkin.Cols.SEND;
    private static final String Checkin_TakeMedication = SymptomSchema.Checkin.Cols.TAKEMEDICATION;
    private static final String Checkin_PhotoPath = SymptomSchema.Checkin.Cols.PHOTO_PATH;
    private static final String CheckinMedication_KEY_ID = SymptomSchema.CheckinMedication.Cols.ID;
    private static final String CheckinMedication_ID_PatientMedication = SymptomSchema.CheckinMedication.Cols.ID_PATIENTMEDICATION;
    private static final String CheckinMedication_ID_Checkin = SymptomSchema.CheckinMedication.Cols.ID_CHECKIN;
    private static final String CheckinMedication_Takeit = SymptomSchema.CheckinMedication.Cols.TAKEIT;
    private static final String CheckinMedication_TakeitDate = SymptomSchema.CheckinMedication.Cols.TAKEITDATE;
    private static final String CheckinMedication_TakeitTime = SymptomSchema.CheckinMedication.Cols.TAKEITTIME;
    private static final String PatientMedication_KEY_ID = SymptomSchema.PatientMedication.Cols.ID;
    private static final String PatientMedication_Name = SymptomSchema.PatientMedication.Cols.NAME;
    private static final String PatientMedication_Active = SymptomSchema.PatientMedication.Cols.ACTIVE;
    private static final String PatientMedication_ID_PATIENT = SymptomSchema.PatientMedication.Cols.ID_PATIENT;
    private static final String PatientMedication_SEND = SymptomSchema.PatientMedication.Cols.SEND;
    private static final String Status_KEY_ID = SymptomSchema.Status.Cols.ID;
    private static final String Status_RoleName = SymptomSchema.Status.Cols.ROLE_NAME;
    private static final String Status_DateInsert = SymptomSchema.Status.Cols.DATE_INSERT;
    
    
    // ST:createShortVariables:finish

    // ST:databaseTableCreationStrings:start
    // SQL Statement to create a new database table.
    private static final String DATABASE_CREATE_DOCTOR = "create table "
            + DATABASE_TABLE_DOCTOR + " (" // start table
            + Doctor_KEY_ID + " integer primary key, " // setup
            // ST:tableCreateVariables:start                        
            + Doctor_Login + " text ," //
            + Doctor_Name + " text ," //
            + Doctor_LastName + " text " //
            // ST:tableCreateVariables:finish
            + " );"; // end table
    // SQL Statement to create a new database table.
    private static final String DATABASE_CREATE_PATIENT = "create table "
            + DATABASE_TABLE_PATIENT + " (" // start table
            + Patient_KEY_ID + " integer primary key, " // setup
            // ST:tableCreateVariables:start           
            + Patient_Login + " text ," //
            + Patient_Name + " text ," //
            + Patient_LastName + " text ," //
            + Patient_BirthDate + " text ," //
            + Patient_ID_DOCTOR + " integer "            
            // ST:tableCreateVariables:finish
            + " );"; // end table
    // SQL Statement to create a new database table.
    private static final String DATABASE_CREATE_CHECKIN = "create table "
            + DATABASE_TABLE_CHECKIN + " (" // start table
            + Checkin_KEY_ID + " integer primary key, " // setup
            // ST:tableCreateVariables:start         
            + Checkin_CheckinDate + " text ," //
            + Checkin_Howbad + " text ," //
            + Checkin_PainStop + " text ," //
            + Checkin_TakeMedication + " integer ," //            
            + Checkin_Send + " integer ," //          
            + Checkin_PhotoPath + " text ," //
            + Checkin_ID_Patient + " integer "            
            // ST:tableCreateVariables:finish
            + " );"; // end table
    // SQL Statement to create a new database table.
    private static final String DATABASE_CREATE_CHECKINMEDICATION = "create table "
            + DATABASE_TABLE_CHECKINMEDICATION + " (" // start table
            + CheckinMedication_KEY_ID + " integer primary key, " // setup
            // ST:tableCreateVariables:start         
            + CheckinMedication_Takeit + " integer ," //
            + CheckinMedication_TakeitDate + " text ," //
            + CheckinMedication_TakeitTime + " text ," //            
            + CheckinMedication_ID_Checkin + " integer ," //
            + CheckinMedication_ID_PatientMedication + " integer" //
            // ST:tableCreateVariables:finish
            + " );"; // end table
    // SQL Statement to create a new database table.
    private static final String DATABASE_CREATE_PATIENTMEDICATION = "create table "
            + DATABASE_TABLE_PATIENTMEDICATION + " (" // start table
            + PatientMedication_KEY_ID + " integer primary key, " // setup
            // ST:tableCreateVariables:start    
            + PatientMedication_Name + " text ," //
            + PatientMedication_SEND + " integer ," //
            + PatientMedication_Active + " integer ," //
            + PatientMedication_ID_PATIENT + " integer" //
            // ST:tableCreateVariables:finish
            + " );"; // end table
    //SQL Statement to create a new database table.
    private static final String DATABASE_CREATE_STATUS = "create table "
            + DATABASE_TABLE_STATUS + " (" // start table
            + Status_KEY_ID + " integer primary key, " // setup
            // ST:tableCreateVariables:start            
            + Status_RoleName + " text ," //
            + Status_DateInsert + " text " //
            // ST:tableCreateVariables:finish
            + " );"; // end table
    // ST:databaseTableCreationStrings:finish

    // Variable to hold the database instance.
    private SQLiteDatabase db;
    // Context of the application using the database.
    private final Context context;
    // Database open/upgrade helper
    private myDbHelper dbHelper;
    // if the DB is in memory or to file.
    private boolean MEMORY_ONLY_DB = false;

    /**
     * constructor that accepts the context to be associated with
     * 
     * @param _context
     */
    public SymptomDataDBAdapter(Context _context) {
        Log.d(LOG_TAG, "MyDBAdapter constructor");

        context = _context;
        dbHelper = new myDbHelper(context, SymptomSchema.DATABASE_NAME, null, SymptomSchema.DATABASE_VERSION);
    }

    /**
     * constructor that accepts the context to be associated with, and if this
     * DB should be created in memory only(non-persistent).
     * 
     * @param _context
     */
    public SymptomDataDBAdapter(Context _context, boolean memory_only_db) {
        Log.d(LOG_TAG, "MyDBAdapter constructor w/ mem only =" + memory_only_db);

        context = _context;
        MEMORY_ONLY_DB = memory_only_db;
        if (MEMORY_ONLY_DB == true) {
            dbHelper = new myDbHelper(context, null, null, SymptomSchema.DATABASE_VERSION);
        } else {
            dbHelper = new myDbHelper(context, SymptomSchema.DATABASE_NAME, null, SymptomSchema.DATABASE_VERSION);
        }
    }

    /**
     * open the DB Get Memory or File version of DB, and write/read access or
     * just read access if that is all that is possible.
     * 
     * @return this MoocDataDBAdaptor
     * @throws SQLException
     */
    public SymptomDataDBAdapter open() throws SQLException {
        Log.d(LOG_TAG, "open()");
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException ex) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    /**
     * Remove a row of the DB where the rowIndex matches.
     * 
     * @param rowIndex
     *            row to remove from DB
     * @return if the row was removed
     */
    public int delete(final String table, long _id) {
        Log.d(LOG_TAG, "delete(" + _id + ") ");
        return db.delete(table, android.provider.BaseColumns._ID + " = " + _id, null);
    }

    /**
     * Delete row(s) that match the whereClause and whereArgs(if used).
     * <p>
     * the whereArgs is an String[] of values to substitute for the '?'s in the
     * whereClause
     * 
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public int delete(final String table, final String whereClause, final String[] whereArgs) {
        Log.d(LOG_TAG, "delete(" + whereClause + ") ");
        return db.delete(table, whereClause, whereArgs);
    }

    /**
     * Query the Database with the provided specifics.
     * 
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return Cursor of results
     */
    public Cursor query(final String table, final String[] projection,
            final String selection, final String[] selectionArgs,
            final String sortOrder) {

    	Log.d(LOG_TAG, "query(" + selection + ") " + "table:"+table);
        // TODO: Perform a query on the database with the given parameters
    	SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    	qb.setTables(table);    	
    	Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    /**
     * close the DB.
     */
    public void close() {
        Log.d(LOG_TAG, "close()");
        db.close();
    }

    /**
     * Start a transaction.
     */
    public void startTransaction() {
        Log.d(LOG_TAG, "startTransaction()");
        db.beginTransaction();
    }

    /**
     * End a transaction.
     */
    public void endTransaction() {
        Log.d(LOG_TAG, "endTransaction()");
        db.endTransaction();
    }

    /**
     * Get the underlying Database.
     * 
     * @return
     */
    SQLiteDatabase getDB() {
        return db;
    }

    /**
     * Insert a ContentValues into the DB.
     * 
     * @param location
     * @return row's '_id' of the newly inserted ContentValues
     */
    public long insert(final String table, final ContentValues cv) {
        Log.d(LOG_TAG, "insert(CV)");
        return db.insert(table, null, cv);
    }

    /**
     * Update Value(s) in the DB.
     * 
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return number of rows changed.
     */
    public int update(final String table, final ContentValues values,
            final String whereClause, final String[] whereArgs) {
        return db.update(table, values, whereClause, whereArgs);
    }

    @Override
    /**
     * finalize operations to this DB, and close it.
     */
    protected void finalize() throws Throwable {
    	Log.d(LOG_TAG, "finalize()");
        try {
            db.close();
        } catch (Exception e) {
            Log.d(LOG_TAG, "exception on finalize():" + e.getMessage());
        }
        super.finalize();
    }

    /**
     * This class can support running the database in a non-persistent mode,
     * this tells you if that is happening.
     * 
     * @return boolean true/false of if this DBAdaptor is persistent or in
     *         memory only.
     */
    public boolean isMemoryOnlyDB() {
        return MEMORY_ONLY_DB;
    }

    /**
     * DB Helper Class.
     * 
     * @author mwalker
     * 
     */
    private static class myDbHelper extends SQLiteOpenHelper {

        public myDbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "DATABASE_CREATE: version: " + SymptomSchema.DATABASE_VERSION);
            // ST:createTable:start
            db.execSQL(DATABASE_CREATE_DOCTOR);
            db.execSQL(DATABASE_CREATE_PATIENT);
            db.execSQL(DATABASE_CREATE_CHECKIN);
            db.execSQL(DATABASE_CREATE_PATIENTMEDICATION);
            db.execSQL(DATABASE_CREATE_CHECKINMEDICATION);     
            db.execSQL(DATABASE_CREATE_STATUS);                 
            // ST:createTable:finish

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Log version upgrade.
            Log.w(LOG_TAG + "DBHelper", "Upgrading from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

            // **** Upgrade DB ****
            // drop old DB
            // ST:dropTableIfExists:start
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_CHECKINMEDICATION);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_PATIENTMEDICATION);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_CHECKIN);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_PATIENT);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_DOCTOR); 
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_STATUS);             
            // ST:dropTableIfExists:finish
            // Create a new one.
            onCreate(db);
        }

    }

}
