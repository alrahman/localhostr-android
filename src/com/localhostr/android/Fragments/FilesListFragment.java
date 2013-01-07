package com.localhostr.android.Fragments;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.MenuItem;
import com.localhostr.android.DB;
import com.localhostr.android.DB.FILES_COLUMNS;
import com.localhostr.android.DB.FilesTableChangeListener;
import com.localhostr.android.Home;
import com.localhostr.android.R;
import com.localhostr.android.Utils;
import com.localhostr.android.ViewFolder;

/**
 * The Fragment which shows a list of files.
 * 
 * @author Al
 *
 */
public class FilesListFragment extends SherlockListFragment
	implements FilesTableChangeListener {

	private Cursor mCursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

	}

	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu,
			com.actionbarsherlock.view.MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		getSherlockActivity().getSupportMenuInflater()
		.inflate(R.menu.files_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_upload:
				final Activity activity = getActivity();
				if (activity instanceof Home) {
					((Home) activity).showUploadScreen();
				}

				else if (activity instanceof ViewFolder) {
					//TODO: Send intent to Home
					//with correct tab selection 
					//and Upload fragment to remember folder
				}

				return true;

			case android.R.id.home:
				//TODO: Fix this to use
				//the task stack builder
				getActivity().finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private ActionMode mActionMode;

	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				if (mActionMode == null) {

					/*
					 * Only Honeycomb and above support the PopupMenu widget
					 */
					if (Build.VERSION.SDK_INT > VERSION_CODES.HONEYCOMB) {
						PopupMenu pm = new PopupMenu(getActivity(), 
								view.findViewById(R.id.popup_anchor));
						pm.getMenuInflater().inflate(R.menu.file_popup_menu, 
								pm.getMenu());

						pm.setOnMenuItemClickListener(new OnMenuItemClickListener() {

							@Override
							public boolean onMenuItemClick(android.view.MenuItem item) {
								switch (item.getItemId()) {
									case R.id.menu_copy_file_link:
										break;

									case R.id.menu_view_file:
										break;

									case R.id.menu_delete_file:
										break;
								}

								return true;
							}
						});

						pm.show();
					}

					else {
						//TODO: Show context menu
					}
				}

				else {
					//TODO: Set a background for items so they reflect the 
					//checked state
					ListView list = (ListView) arg0;
					if (list.isItemChecked(position)) {
						list.setItemChecked(position, false);
					} 

					else {
						list.setItemChecked(position, true);
					}
				}
			}
		});

		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				mActionMode = 
					((SherlockFragmentActivity) getActivity()).startActionMode(new Callback() {

						@Override
						public boolean onCreateActionMode(ActionMode mode,
								com.actionbarsherlock.view.Menu menu) {
							menu.add("Delete").setIcon(android.R.drawable.ic_menu_delete);
							return true;
						}

						@Override
						public boolean onPrepareActionMode(ActionMode mode,
								com.actionbarsherlock.view.Menu menu) {
							// TODO Auto-generated method stub
							return true;
						}

						@Override
						public boolean onActionItemClicked(ActionMode mode,
								MenuItem item) {
							// TODO Auto-generated method stub
							return false;
						}

						@Override
						public void onDestroyActionMode(ActionMode mode) {
							mActionMode = null;
						}

					}); 

				return true;
			}
		});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Utils.getDB(activity).setFilesTableListener(this);

		initLayout();

	}

	@Override
	public void onDetach() {
		super.onDetach();
		Utils.getDB(getActivity()).removeFilesTableListener(this);
	}

	private void initLayout() {
		final int layout = R.layout.files_list_row;
		final DB db = Utils.getDB(getActivity());

		final String[] columns = new String[] {
				FILES_COLUMNS.NAME, 
				FILES_COLUMNS.FILE_SIZE, 
				FILES_COLUMNS.FILE_TYPE
		};

		mCursor = db.getFilesList(columns);

		int[] to = new int[] { R.id.file_name, R.id.file_size, R.id.file_type};

		SimpleCursorAdapter sca = new SimpleCursorAdapter(getActivity(), 
				layout, mCursor, columns, to, 0);
		sca.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIdx) {
				if (view.getId() == R.id.file_size) {
					int size = cursor.getInt(columnIdx);

					((TextView) view).setText("(" + getReadableFileSizeString(size) + ")");

					return true;
				}

				return false;
			}
		});

		setListAdapter(sca);

	}

	private class FileInfo {
		public final String Name;
		public final String Size;
		public final String Type;

		public FileInfo(String name, String size, String type) {
			Name = name;
			Size = size;
			Type = type;
		}
	}

	private class FilesAdapter extends ArrayAdapter<FileInfo> {


		public FilesAdapter(Context context, 
				int textViewResourceId, List<FileInfo> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = getActivity().getLayoutInflater().inflate
			(R.layout.files_list_row, parent, false);

			FileInfo fi = getItem(position);

			((TextView) view.findViewById(R.id.file_name))
			.setText(fi.Name + " - " + fi.Size);

			((TextView) view.findViewById(R.id.file_type))
			.setText(fi.Type);

			return view;
		}

	}

	private void setupEmptyList() {
		if (mCursor != null) {
			setListAdapter(null);
			mCursor.close();
			mCursor = null;
		}

		setListShown(false);
	}

	@Override
	public void onBatchAddStarted() {	
		setupEmptyList();
	}

	@Override
	public void onBatchAddFinished() {
		setListShown(true);
		initLayout();
	}

	@Override
	public void onFilesListCleared() {
		setupEmptyList();

	}

	public String getReadableFileSizeString(int bytes) {

		int i = -1;
		String[] byteUnits = new String[] {
				"kB", "MB", "GB"
		};

		do {
			bytes /= 1024;
			i++;
		} while (bytes > 1024);

		return Math.max(bytes, 0.1) + byteUnits[i];
	};
}