package org.coursera.symptom.utils;

import java.io.File;

import android.os.Environment;

/**
 * Class for Froyo systems
 * 
 */
public final class FroyoAlbumDirFactory extends AlbumStorageDirFactory {

	/**
	 * Another way to build Album directories.
	 */
	@Override
	public File getAlbumStorageDir(String albumName) {
		return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
	}
}
