package org.coursera.symptom.utils;

import java.io.File;

import android.os.Environment;

/**
 * 
 * A base class for album directory
 */
public final class BaseAlbumDirFactory extends AlbumStorageDirFactory {

	//photos standard directory  
	private static final String CAMERA_DIR = "/dcim/";

	@Override
	public File getAlbumStorageDir(String albumName) {
		return new File (Environment.getExternalStorageDirectory() + CAMERA_DIR + albumName
		);
	}
}
