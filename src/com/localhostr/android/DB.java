package com.localhostr.android;

import static com.localhostr.android.LocalhostrApp.TAG;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;

/**
 * Provides a mechanism for interacting with the database
 * 
 * @author Al
 *
 */
public final class DB extends SQLiteOpenHelper {

	/**
	 * Currently, only 2 plans exist. 
	 * 
	 * The user table's plan column should 
	 * map to one of these.
	 *
	 */
	public static class Plan {
		public static final Plan FREE = new Plan(0) {
			
			@Override
			public String toString() {
				return "Free";
			}
		};
		
		public static final Plan PREMIUM = new Plan(1) {
			
			@Override
			public String toString() {
				return "Premium";
			}
		};
		
		private int value;
		
		private Plan(int value) {
			this.value = value;
		}
		
		public static Plan readPlan(String str) {
			if (str.equalsIgnoreCase("free")) {
				return FREE;
			}
			
			else if (str.equalsIgnoreCase("premium")) {
				return PREMIUM;
			}
			
			else {
				Log.e(TAG, "Unknown plan " + str);
			}
			
			return FREE;
		}

		public static Plan readPlan(int plan) {
			if (plan == FREE.value) {
				return FREE;
			}
			
			else if (plan == PREMIUM.value) {
				return PREMIUM;
			}
			
			return FREE;
		}
	};
		
	/**
	 * Static values that can be used to reference
	 * columns in the user table
	 */
	public static final class USER_COLUMNS {
		public static final String ID = "_id";
		public static final String EMAIL = "email";
		public static final String PASSWORD = "password";
		public static final String DAILY_ALLOWANCE = "daily_allowance";
		public static final String MAX_FILESIZE = "max_filesize";
		public static final String FILE_COUNT = "num_files";
		public static final String PLAN = "plan";
		public static final String UPLOADS_TODAY = "uploads_today";	
	}
	
	/**
	 * Static values that can be used to reference
	 * columns in the files table
	 */
	public static final class FILES_COLUMNS {
		public static final String ID = "_id";
		public static final String NAME = "name";
		public static final String WEB_LINK = "link";
		public static final String FILE_TYPE = "type";
		public static final String FILE_ID = "file_id";
		public static final String FILE_SIZE = "size";
		public static final String DATE_ADDED = "date_added";
		public static final String LINK_SMALL = "link_small";
		public static final String LINK_LARGE = "link_large";
		public static final String DOWNLOADS = "downloads";
		public static final String THUMBNAIL = "thumbnail";
		public static final String FOLDER = "folder";		
	}
	
	/**
	 * Static values that can be used to reference
	 * columns in the folders table
	 */
	public static final class FOLDERS_COLUMNS {
		public static final String ID = "_id";
		public static final String NAME = "name";
		public static final String WEB_LINK = "link";
		public static final String DATE_ADDED = "date_added";
		public static final String FOLDER_ID = "folder_id";
		public static final String PRIVATE = "private";
	}
	
	private static final String DB_NAME = "localhostr.db";
	
	/*
	 * Increment this when changes to the db structure 
	 * are necessary so onUpgrade will be called
	 */
	private static final int DB_VERSION = 1;
	
	private static final String TABLE_USER = "user";
	private static final String TABLE_FILES = "files";
	private static final String TABLE_FOLDERS = "folders";
		  
	private static final String CREATE_TABLE_USER = 
		new StringBuilder("CREATE TABLE IF NOT EXISTS ")
			.append(TABLE_USER)
			.append(" (").append(USER_COLUMNS.ID).append(" INTEGER ").append("PRIMARY KEY AUTOINCREMENT, ")
			.append(USER_COLUMNS.DAILY_ALLOWANCE).append(" INTEGER NOT NULL DEFAULT 15, ")
			.append(USER_COLUMNS.MAX_FILESIZE).append(" INTEGER, ")
			.append(USER_COLUMNS.FILE_COUNT).append(" INTEGER DEFAULT 0, ")
			.append(USER_COLUMNS.PLAN).append(" INTEGER NOT NULL DEFAULT " + Plan.FREE.value + ", ")
			.append(USER_COLUMNS.UPLOADS_TODAY).append(" INTEGER DEFAULT 0, ")
			.append(USER_COLUMNS.EMAIL).append(" TEXT NOT NULL, ")
			.append(USER_COLUMNS.PASSWORD).append(" TEXT NOT NULL) ")
		.toString();
	
	private static final String CREATE_TABLE_FILES = 
		new StringBuilder("CREATE TABLE IF NOT EXISTS ")
			.append(TABLE_FILES)
			.append(" (" + FILES_COLUMNS.ID).append(" INTEGER ").append("PRIMARY KEY AUTOINCREMENT, ")
			.append(FILES_COLUMNS.NAME).append(" TEXT NOT NULL, ")
			.append(FILES_COLUMNS.WEB_LINK).append(" TEXT, ")
			.append(FILES_COLUMNS.FILE_TYPE).append(" TEXT, ")
			.append(FILES_COLUMNS.FILE_ID).append(" TEXT NOT NULL, ")
			.append(FILES_COLUMNS.FILE_SIZE).append(" INTEGER DEFAULT 0, ")
			.append(FILES_COLUMNS.DATE_ADDED).append(" TEXT NOT NULL, ")
			.append(FILES_COLUMNS.LINK_SMALL).append(" TEXT, ")
			.append(FILES_COLUMNS.LINK_LARGE).append(" TEXT, ")
			.append(FILES_COLUMNS.DOWNLOADS).append(" INTEGER, ")
			.append(FILES_COLUMNS.THUMBNAIL).append(" TEXT, ")
			.append(FILES_COLUMNS.FOLDER).append(" INTEGER DEFAULT -1)")
		.toString();
	
	private static final String CREATE_TABLE_FOLDERS = 
		new StringBuilder("CREATE TABLE IF NOT EXISTS ")
			.append(TABLE_FOLDERS)
			.append(" (" + FOLDERS_COLUMNS.ID).append(" INTEGER ").append("PRIMARY KEY AUTOINCREMENT, ")
			.append(FOLDERS_COLUMNS.NAME).append(" TEXT NOT NULL, ")
			.append(FOLDERS_COLUMNS.WEB_LINK).append(" TEXT, ")
			.append(FOLDERS_COLUMNS.FOLDER_ID).append(" TEXT NOT NULL, ")
			.append(FOLDERS_COLUMNS.PRIVATE).append(" INTEGER DEFAULT 1, ")
			.append(FOLDERS_COLUMNS.DATE_ADDED).append(" TEXT) ")
		.toString();

	/**
	 * Do not call this directly. Instead,
	 * use {@link LocalhostrApp#getDB()} in 
	 * order to reuse an existing instance
	 * of this class
	 * 
	 * @param context
	 * @see Utils#getDB(Context)
	 * @see LocalhostrApp#getDB()
	 */
	public DB(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	/*
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_USER);
		db.execSQL(CREATE_TABLE_FOLDERS);
		db.execSQL(CREATE_TABLE_FILES);		
	}

	/*
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onConfigure(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onConfigure(SQLiteDatabase db) {
		//foreign keys are off by default according
		//to SQLite docs, this turns it on
		db.execSQL("PRAGMA foreign_keys = ON;");
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	/**
	 * Save information about the currently logged in user
	 * 
	 * @param email
	 * @param password
	 * @param allowance
	 * @param plan
	 * @param maxFileSize
	 * @param numFiles
	 * @param uploadsToday
	 */
	public void saveUserDetails(String email, String password,
			String allowance, String plan, String maxFileSize, String numFiles,
			String uploadsToday) {	
		
		ContentValues cv = new ContentValues(7);
		cv.put(USER_COLUMNS.EMAIL, email);
		cv.put(USER_COLUMNS.PASSWORD, password);
		cv.put(USER_COLUMNS.DAILY_ALLOWANCE, allowance);
		cv.put(USER_COLUMNS.PLAN, Plan.readPlan(plan).value);
		cv.put(USER_COLUMNS.MAX_FILESIZE, Integer.parseInt(maxFileSize));
		cv.put(USER_COLUMNS.FILE_COUNT, numFiles);
		cv.put(USER_COLUMNS.UPLOADS_TODAY, uploadsToday);		
		getWritableDatabase().insert(TABLE_USER, null, cv);
		
	}
	
	/**
	 * Check if a user already exists
	 * 
	 * @return
	 */
	public boolean haveUser() {
		
		Cursor c = getReadableDatabase().query(
				TABLE_USER, new String[] { USER_COLUMNS.EMAIL}, 
				null, null, null, null, null);
		
		int count = c.getCount();
		c.close();
		
		return count > 0;
	}

	/**
	 * Retrieves the information about the currently logged in user
	 * @return an instance of {@link ContentValues}, values of which can
	 * be retrieved using the {@link USER_COLUMNS} values as keys
	 */
	public ContentValues getAccountInfo() {
		Cursor c = getReadableDatabase().query(TABLE_USER, null, null, null, null, null, null);
		
		ContentValues cv = new ContentValues();
		
		if (c.moveToFirst()) {
			cv.put(USER_COLUMNS.EMAIL, c.getString(c.getColumnIndex(USER_COLUMNS.EMAIL)));
			cv.put(USER_COLUMNS.PLAN, c.getInt(c.getColumnIndex(USER_COLUMNS.PLAN)));
			cv.put(USER_COLUMNS.DAILY_ALLOWANCE, c.getInt(c.getColumnIndex(USER_COLUMNS.DAILY_ALLOWANCE)));
			cv.put(USER_COLUMNS.MAX_FILESIZE, c.getInt(c.getColumnIndex(USER_COLUMNS.MAX_FILESIZE)));
			cv.put(USER_COLUMNS.FILE_COUNT, c.getInt(c.getColumnIndex(USER_COLUMNS.FILE_COUNT)));	
		}
		
		c.close();
		
		return cv;
	}

	/**
	 * Clear all the information relating to the user, their files
	 * and folders when logging out.
	 */
	public void clearForLogout() {
		getWritableDatabase().execSQL("DELETE FROM " + TABLE_USER);
		getWritableDatabase().execSQL("DELETE FROM " + TABLE_FILES);
		getWritableDatabase().execSQL("DELETE FROM " + TABLE_FOLDERS);		
	}

	/**
	 * Create a Base64 value holding the user's email and password
	 * to be used when negotiating Basic authentication against the 
	 * Localhostr API url
	 * @return
	 */
	public String makeAuthString() {
		Cursor c = getReadableDatabase().query(TABLE_USER, 
				new String[] { USER_COLUMNS.EMAIL, USER_COLUMNS.PASSWORD}, 
				null, null, null, null, null);
		

		String base64 = "";
		
		if (c.moveToFirst()) {
			String email = c.getString(c.getColumnIndex(USER_COLUMNS.EMAIL));
			String password = c.getString(c.getColumnIndex(USER_COLUMNS.PASSWORD));
			
			base64 = 
				Base64.encodeToString((email + ":" + password).getBytes(), 
						Base64.NO_WRAP);
		}
		
		c.close();
		return base64;
	}

	/**
	 * Add a single file to the database
	 * @param cv
	 * @return the id of the newly inserted row, or -1 if there was an error
	 */
	public int addFile(ContentValues cv) {
		
		return (int) getWritableDatabase().insertOrThrow(TABLE_FILES, null, cv);
		
	}
	
	/**
	 * Add a number of files to the database as a batch job. 
	 * <br> <br>
	 * This is preferred against {@link #addFile(ContentValues)}
	 * as it uses SQL transactions, which are much quicker 
	 * than adding one file at a time.
	 * 
	 * @param batchList
	 */
	public void doBatchFileAdd(List<ContentValues> batchList) {
		final SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		
		if (mFilesListener != null) {
			mFilesListener.onBatchAddStarted();
		}
		
		try {
			for (int i = 0; i<batchList.size(); i++) {
				addFile(batchList.get(i));
			}
			
			db.setTransactionSuccessful();
		}
		
		catch (SQLiteException sqe) {
			sqe.printStackTrace();
		}
		
		finally {
			db.endTransaction();
		}
		
		if (mFilesListener != null) {
			mFilesListener.onBatchAddFinished();
		}
		
	}

	/**
	 * Get retrieve the files list
	 * @param columns the name of columns to retrieve
	 * @return
	 * @see FILES_COLUMNS
	 */
	public Cursor getFilesList(final String[] columns) {
		String[] realColumns = new String[columns.length+1];
		/*
		 * The [Simple]CursorAdapter requires _id to exist in
		 * the cursor to function. However, this shouldn't
		 * be passed in `columns` as it maps the value of _id 
		 * to one of the TextViews.
		 * 
		 * Therefore, we add it in ourselves here.
		 */
		realColumns[0] = FILES_COLUMNS.ID;
		System.arraycopy(columns, 0, realColumns, 1, columns.length);
		
		final Cursor c = getReadableDatabase().query(
				TABLE_FILES, 
				realColumns, null, null, null, null, null);
		return c;
	}

	public void setFilesTableListener(FilesTableChangeListener listener) {
		mFilesListener = listener;
	}
	
	public void removeFilesTableListener(FilesTableChangeListener listener) {
		if (mFilesListener == listener) {
			mFilesListener = null;
		}
	}
	
	private FilesTableChangeListener mFilesListener;
	
	/**
	 * Allows a client to listener for changes to the Files table
	 * @author Al
	 *
	 */
	public static interface FilesTableChangeListener {
		
		public void onBatchAddStarted();
		public void onBatchAddFinished();
		public void onFilesListCleared();
	}

	public void clearFilesTable() {
		getWritableDatabase().delete(TABLE_FILES, null, null);
		if (mFilesListener != null) {
			mFilesListener.onFilesListCleared();
		}
		
	}
}
