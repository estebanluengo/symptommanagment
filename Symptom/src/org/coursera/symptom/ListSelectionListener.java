package org.coursera.symptom;

import org.coursera.symptom.orm.Checkin;

/**
 * Interface to attach ListActivity classes to receive info that a checkin row has been selected
 *
 */
public interface ListSelectionListener {
	/**
	 * Called when a Checkin row has been selected
	 * @param index a int that represents index row selected
	 * @param checkin a Checkin object
	 */
	public void onListSelection(int index, Checkin checkin);
}
