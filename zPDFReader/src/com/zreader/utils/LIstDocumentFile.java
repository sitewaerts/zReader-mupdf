package com.zreader.utils;

import java.io.File;

/**
 * TODO reconstruction...
 * @author philipp
 *
 */
public class LIstDocumentFile {

	public File file;
	public String name;
	public String fileName;
	
	public LIstDocumentFile(File file, String name) {
		this.file = file;
		this.fileName = file.getName();
		this.name = name;
	}

	public int compareToIgnoreCase(LIstDocumentFile other) {
		return this.fileName.toLowerCase().compareTo(other.fileName.toLowerCase());
	}
}
