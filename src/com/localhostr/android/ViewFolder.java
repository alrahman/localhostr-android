package com.localhostr.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.localhostr.android.Fragments.FilesListFragment;

/**
 * This activity shows a list of files within a folder.
 * 
 * @author Al
 *
 */
public class ViewFolder extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.folder_items);

		getSupportFragmentManager()
		.beginTransaction().add(R.id.fragment_host, Fragment.instantiate(
				this, FilesListFragment.class.getName()), null)
				.commit();

		setTitle("Random Folder");

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Delete").setIcon(android.R.drawable.ic_menu_delete);

		return true;
	}
}
