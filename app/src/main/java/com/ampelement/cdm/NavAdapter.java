package com.ampelement.cdm;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ampelement.cdm.utils.android.ExtendedFragment;
import com.ampelement.cdm.utils.android.NavDrawerEntry;
import com.ampelement.cdm.utils.android.NavDrawerEntry.EntryStyle;
import com.ampelement.cdm.utils.android.NavDrawerEntry.EntryType;

import java.util.ArrayList;
import java.util.List;

public class NavAdapter {

	private static final String TAG = "NavAdapter";

	private static final String BUNDLE_CURRENT_POS = "currentPos";

	List<NavDrawerEntry> mEntries;

	private ActionBarDrawerToggle mDrawerToggle;
	private NavListAdapter mNavListAdapter;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private int mResFragmentFrame;

	private ActionBarActivity mActivity;
	private final LayoutInflater mInflater;
	private FragmentManager mFragmentManager;

	private int mCurrentPos;
	private boolean mIsDrawerOpen;

	private OnNavChangeListener mOnNavChangeListener;

	public interface OnNavChangeListener {
		public void onDrawerOpen(View drawerView);

		public void onDrawerClose(View drawerView);

		public void onFragmentLoaded(ExtendedFragment oldFragment, ExtendedFragment newFragment);
	}

	public NavAdapter(ActionBarActivity activity, Bundle savedInstanceState, View parentView, int resDrawerLayout, int resDrawerList,
			int resFragmentFrame, OnNavChangeListener onNavChangeListener, Class<? extends NavDrawerEntry>... classes) {
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
				if (position > 0 && getEntry(position - 1).getType() != EntryType.LABEL)
					loadPosition(position - 1);
			}
		});

		mDrawerToggle = new ActionBarDrawerToggle(activity, mDrawerLayout, R.drawable.ic_drawer, R.string.cdm_drawer_open, R.string.cdm_app_name) {
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
			if (entry.getType() == EntryType.FRAGMENT) {
				ExtendedFragment fragment = entry.findFragment(mFragmentManager);
				if (savedInstanceState == null || fragment == null) {
					fragment = entry.getFragment();
					transaction.add(mResFragmentFrame, fragment, fragment.getFragmentTag());
					transaction.detach(fragment);
				}
			}
		}
		if (savedInstanceState != null)
			mCurrentPos = savedInstanceState.getInt(BUNDLE_CURRENT_POS);
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
				if (entry.getType() == EntryType.ACTION) {
					entry.runAction(mActivity);
				} else if (entry.getType() == EntryType.FRAGMENT) {
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
			if (getEntry(position).getType() == EntryType.LABEL)
				return false;
			return super.isEnabled(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Setup row view
			TextView title;
			ImageView icon;
			switch (getEntry(position).getStyle()) {
			case CATEGORY:
				convertView = mInflater.inflate(R.layout.nav_drawer_list_item_header, parent, false);

				title = (TextView) convertView.findViewById(R.id.nav_drawer_item_header_title);
				title.setText(mEntries.get(position).getTitle());
				break;
			case NORMAL:
				convertView = mInflater.inflate(R.layout.nav_drawer_list_item_normal, parent, false);

				title = (TextView) convertView.findViewById(R.id.nav_drawer_list_item_title);
				try {
					if (mCurrentPos == position)
						title.setTypeface(Typeface.DEFAULT_BOLD);
					else
						title.setTypeface(null, Typeface.NORMAL);
					title.setText(mEntries.get(position).getTitle());
				} catch (Exception e) {
				}
				break;
			case SMALL:
				convertView = mInflater.inflate(R.layout.nav_drawer_list_item_small, parent, false);

				title = (TextView) convertView.findViewById(R.id.nav_drawer_list_item_small_title);
				title.setText(mEntries.get(position).getTitle());

				if (mEntries.get(position).getIcon() != 0) {
					icon = (ImageView) convertView.findViewById(R.id.nav_drawer_list_item_small_icon);
					icon.setImageResource(mEntries.get(position).getIcon());
				}

				if (position > 1 && mEntries.get(position - 1).getStyle() == EntryStyle.CATEGORY) {
					View dividerView = convertView.findViewById(R.id.nav_drawer_list_item_divider);
					dividerView.setVisibility(View.INVISIBLE);
				}
				break;
			}

			return convertView;
		}
	}

	public CharSequence getCurrentTitle() {
		return mEntries.get(mCurrentPos).getTitle();
	}

	public ExtendedFragment getCurrentFragment() {
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

	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(BUNDLE_CURRENT_POS, mCurrentPos);
	}
}
