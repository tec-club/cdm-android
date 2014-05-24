package com.ampelement.cdm.utils.android;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.ampelement.cdm.CDMActivity;

public abstract class NavDrawerEntry {
	/**
	 * Returns a human friendly title for this fragment. Intended to be used in
	 * a navigation drawer or displayed on the action bar.
	 * 
	 * @return String human friendly title
	 */
	public abstract String getTitle();

	public enum EntryType {
		LABEL, ACTION, FRAGMENT;
	}

	/**
	 * Which {@link EntryType} is this entry.
	 * 
	 * @return {@link EntryType}
	 */
	public abstract EntryType getType();

	public enum EntryStyle {
		NORMAL, CATEGORY, SMALL;
	}

	/**
	 * Which {@link EntryStyle} is this entry.
	 * 
	 * @return {@link EntryStyle}
	 */
	public abstract EntryStyle getStyle();

	/**
	 * Returns an icon res id for the navigation drawer entry. Returns 0 if
	 * there should be no icon.
	 * 
	 * @return Icon res id or 0 for no icon
	 */
	public abstract int getIcon();

	public abstract Class<? extends ExtendedFragment> getFragmentType();

	private ExtendedFragment fragment;

	/**
	 * Should be overridden for entries that represent a view.
	 * 
	 * @return the fragment to be shown
	 */
	public ExtendedFragment getFragment() {
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

	public ExtendedFragment findFragment(FragmentManager fm) {
		Fragment f = fm.findFragmentByTag(getFragmentType().getSimpleName());
		if (f != null && f instanceof ExtendedFragment)
			fragment = (ExtendedFragment) f;
		return getFragment();
	}

	/**
	 * Should be overridden for entries that are simply actions to be executed
	 * with no attached view.
	 */
	public abstract void runAction(Activity activity);

}
