/* 
**
** Copyright 2014, Jules White
**
** 
*/
package org.coursera.symptom.client;

/**
 * An interface that it is used in conjunction with CallableTask class
 * to indicate an object that will be used as a callback when the work is done
 * in background.
 */
public interface TaskCallback<T> {

	/**
	 * Method that will be execute if work in background finish successfully
	 * @param result a result that work in background produces
	 */
    public void success(T result);

    /**
     * Method that will be execute if work in background throws an Exception
     * @param e the exception that it is thrown from background work
     */
    public void error(Exception e);

}
