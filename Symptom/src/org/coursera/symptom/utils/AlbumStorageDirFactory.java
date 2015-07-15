package org.coursera.symptom.utils;

import java.io.File;

/**
 * An abstrac factory class to build Album directory
 *
 */
abstract public class AlbumStorageDirFactory {
	public abstract File getAlbumStorageDir(String albumName);
}