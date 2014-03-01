package com.ampelement.cdm;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.ampelement.cdm.utils.android.ExtendedSherlockFragment;
import com.ampelement.cdm.utils.android.NavDrawerEntry;

public class NavAdapter {

	private static final String TAG = "NavAdapter";

	List<NavDrawerEntry> mEntries;

	private ActionBarDrawerToggle mDrawerToggle;
	private NavListAdapter mNavListAdapter;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private int mResFragmentFrame;

	private SherlockFragmentActivity mActivity;
	private final LayoutInflater mInflater;
	private FragmentManager mFragmentManager;

	private int mCurrentPos;
	private boolean mIsDrawerOpen;

	private OnNavChangeListener mOnNavChangeListener;

	public interface OnNavChangeListener {
		public void onDrawerOpen(View drawerView);

		public void onDrawerClose(View drawerView);

		public void onFragmentLoaded(ExtendedSherlockFragment oldFragment, ExtendedSherlockFragment newFragment);
	}

	public NavAdapter(SherlockFragmentActivity activity, View parentView, int resDrawerLayout, int resDrawerList, int resFragmentFrame,
			OnNavChangeListener onNavChangeListener, Class<? extends NavDrawerEntry>... classes) {
		mActivity = activity;

		mFragmentManager = mActivity.getSupportFragmentManager();
		mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mOnNavChangeListener = onNavChangeListener;

		mEntries = new ArrayList<NavDrawerEntry>();
		for (Class<? extends NavDrawerEntry> fragmentClass : classes) {
			try {
				mEntries.add(fragmentClass.newInstance());
			} catch (InstantiationException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch (IllegalAccessException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		mDrawerLayout = (DrawerLayout) parentView.findViewById(resDrawerLayout);
		mDrawerList = (ListView) parentView.findViewById(resDrawerList);
		mResFragmentFrame = resFragmentFrame;

		mNavListAdapter = new NavListAdapter();

		mDrawerList.addHeaderView(mInflater.inflate(R.layout.nav_drawer_list_header, null));
		mDrawerList.setAdapter(mNavListAdapter);

		mDrawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View rowView, int position, long itemId) {
				if (!getEntry(position - 1).isCategory())
					loadPosition(position - 1);
			}
		});

		mDrawerToggle = new ActionBarDrawerToggle(activity, mDrawerLayout, R.drawable.ic_drawer, R.string.drawerOpen, R.string.app_name) {
			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				mIsDrawerOpen = false;
				if (mOnNavChangeListener != null)
					mOnNavChangeListener.onDrawerClose(view);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				mIsDrawerOpen = true;
				if (mOnNavChangeListener != null)
					mOnNavChangeListener.onDrawerOpen(drawerView);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		for (NavDrawerEntry entry : mEntries) {
			if (entry.isFragment()) {
				ExtendedSherlockFragment fragment = entry.getFragment();
				transaction.add(mResFragmentFrame, fragment);
				transaction.detach(fragment);
			}
		}
		transaction.attach(getCurrentFragment());
		transaction.commit();
	}

	private NavDrawerEntry getEntry(int position) {
		return mEntries.get(position);
	}

	public void loadPosition(int position) {
		if (position >= 0) {
			if (mCurrentPos != position) {
				NavDrawerEntry entry = getEntry(position);
				if (!entry.isFragment()) {
					entry.runAction(mActivity);
				} else {
					int oldFragmentPos = mCurrentPos;
					mCurrentPos = position;

					FragmentTransaction transaction = mFragmentManager.beginTransaction();
					transaction.setCustomAnimations(R.anim.drop_and_fade_in, R.anim.fade_out);
					transaction.detach(mEntries.get(oldFragmentPos).getFragment());
					transaction.attach(getCurrentFragment());
					transaction.commit();

					/*
					 * Highlight the selected item, update the title, and close
					 * the drawer
					 */
					mDrawerList.setItemChecked(position, true);
					if (mOnNavChangeListener != null)
						mOnNavChangeListener.onFragmentLoaded(mEntries.get(oldFragmentPos).getFragment(), mEntries.get(mCurrentPos).getFragment());
				}
			}
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	private class NavListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mEntries.size();
		}

		@Override
		public Object getItem(int position) {
			return mEntries.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean isEnabled(int position) {
			if (getEntry(position).isCategory())
				return false;
			return super.isEnabled(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Setup row view
			if (getEntry(position).isCategory()) {
				convertView = mInflater.inflate(R.layout.nav_drawer_list_item_header, parent, false);

				TextView title = (TextView) convertView.findViewById(R.id.nav_drawer_item_header_title);
				title.setText(mEntries.get(position).getTitle());
			} else {
				convertView = mInflater.inflate(R.layout.nav_drawer_list_row, parent, false);

				TextView title = (TextView) convertView.findViewById(R.id.nav_drawer_list_item_title);
				try {
					if (mCurrentPos == position)
						title.setTypeface(Typeface.DEFAULT_BOLD);
					else
						title.setTypeface(null, Typeface.NORMAL);
					title.setText(mEntries.get(position).getTitle());
				} catch (Exception e) {
				}
			}

			return convertView;
		}

	}

	public CharSequence getCurrentTitle() {
		return mEntries.get(mCurrentPos).getTitle();
	}

	public ExtendedSherlockFragment getCurrentFragment() {
		return mEntries.get(mCurrentPos).getFragment();
	}

	public void onPostCreate(Bundle savedInstanceState) {
		mDrawerToggle.syncState();
	}

	public void onConfigurationChanged(Configuration newConfig) {
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (item.getItemId() == android.R.id.home) {
			if (mIsDrawerOpen) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
			return true;
		}

		return false;
	}
}
