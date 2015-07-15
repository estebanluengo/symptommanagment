package org.coursera.symptomserver.beans;

/**
 * Bean container that represents a photo upload by the mobile app
 * 
 */
public class Photo {
	//All photo files will have this prefix as a file name
	public static final String PREFIX = "photo_";	
	//The original photo name
	private String name;
	//The full file name with the path in the local filesystem
	private String fileName;
	
	public Photo(){
	}

	public Photo(String name, String fileName) {
		super();
		this.name = name;
		this.fileName = fileName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
}
