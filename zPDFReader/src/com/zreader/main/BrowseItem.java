package com.zreader.main;

import java.io.File;

/**
 * TODO reconstruction...
 * @author philipp
 *
 */
public class BrowseItem {

	private String path;
	private String name;
	private int iconFolderId;
	
	public void setPath(String path) {
		this.path = path;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setIcon(int icFolder) {
		this.iconFolderId = icFolder;
	}

	public String getPath() {
		return this.path;
	}

	public String getName() {
		return this.name;
	}

	public int getIcon() {
		return this.iconFolderId;
	}

	public File getFile() {
		// TODO Auto-generated method stub
		return null;
	}
}
