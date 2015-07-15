package org.coursera.symptomserver.errors;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains field error information
 */
public class FieldError {

	//Field name that don't match criteria validation
    private String field;
    //Textual human information with the error
    private String message;
    
    public FieldError(){
        
    }

    public FieldError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    @JsonProperty("field")
    public String getField() {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
