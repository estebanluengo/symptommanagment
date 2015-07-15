package org.coursera.symptomserver.exceptions;

/**
 * Class Exception to indicate a General Exceptions
 */
public class GeneralException extends RuntimeException{    
    
	private static final long serialVersionUID = 3589525101096033839L;
	private String errorCode;
    private Exception exception;
    
    public GeneralException(){
        
    }
    
    /**
     * Constructor class
     * @param errorCode a String with error code. This error code corresponds to value in messages.properties file
     */
    public GeneralException(String errorCode){
        this.errorCode = errorCode;
    }
    
    /**
     * Constructor class
     * @param errorCode a String with error code. This error code corresponds to value in messages.properties file
     * @param exception a Exception object with origina Exception
     */
    public GeneralException(String errorCode, Exception exception){
        this.errorCode = errorCode;
        this.exception = exception;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }
    
    
}
