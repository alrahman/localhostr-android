package com.localhostr.android;

/**
 * Holds constants that are useful for when making API requests
 * 
 * @author Al
 *
 */
public final class Constants {

	public static final String API_URL = "https://api.localhostr.com";
	public static final String ACCEPT_HEADER = "application/vnd.localhostr.com.customer-v0+json";

	public static final String REQUEST_USER_DETAILS = API_URL + "/user";
	
	public static final String BASE_FILES_URL = API_URL + "/file";
	public static final String REQUEST_SINGLE_FILE = BASE_FILES_URL + "/%s";
	public static final String REQUEST_DELETE_FILE = BASE_FILES_URL + "/%s";
	
	public static final String BASE_FOLDERS_URL = API_URL + "/folder";
	public static final String REQUEST_SINGLE_FOLDER = BASE_FOLDERS_URL + "/%s";
	public static final String REQUEST_CREATE_FOLDER = API_URL + "/folder/";
	
	public static final String POST_FILE_URL = API_URL + "/file";
}
