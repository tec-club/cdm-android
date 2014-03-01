package com.ampelement.cdm.utils.android;

import com.ampelement.cdm.CDMActivity;

import android.app.Activity;
import android.util.Log;

public abstract class NavDrawerEntry {
	/**
	 * Returns a human friendly title for this fragment. Intended to be used in
	 * a navigation drawer or displayed on the action bar.
	 * 
	 * @return String human friendly title
	 */
	public abstract String getTitle();

	/**
	 * If this entry represents a category header or not
	 * 
	 * @return false if not a category, true otherwise
	 */
	public abstract boolean isCategory();

	/**
	 * Returns an icon res id for the navigation drawer entry. Returns 0 if
	 * there should be no icon.
	 * 
	 * @return Icon res id or 0 for no icon
	 */
	public abstract int getIcon();

	/**
	 * If this entry represents a view or an action.
	 * 
	 * @return true if this entry represents a view
	 */
	public abstract boolean isFragment();

	public abstract Class<? extends ExtendedSherlockFragment> getFragmentType();

	private ExtendedSherlockFragment fragment;

	/**
	 * Should be overridden for entries that represent a view.
	 * 
	 * @return the fragment to be shown
	 */
	public ExtendedSherlockFragment getFragment() {
		if (fragment == null)
			try {
				fragment = getFragmentType().newInstance();
			} catch (InstantiationException e) {
				Log.e(CDMActivity.TAG, e.getMessage(), e);
			} catch (IllegalAccessException e) {
				Log.e(CDMActivity.TAG, e.getMessage(), e);
			} catch (NullPointerException e) {
				Log.e(CDMActivity.TAG, e.getMessage(), e);
			}
		return fragment;
	}

	/**
	 * Should be overridden for entries that are simply actions to be executed
	 * with no attached view.
	 */
	public abstract void runAction(Activity activity);

}
