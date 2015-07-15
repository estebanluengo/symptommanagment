package org.coursera.symptomserver.exceptions;

/**
 * Class Exception to indicate a Unknown user's role
 */
public class AccessException extends GeneralException{

    private static final long serialVersionUID = 6143803797523684042L;

    /**
     * Constructor class
     * @param errorCode a String with error code. This error code corresponds to value in messages.properties file
     */
	public AccessException(String errorCode) {
        super(errorCode);
    }
}
