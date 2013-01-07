package com.localhostr.android.Fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.localhostr.android.DB;
import com.localhostr.android.DB.Plan;
import com.localhostr.android.DB.USER_COLUMNS;
import com.localhostr.android.LoginActivity;
import com.localhostr.android.R;
import com.localhostr.android.Utils;

/**
 * The Fragment which shows the current account information.
 * 
 * @author Al
 *
 */
public class AccountInfoFragment extends SherlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.account_info, container, false);
		
		final DB db = Utils.getDB(getActivity());
		ContentValues cv = db.getAccountInfo();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("You are logged in as ").append(cv.get(USER_COLUMNS.EMAIL)).append("\n");
		sb.append("You are on the ").append(Plan.readPlan(cv.getAsInteger(USER_COLUMNS.PLAN))).append(" plan\n");
		sb.append("You have uploaded ").append(cv.getAsInteger(USER_COLUMNS.FILE_COUNT)).append(" files\n");
		sb.append("Your daily upload allowance is ").append(cv.getAsInteger(USER_COLUMNS.DAILY_ALLOWANCE)).append(" files\n");
		sb.append("You can upload files with a maximum size of ").append(cv.getAsInteger(USER_COLUMNS.MAX_FILESIZE)).append("\n");
		
		((TextView) v.findViewById(R.id.account_details)).setText(sb);
		
		v.findViewById(R.id.logout).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				db.clearForLogout();
				startActivity(new Intent(getActivity(), LoginActivity.class));
				getActivity().finish();
			}
			
		});
		
		return v;
	}
	 
}