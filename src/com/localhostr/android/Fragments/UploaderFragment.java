package com.localhostr.android.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.localhostr.android.R;

/**
 * The Fragment which allows you to upload files.
 * 
 * @author Al
 *
 */
public class UploaderFragment extends SherlockFragment {

    private Button mPicker;
    private Button mCanceller;
    private TextView mFileName;
    private ProgressBar mProgress;
    private AsyncTask<Void, Integer, Void> mUploadTask;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
						
		final View ret = inflater.inflate(R.layout.upload, container, false);
			
		mPicker = (Button) ret.findViewById(R.id.pick_file);
		mCanceller = (Button) ret.findViewById(R.id.cancel_upload);
		mFileName = (TextView) ret.findViewById(R.id.uploaded_filename);
		mProgress = (ProgressBar) ret.findViewById(R.id.upload_progress);
		
		mPicker.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				v.setVisibility(View.GONE);
				mProgress.setVisibility(View.VISIBLE);
				mCanceller.setVisibility(View.VISIBLE);
				mFileName.setVisibility(View.VISIBLE);
				mUploadTask = new AsyncTask<Void, Integer, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						int i = 0;
						
						while (i <= 100) {
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
							}
						
							i++;
								
							publishProgress(i);
						}
							
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mCanceller.performClick();
					}

					@Override
					protected void onProgressUpdate(Integer... values) {
						mProgress.setProgress(values[0]);
					}				
				};
					
				mUploadTask.execute();
			}
		});
			
		mCanceller.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mUploadTask != null) {
					mUploadTask.cancel(true);
					mUploadTask = null;
				}
		
				v.setVisibility(View.GONE);
				mProgress.setVisibility(View.GONE);
				mPicker.setVisibility(View.VISIBLE);
				mFileName.setVisibility(View.GONE);
			}
		});
		
		return ret;
	}
}