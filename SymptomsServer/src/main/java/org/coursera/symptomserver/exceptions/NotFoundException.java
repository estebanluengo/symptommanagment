package org.coursera.symptomserver.exceptions;

/**
 * Class Exception to indicate a Not found resource
 */
public class NotFoundException extends GeneralException{
    
    private static final long serialVersionUID = -2213521416856589064L;

    /**
     * Constructor class
     * @param errorCode a String with error code. This error code corresponds to value in messages.properties file
     */
	public NotFoundException(String errorCode){
        super(errorCode);
    }
}
