package com.ampelement.cdm.utils.android;

import com.actionbarsherlock.app.SherlockFragment;

public abstract class TitledSherlockFragment extends SherlockFragment {
	/**
	 * Returns a human friendly title for this fragment. Intended to be used in
	 * a navigation drawer or displayed on the action bar.
	 * 
	 * @return String human friendly title
	 */
	public abstract String getTitle();
}
