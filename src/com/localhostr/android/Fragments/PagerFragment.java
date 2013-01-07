package com.localhostr.android.Fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.localhostr.android.R;
import com.viewpagerindicator.TitlePageIndicator;

public class PagerFragment extends SherlockFragment {

	private ViewPager mViewPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.pager_fragment, container, false);

		mViewPager = (ViewPager) v.findViewById(R.id.pager);
		Adapter adapter = new Adapter(getActivity().getSupportFragmentManager());
		adapter.addItems(new FragmentSpec(AccountInfoFragment.class, "Account"));
		adapter.addItems(new FragmentSpec(FilesListFragment.class, "My Files"));
		adapter.addItems(new FragmentSpec(FolderListFragment.class, "My Folders"));
		adapter.addItems(new FragmentSpec(UploaderFragment.class, "Upload"));

		mViewPager.setAdapter(adapter);
		TitlePageIndicator titleIndicator = (TitlePageIndicator) v.findViewById(R.id.titles);
		titleIndicator.setViewPager(mViewPager);
		mViewPager.setCurrentItem(1);

		return v;
	}

	public static class FragmentSpec {

		public final Class<?> mClass;
		public final String mTitle;

		public FragmentSpec(Class<?> clazz, String title) {
			mClass = clazz;
			mTitle = title;
		}
	}

	public class Adapter extends FragmentPagerAdapter {

		private ArrayList<FragmentSpec> mItems = new ArrayList<FragmentSpec>();

		public Adapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mItems.get(position).mTitle;
		}

		@Override
		public Fragment getItem(int position) {
			return Fragment.instantiate(getActivity(), mItems.get(position).mClass.getName());
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		public void addItems(FragmentSpec spec) {
			if (mItems.indexOf(spec) < 0) {
				mItems.add(spec);
			}
		}
	}

	public void showUploadsScreen() {
		mViewPager.setCurrentItem(3, true);
	}
}
