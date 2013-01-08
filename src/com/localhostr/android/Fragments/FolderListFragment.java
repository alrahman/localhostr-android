package com.localhostr.android.Fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.MenuItem;
import com.localhostr.android.R;
import com.localhostr.android.ViewFolder;

/**
 * The Fragment which shows the list of folders.
 * 
 * @author Al
 *
 */
public class FolderListFragment extends SherlockListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);

	}

	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu,
			com.actionbarsherlock.view.MenuInflater inflater) {
		getSherlockActivity().getSupportMenuInflater().inflate(R.menu.folder_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		
		ArrayList<FolderInfo> folders = new ArrayList<FolderInfo>();
		String[] FolderNames = new String[] { "Folder A", "Folder B",
				"Folder C", "Folder D"};
		
		for (int i = 0; i<15; i++) {
			folders.add(new FolderInfo(FolderNames[i % FolderNames.length], 
					Math.abs(new Random(2).nextInt()) / 500));
		}
		
		setListAdapter(new FolderAdapter(activity, R.layout.folder_list_row,
				folders));
		
		
	}
	
	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final boolean isDualPane = getResources().getBoolean(R.bool.dual_pane);
				
				if (isDualPane) {
					getActivity().getSupportFragmentManager()
					.beginTransaction().replace(
							R.id.container_for_fragment, 
							Fragment.instantiate(getActivity(), FilesListFragment.class.getName()))
					.addToBackStack(null)
					.commit();
				}
				
				
				else {
					startActivity(new Intent(view.getContext(), ViewFolder.class));
				}
			}
		});
	}

	private class FolderInfo {
		public final String Name;
		public final int NumItems;
		
		public FolderInfo(String name, int num) {
			Name = name;
			NumItems = num;
		}
	}
 	 
	private class FolderAdapter extends ArrayAdapter<FolderInfo> {


		public FolderAdapter(Context context, 
				int textViewResourceId, List<FolderInfo> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = getActivity().getLayoutInflater().inflate
			(R.layout.folder_list_row, parent, false);
			
			FolderInfo fi = getItem(position);
			
			((TextView) view.findViewById(R.id.primary_folder_info))
			.setText(fi.Name);
		
			((TextView) view.findViewById(R.id.secondary_folder_info))
			.setText(fi.NumItems + " files");
			
			return view;
		}	
	}
 }