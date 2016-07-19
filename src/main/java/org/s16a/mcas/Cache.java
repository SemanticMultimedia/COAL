package org.s16a.mcas;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Cache {

	private static String BASE_PATH = "cache/";
	private String hash;
	private String url;

	public Cache(String url) {
		this.url = url;
		this.hash = hash(url);
	}

	public String getCacheId(){
		return this.hash;
	}

	public String getUrl(){
		return this.url;
	}

	public static String hash(String input) {
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.update(input.getBytes(), 0, input.length());
		return new BigInteger(1, m.digest()).toString(16);
	}

	private static String getFileExtension(File file) {
		String name = file.getName();
		try {
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}

	public String getPath(){
		String path = BASE_PATH + this.hash + '/';
		File dir = new File(path);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		return path;
	}

	public String getFilePath(String fileName){
		return getPath() + fileName;
	}

	public File getResourceFile(String fileName){
		return new File(getResourceFilePath());
	}

	public String getResourceFilePath(){
		return getPath() + "data." + getResourceFileExtension();
	}

	public String getResourceFileExtension(){
		return getFileExtension(new File(this.url));
	}

}
