package com.ampelement.cdm.eb;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EBCheckerFragment extends Fragment {

	public static final String TAG = "EBCheckerFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return null;
	}

	private class EBUpdateTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... cred) {
			return null;
		}

		@Override
		protected void onPostExecute(Boolean success) {
		}
	}

}
