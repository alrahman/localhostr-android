package com.localhostr.android;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.Window;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.localhostr.android.DB.FILES_COLUMNS;
import com.localhostr.android.Fragments.PagerFragment;
import com.localhostr.android.Fragments.Sidebar;

/**
 * The main Activity provided in order to interact with all 
 * parts of the app. 
 * 
 * @author Al
 *
 */
public class Home extends SherlockFragmentActivity 
	implements LoaderCallbacks<String> {

	private static final int LOADER_FILES = 0;
	private static final int LOADER_FOLDERS = 1;
	private static final int LOADER_THUMBS = 2;
	
	public static final String EXTRA_FROM_LOGIN = "from_login_screen";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		final boolean haveUser = Utils.getDB(this).haveUser();
		
		if (! haveUser) {
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		}

		if (getIntent().getBooleanExtra(EXTRA_FROM_LOGIN, false)) {
			getIntent().removeExtra(EXTRA_FROM_LOGIN);
			setProgressBarIndeterminateVisibility(true);
			Toast.makeText(this, "Loading your files & folders...", Toast.LENGTH_SHORT).show();
			getSupportLoaderManager().initLoader(LOADER_FILES, null, this).forceLoad();
			getSupportLoaderManager().initLoader(LOADER_FOLDERS, null, this).forceLoad();
		}

		setContentView(R.layout.main);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.home_menu, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_refresh:
				getSupportLoaderManager().initLoader(LOADER_FILES, null, this).forceLoad();
				getSupportLoaderManager().initLoader(LOADER_FOLDERS, null, this).forceLoad();
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<String> onCreateLoader(int id, Bundle arg1) {
		switch (id){
			case LOADER_FILES:
				return new DetailsLoader(this, Constants.BASE_FILES_URL);
				
			case LOADER_FOLDERS:
				return new DetailsLoader(this, Constants.BASE_FOLDERS_URL);
		}
		
		return null;
	}

	@Override
	public void onLoadFinished(Loader<String> loader, String results) {
		if (!getSupportLoaderManager().hasRunningLoaders()) {
			setProgressBarIndeterminateVisibility(false);
		}
		
		try {
			final JSONArray ja = new JSONArray(results);
			final JSONObject jo = ja.getJSONObject(0);
			
			if (jo.has("exception")) {
				//TODO
				return;
			}
			
			else if (jo.has("error")) {
				//TODO
				return;
			}
			
			else {
				switch (loader.getId()) {
					case LOADER_FILES:
						parseFileList(ja);
						break;
						
					case LOADER_FOLDERS:
						parseFolderList(ja);
						break;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(results);
	}

	private void parseFolderList(JSONArray results) {
		// TODO Auto-generated method stub
		
	}


	private void parseFileList(JSONArray results) {
		System.out.println(results.length());

		DB db = Utils.getDB(this);
		db.clearFilesTable();
		int parsed = 0;
		
		ArrayList<ContentValues> values = new ArrayList<ContentValues>();
		
		for (int i = 0; i<results.length(); i++) {
			try {
				JSONObject jo = results.getJSONObject(i);
				final String added = jo.getString("added");
				final String name = jo.getString("name");
				final int downloads = jo.getInt("downloads");
				String lowertype = jo.getString("type");
				final String type = 
					lowertype.substring(0, 1).toUpperCase() 
					+ lowertype.substring(1).toLowerCase();
				String small = null;
				String large = null;
				
				if (type.equalsIgnoreCase("image")) {
					try {
						JSONObject links = jo.getJSONObject("direct");
						
						small = links.getString("150x");
						//TODO: Does large always exists?
						large = links.getString("930x");
					}
					
					catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				final String href = jo.getString("href");
				final String id = jo.getString("id");
				final int size = jo.getInt("size");
					
				ContentValues cv = new ContentValues();
					
				cv.put(FILES_COLUMNS.FILE_ID, id);
				cv.put(FILES_COLUMNS.NAME, name);
				cv.put(FILES_COLUMNS.FILE_TYPE, type);
				cv.put(FILES_COLUMNS.DATE_ADDED, added);
				cv.put(FILES_COLUMNS.DOWNLOADS, downloads);
				cv.put(FILES_COLUMNS.WEB_LINK, href);
				cv.put(FILES_COLUMNS.FILE_SIZE, size);
					
				if (small != null) {
					cv.put(FILES_COLUMNS.LINK_SMALL, small);
				}
				
				if (large != null) {
					cv.put(FILES_COLUMNS.LINK_LARGE, large);
				}
					
				values.add(cv);
				
				parsed++;
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}

		}
		
		db.doBatchFileAdd(values);
	}


	@Override
	public void onLoaderReset(Loader<String> loader) {
	}

	public static class DetailsLoader extends AsyncTaskLoader<String> {
	
		private final Context mContext;
		private final String mUrl;
		
		public DetailsLoader(Context context, String url) {
			super(context);
			mContext = context;
			mUrl = url;
		}
	
		@Override
		public String loadInBackground() {
			DB db = Utils.getDB(mContext);
			String authString = db.makeAuthString();
			String jsonResult = null;
			
			try {
				jsonResult = Utils.makeApiRequest(mUrl, authString);
				
				//jsonResult = Utils.getSampleFilesList();
					
			} catch (IOException e) {
				e.printStackTrace();
				
				return Utils.createJSONStringFromException(e, true);
			}
			
			return jsonResult;
		}
	}

	public void showUploadScreen() {
		if (getResources().getBoolean(R.bool.dual_pane)) {
			Sidebar sidebar = (Sidebar) getSupportFragmentManager()
			.findFragmentById(R.id.sidebar_fragment);
			sidebar.showUploadsScreen();
		}
		
		else {
			PagerFragment pf = (PagerFragment) getSupportFragmentManager()
					.findFragmentById(R.id.pager_fragment);
			pf.showUploadsScreen();
		}
	}
}
