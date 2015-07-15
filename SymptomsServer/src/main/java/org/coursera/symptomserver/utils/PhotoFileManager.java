package org.coursera.symptomserver.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.coursera.symptomserver.beans.Photo;

/**
 * Class to handle with photos in the file system
 * @author Dr Jules
 * @author me
 */
public class PhotoFileManager {

	public static PhotoFileManager get() throws IOException {
		return new PhotoFileManager();
	}

	//Base path to save photos
	private Path targetDir_ = Paths.get("photos");

	public PhotoFileManager() throws IOException {
		if (!Files.exists(targetDir_)) {
			Files.createDirectories(targetDir_);
		}
	}

	// Private helper method for resolving photo file paths
	private Path getPhotoPath(Photo p) {
		return targetDir_.resolve(Photo.PREFIX + p.getFileName());
	}

	/**
	 * This method returns true if the specified photo has binary data stored on
	 * the file system.
	 * 
	 * @param p an Photo object that contains the file name
	 * @return a boolean 
	 */
	public boolean hasPhotoData(Photo p) {
		Path source = getPhotoPath(p);
		return Files.exists(source);
	}
	
	/**
	 * This method returns true if the specified photo has binary data stored on
	 * the file system.
	 * 
	 * @param path represents the path in the file system 
	 * @return a boolean 
	 */
	public boolean hasPhotoData(String path) {
		Path source = Paths.get(path);
		return Files.exists(source);
	}

	/**
	 * This method copies the binary data for the given photo to the provided
	 * output stream. The caller is responsible for ensuring that the specified
	 * Photo has binary data associated with it. If not, this method will throw
	 * a FileNotFoundException.
	 * 
	 * @param Photo p a object that contains the file name
	 * @param out a OutputStream object
	 * @throws IOException
	 */
	public void copyPhotoData(Photo p, OutputStream out) throws IOException {
		Path source = getPhotoPath(p);
		if (!Files.exists(source)) {
			throw new FileNotFoundException("Unable to find the referenced photo file for photo:" + p.getName());
		}
		Files.copy(source, out);
	}
	
	/**
	 * This method copies the binary data for the given photo to the provided
	 * output stream. The caller is responsible for ensuring that the specified
	 * Photo has binary data associated with it. If not, this method will throw
	 * a FileNotFoundException.
	 * 
	 * @param String path a String with file path
	 * @param out a OutputStream object to write to
	 * @throws IOException
	 */
	public void copyPhotoData(String path, OutputStream out) throws IOException {
		Path source = Paths.get(path);
		if (!Files.exists(source)) {
			throw new FileNotFoundException("Unable to find the referenced photo file for photo:" + path);
		}
		Files.copy(source, out);
	}

	/**
	 * This method reads all of the data in the provided InputStream and stores
	 * it on the file system. The data is associated with the Video object that
	 * is provided by the caller.
	 * 
	 * @param p a object that contains the file name
	 * @param photoData a InputStream to read from
	 * @throws IOException
	 */
	public String savePhotoData(Photo p, InputStream photoData)
			throws IOException {
		assert (photoData != null);

		Path target = getPhotoPath(p);
		Files.copy(photoData, target, StandardCopyOption.REPLACE_EXISTING);
		return target.toString();
	}

}
