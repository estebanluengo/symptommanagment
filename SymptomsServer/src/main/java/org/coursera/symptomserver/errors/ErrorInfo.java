package org.coursera.symptomserver.errors;

/**
 * Contains error information that it is send to clients.
 * 
 */
public class ErrorInfo {

	//URL that produces the error
    private String url;  
    //Original error that it occurred 
    private Throwable exception;
    //Textual human information with the error
    private String text;
    
    public ErrorInfo(String url, String text){
        this.url = url;
        this.text = text;
    }
    
    public ErrorInfo(String url, Throwable exception) {
        this.url = url;
        this.exception = exception;
    }
    
    public ErrorInfo(String url, Throwable exception, String text) {
        this.url = url;
        this.exception = exception;
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    
}
