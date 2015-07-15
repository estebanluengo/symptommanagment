// ST:BODY:start

package org.coursera.symptom.provider;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 
 * <p>
 * based on the work by Vladimir Vivien (http://vladimirvivien.com/), which
 * provides a very logical organization of the meta-data of the Database and
 * Content Provider
 * <p>
 * This note might be moved to a 'Special Thanks' section once one is created
 * and moved out of future test code.
 * 
 * @author Michael A. Walker
 */
public class SymptomSchema {

    /**
     * Project Related Constants
     */

    public static final String ORGANIZATIONAL_NAME = "org.coursera";
    public static final String PROJECT_NAME = "symptomsmanagment";
    public static final String PROVIDER_NAME = "SymptomProvider";
    public static final String DATABASE_NAME = "symptomsmanagment.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * ConentProvider Related Constants
     */
    public static final String AUTHORITY = ORGANIZATIONAL_NAME + "." + PROJECT_NAME + "." + PROVIDER_NAME;
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final UriMatcher URI_MATCHER = buildUriMatcher();

    // register identifying URIs for Restaurant entity
    // the TOKEN value is associated with each URI registered
    private static UriMatcher buildUriMatcher() {
        // add default 'no match' result to matcher
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // ST:addMatcherURIs:inline
        // Doctor URIs
        matcher.addURI(AUTHORITY, Doctor.PATH, Doctor.PATH_TOKEN);
        matcher.addURI(AUTHORITY, Doctor.PATH_FOR_ID, Doctor.PATH_FOR_ID_TOKEN);
        // Patient URIs
        matcher.addURI(AUTHORITY, Patient.PATH, Patient.PATH_TOKEN);
        matcher.addURI(AUTHORITY, Patient.PATH_FOR_ID, Patient.PATH_FOR_ID_TOKEN);
        // Checkin URIs
        matcher.addURI(AUTHORITY, Checkin.PATH, Checkin.PATH_TOKEN);
        matcher.addURI(AUTHORITY, Checkin.PATH_FOR_ID, Checkin.PATH_FOR_ID_TOKEN);
        // PatientMedication URIs
        matcher.addURI(AUTHORITY, PatientMedication.PATH, PatientMedication.PATH_TOKEN);
        matcher.addURI(AUTHORITY, PatientMedication.PATH_FOR_ID, PatientMedication.PATH_FOR_ID_TOKEN);
        // CheckinMedication URIs
        matcher.addURI(AUTHORITY, CheckinMedication.PATH, CheckinMedication.PATH_TOKEN);
        matcher.addURI(AUTHORITY, CheckinMedication.PATH_FOR_ID, CheckinMedication.PATH_FOR_ID_TOKEN);      
        // Status URIs
        matcher.addURI(AUTHORITY, Status.PATH, Status.PATH_TOKEN);
        matcher.addURI(AUTHORITY, Status.PATH_FOR_ID, Status.PATH_FOR_ID_TOKEN);              
        // ST:addMatcherURIs:complete
        return matcher;
    }

    // ST:createRelationMetaData:inline    
    // Define a static class that represents description of stored content entity.
    public static class Doctor {
        // an identifying name for entity
        public static final String TABLE_NAME = "doctor_table";
        // define a URI paths to access entity
        // BASE_URI/tag - for list of tag(s)
        // BASE_URI/tag/* - retrieve specific tag by id
        // the token value are used to register path in matcher (see above)        
        public static final String PATH = "doctor";
        public static final int PATH_TOKEN = 110;
        public static final String PATH_FOR_ID = "doctor/*";
        public static final int PATH_FOR_ID_TOKEN = 120;
        // URI for all content stored as Restaurant entity
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
        public static final String CONTENT_TOPIC = "topic/org.coursera.doctor";
        private final static String MIME_TYPE_END = "doctor";
        // define the MIME type of data in the content provider
        public static final String CONTENT_TYPE_DIR = ORGANIZATIONAL_NAME + ".cursor.dir/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        public static final String CONTENT_ITEM_TYPE = ORGANIZATIONAL_NAME + ".cursor.item/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        // the names and order of ALL columns, including internal use ones
        public static final String[] ALL_COLUMN_NAMES = {
                Cols.ID,
                // ST:getColumnNames:inline
                Cols.NAME, Cols.LASTNAME, Cols.LOGIN
                // ST:getColumnNames:complete
        };

        public static ContentValues initializeWithDefault(
                final ContentValues assignedValues) {
            // final Long now = Long.valueOf(System.currentTimeMillis());
            final ContentValues setValues = (assignedValues == null) ? new ContentValues() : assignedValues;
            
            if (!setValues.containsKey(Cols.NAME)) {
                setValues.put(Cols.NAME, "");
            }
            if (!setValues.containsKey(Cols.LASTNAME)) {
                setValues.put(Cols.LASTNAME, "");
            }
            if (!setValues.containsKey(Cols.LOGIN)) {
                setValues.put(Cols.LOGIN, "");
            }                     
            return setValues;
        }

        // a static class to store columns in entity
        public static class Cols {
            public static final String ID = BaseColumns._ID; // convention            
            // The name and column index of each column in your database
            // ST:getColumnDeclaration:inline
            public static final String NAME = "NAME";
            public static final String LASTNAME = "LASTNAME";
            public static final String LOGIN = "LOGIN";
            // ST:getColumnDeclaration:complete
        }
    }

    // Define a static class that represents description of stored content entity.
    public static class Patient {
        // an identifying name for entity
        public static final String TABLE_NAME = "patient_table";
        public static final String PATH = "patient";
        public static final int PATH_TOKEN = 210;
        public static final String PATH_FOR_ID = "patient/*";
        public static final int PATH_FOR_ID_TOKEN = 220;
        // URI for all content stored as Restaurant entity
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
        public static final String CONTENT_TOPIC = "topic/org.coursera.patient";
        private final static String MIME_TYPE_END = "patient";
        // define the MIME type of data in the content provider
        public static final String CONTENT_TYPE_DIR = ORGANIZATIONAL_NAME + ".cursor.dir/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        public static final String CONTENT_ITEM_TYPE = ORGANIZATIONAL_NAME + ".cursor.item/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        // the names and order of ALL columns, including internal use ones
        public static final String[] ALL_COLUMN_NAMES = { Cols.ID,
            // ST:getColumnNames:inline
        	Cols.NAME, Cols.LASTNAME, Cols.LOGIN, Cols.BIRTHDATE, Cols.ID_DOCTOR
        	// ST:getColumnNames:complete
        };  

        public static ContentValues initializeWithDefault(
                final ContentValues assignedValues) {
            // final Long now = Long.valueOf(System.currentTimeMillis());
            final ContentValues setValues = (assignedValues == null) ? new ContentValues()
                    : assignedValues;
            if (!setValues.containsKey(Cols.NAME)) {
                setValues.put(Cols.NAME, "");
            }
            if (!setValues.containsKey(Cols.LASTNAME)) {
                setValues.put(Cols.LASTNAME, "");
            }
            if (!setValues.containsKey(Cols.LOGIN)) {
                setValues.put(Cols.LOGIN, "");
            }            
            if (!setValues.containsKey(Cols.BIRTHDATE)) {
                setValues.put(Cols.BIRTHDATE, "");
            }
            if (!setValues.containsKey(Cols.ID_DOCTOR)) {
                setValues.put(Cols.ID_DOCTOR, 0);
            }
            return setValues;
        }
        // a static class to store columns in entity
        public static class Cols {
            public static final String ID = BaseColumns._ID; // convention
            // The name and column index of each column in your database
            // ST:getColumnDeclaration:inline
            public static final String NAME = "NAME";
            public static final String LASTNAME = "LASTNAME";
            public static final String LOGIN = "LOGIN";
            public static final String BIRTHDATE = "BIRTHDATE";
            public static final String ID_DOCTOR = "ID_DOCTOR";            
            // ST:getColumnDeclaration:complete
        }
    }
    // ST:createRelationMetaData:complete

 // Define a static class that represents description of stored content entity.
    public static class Checkin {
        // an identifying name for entity
        public static final String TABLE_NAME = "checkin_table";
        public static final String PATH = "checkin";
        public static final int PATH_TOKEN = 310;
        public static final String PATH_FOR_ID = "checkin/*";
        public static final int PATH_FOR_ID_TOKEN = 320;
        // URI for all content stored as Restaurant entity
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
        public static final String CONTENT_TOPIC = "topic/org.coursera.checkin";
        private final static String MIME_TYPE_END = "checkin";
        // define the MIME type of data in the content provider
        public static final String CONTENT_TYPE_DIR = ORGANIZATIONAL_NAME + ".cursor.dir/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        public static final String CONTENT_ITEM_TYPE = ORGANIZATIONAL_NAME + ".cursor.item/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        // the names and order of ALL columns, including internal use ones
        public static final String[] ALL_COLUMN_NAMES = {
                Cols.ID,
                // ST:getColumnNames:inline
                Cols.CHECKINDATE, Cols.SEND, Cols.HOWBAD, Cols.PAINSTOP, Cols.TAKEMEDICATION, Cols.PHOTO_PATH, Cols.ID_PATIENT
                // ST:getColumnNames:complete
        };

        public static ContentValues initializeWithDefault(
                final ContentValues assignedValues) {
            // final Long now = Long.valueOf(System.currentTimeMillis());
            final ContentValues setValues = (assignedValues == null) ? new ContentValues()
                    : assignedValues;
            if (!setValues.containsKey(Cols.CHECKINDATE)) {
                setValues.put(Cols.CHECKINDATE, "");
            }
            if (!setValues.containsKey(Cols.SEND)) {
                setValues.put(Cols.SEND, 0);
            }
            if (!setValues.containsKey(Cols.HOWBAD)) {
                setValues.put(Cols.HOWBAD, "");
            }
            if (!setValues.containsKey(Cols.PAINSTOP)) {
                setValues.put(Cols.PAINSTOP, "");
            }
            if (!setValues.containsKey(Cols.TAKEMEDICATION)) {
                setValues.put(Cols.TAKEMEDICATION, 0);
            }
            if (!setValues.containsKey(Cols.PHOTO_PATH)) {
                setValues.put(Cols.PHOTO_PATH, "");
            }
            if (!setValues.containsKey(Cols.ID_PATIENT)) {
                setValues.put(Cols.ID_PATIENT, 0);
            }
            return setValues;
        }

        // a static class to store columns in entity
        public static class Cols {
            public static final String ID = BaseColumns._ID; // convention
            // The name and column index of each column in your database
            // ST:getColumnDeclaration:inline
            public static final String CHECKINDATE = "CHECKINDATE";
            public static final String SEND = "SEND";
            public static final String HOWBAD = "HOWBAD";
            public static final String PAINSTOP = "PAINSTOP";
            public static final String TAKEMEDICATION = "TAKEMEDICATION";
            public static final String PHOTO_PATH = "PHOTO_PATH";
            public static final String ID_PATIENT = "ID_PATIENT";
            
            // ST:getColumnDeclaration:complete
        }
    }
    
    // Define a static class that represents description of stored content entity.
    public static class CheckinMedication {
        // an identifying name for entity
        public static final String TABLE_NAME = "checkinmedication_table";
        public static final String PATH = "checkinmedication";
        public static final int PATH_TOKEN = 410;
        public static final String PATH_FOR_ID = "checkinmedication/*";
        public static final int PATH_FOR_ID_TOKEN = 420;
        // URI for all content stored as Restaurant entity
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
        public static final String CONTENT_TOPIC = "topic/org.coursera.checkinmedication";
        private final static String MIME_TYPE_END = "checkinmedication";
        // define the MIME type of data in the content provider
        public static final String CONTENT_TYPE_DIR = ORGANIZATIONAL_NAME + ".cursor.dir/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        public static final String CONTENT_ITEM_TYPE = ORGANIZATIONAL_NAME + ".cursor.item/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        // the names and order of ALL columns, including internal use ones
        public static final String[] ALL_COLUMN_NAMES = {
                Cols.ID,
                // ST:getColumnNames:inline
                Cols.TAKEIT, Cols.TAKEITDATE, Cols.TAKEITTIME, Cols.ID_CHECKIN, Cols.ID_PATIENTMEDICATION
                // ST:getColumnNames:complete
        };

        public static ContentValues initializeWithDefault(
                final ContentValues assignedValues) {
            // final Long now = Long.valueOf(System.currentTimeMillis());
            final ContentValues setValues = (assignedValues == null) ? new ContentValues()
                    : assignedValues;
            if (!setValues.containsKey(Cols.TAKEIT)) {
                setValues.put(Cols.TAKEIT, 0);
            }
            if (!setValues.containsKey(Cols.TAKEITDATE)) {
                setValues.put(Cols.TAKEITDATE, "");
            }
            if (!setValues.containsKey(Cols.TAKEITTIME)) {
                setValues.put(Cols.TAKEITTIME, "");
            }
            if (!setValues.containsKey(Cols.ID_CHECKIN)) {
                setValues.put(Cols.ID_CHECKIN, 0);
            }
            if (!setValues.containsKey(Cols.ID_PATIENTMEDICATION)) {
                setValues.put(Cols.ID_PATIENTMEDICATION, 0);
            }            
            return setValues;
        }

        // a static class to store columns in entity
        public static class Cols {
            public static final String ID = BaseColumns._ID; // convention
            // The name and column index of each column in your database
            // ST:getColumnDeclaration:inline
            public static final String TAKEIT = "TAKEIT";
            public static final String TAKEITDATE = "TAKEITDATE";
            public static final String TAKEITTIME = "TAKEITTIME";            
            public static final String ID_CHECKIN = "ID_CHECKIN";
            public static final String ID_PATIENTMEDICATION = "ID_PATIENTMEDICATION";            
            // ST:getColumnDeclaration:complete
        }
    }    
    
 // Define a static class that represents description of stored content entity.
    public static class PatientMedication {
        // an identifying name for entity
        public static final String TABLE_NAME = "patientmedication_table";
        public static final String PATH = "patientmedication";
        public static final int PATH_TOKEN = 510;
        public static final String PATH_FOR_ID = "patientmedication/*";
        public static final int PATH_FOR_ID_TOKEN = 520;
        // URI for all content stored as Restaurant entity
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
        public static final String CONTENT_TOPIC = "topic/org.coursera.patientmedication";
        private final static String MIME_TYPE_END = "patientmedication";
        // define the MIME type of data in the content provider
        public static final String CONTENT_TYPE_DIR = ORGANIZATIONAL_NAME + ".cursor.dir/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        public static final String CONTENT_ITEM_TYPE = ORGANIZATIONAL_NAME + ".cursor.item/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        // the names and order of ALL columns, including internal use ones
        public static final String[] ALL_COLUMN_NAMES = {
                Cols.ID,
                // ST:getColumnNames:inline
                Cols.NAME, Cols.SEND, Cols.ACTIVE, Cols.ID_PATIENT
                // ST:getColumnNames:complete
        };

        public static ContentValues initializeWithDefault(
                final ContentValues assignedValues) {
            // final Long now = Long.valueOf(System.currentTimeMillis());
            final ContentValues setValues = (assignedValues == null) ? new ContentValues()
                    : assignedValues;
            if (!setValues.containsKey(Cols.NAME)) {
                setValues.put(Cols.NAME, "");
            }
            if (!setValues.containsKey(Cols.SEND)) {
                setValues.put(Cols.SEND, 0);
            }
            if (!setValues.containsKey(Cols.ACTIVE)) {
                setValues.put(Cols.ACTIVE, 0);
            }
            if (!setValues.containsKey(Cols.ID_PATIENT)) {
                setValues.put(Cols.ID_PATIENT, 0);
            }            
            return setValues;
        }

        // a static class to store columns in entity
        public static class Cols {
            public static final String ID = BaseColumns._ID; // convention
            // The name and column index of each column in your database
            // ST:getColumnDeclaration:inline
            public static final String NAME = "NAME";
            public static final String SEND = "SEND";
            public static final String ACTIVE = "ACTIVE";
            public static final String ID_PATIENT = "ID_PATIENT";            
            // ST:getColumnDeclaration:complete
        }
    }    
    
 // Define a static class that represents description of stored content entity.
    public static class Status {
        // an identifying name for entity
        public static final String TABLE_NAME = "status_table";
        // define a URI paths to access entity       
        public static final String PATH = "getstatus";
        public static final int PATH_TOKEN = 610;
        public static final String PATH_FOR_ID = "getstatus/*";
        public static final int PATH_FOR_ID_TOKEN = 620;
        // URI for all content stored as Restaurant entity
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
        public static final String CONTENT_TOPIC = "topic/org.coursera.getstatus";
        private final static String MIME_TYPE_END = "getstatus";
        // define the MIME type of data in the content provider
        public static final String CONTENT_TYPE_DIR = ORGANIZATIONAL_NAME + ".cursor.dir/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        public static final String CONTENT_ITEM_TYPE = ORGANIZATIONAL_NAME + ".cursor.item/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        // the names and order of ALL columns, including internal use ones
        public static final String[] ALL_COLUMN_NAMES = {
                Cols.ID,
                // ST:getColumnNames:inline
                Cols.ROLE_NAME, Cols.DATE_INSERT
                // ST:getColumnNames:complete
        };

        public static ContentValues initializeWithDefault(
                final ContentValues assignedValues) {
            // final Long now = Long.valueOf(System.currentTimeMillis());
            final ContentValues setValues = (assignedValues == null) ? new ContentValues() : assignedValues;
            if (!setValues.containsKey(Cols.ROLE_NAME)) {
                setValues.put(Cols.ROLE_NAME, "");
            }
            if (!setValues.containsKey(Cols.DATE_INSERT)) {
                setValues.put(Cols.DATE_INSERT, "");
            }                      
            return setValues;
        }

        // a static class to store columns in entity
        public static class Cols {
            public static final String ID = BaseColumns._ID; // convention
            // The name and column index of each column in your database
            // ST:getColumnDeclaration:inline
            public static final String ROLE_NAME = "ROLE_NAME";
            public static final String DATE_INSERT = "DATE_INSERT";
            // ST:getColumnDeclaration:complete
        }
    }
    
}
// ST:BODY:end