package com.localhostr.android;

import android.app.Application;

public class LocalhostrApp extends Application {

	public static final boolean DEBUG = true;
	public static final String TAG = "Localhostr";

	private DB mDb;
	
	public DB getDB() {
	
		if (mDb == null) {
			mDb = new DB(this);
		}
		
		return mDb;
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		
		if (mDb != null) {
			mDb.close();
			mDb = null;
		}
	}
}
