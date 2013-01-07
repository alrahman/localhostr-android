package com.localhostr.android.Fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockListFragment;
import com.localhostr.android.R;

public class Sidebar extends SherlockListFragment {

	private String mCurFragment;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		List<String> items = new ArrayList<String>();
		items.add("My Files");
		items.add("My Folders");
		items.add("Upload");

		setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.sidebar_row, 
				R.id.row_name, items));

		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				String clazz = null;

				switch (arg2) {
					case 0:
						clazz = FilesListFragment.class.getName();
						break;

					case 1:
						clazz = FolderListFragment.class.getName();
						break;

					case 2:
						clazz = UploaderFragment.class.getName();
						break;
				}

				switchTo(clazz);
			}
		});

		getActivity().getSupportFragmentManager().beginTransaction()
		.add(R.id.container_for_fragment, 
				Fragment.instantiate(getActivity(), FilesListFragment.class.getName()))
				.commit();

		mCurFragment = FilesListFragment.class.getName();
	}

	public void showUploadsScreen() {
		switchTo(UploaderFragment.class.getName());
	}

	private void switchTo(String clazz) {
		if (clazz.equalsIgnoreCase(mCurFragment)) {
			return;
		}

		getActivity().getSupportFragmentManager().beginTransaction()
		.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
		.replace(R.id.container_for_fragment, 
				Fragment.instantiate(getActivity(), clazz))
				.commit();

		mCurFragment = clazz;

	}	
}
