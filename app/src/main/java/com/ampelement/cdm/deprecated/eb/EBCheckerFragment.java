package com.ampelement.cdm.deprecated.eb;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
No longer needed I think... since EB has been discontinued
Perhaps we can use this for a running tab on books available at the
school library. That would be useful
TODO Delete this code later, or convert School Library book availability checker
 */
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
