package com.ampelement.cdm;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ampelement.cdm.fragments.EventListFragment;
import com.ampelement.cdm.fragments.InfoListFragment;
import com.ampelement.cdm.fragments.SchoolLoopFragment;

public class CDMActivity extends SherlockFragmentActivity {

	private static final String TAG = "CDMActivity";

	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;

	EventListFragment mFragmentEventList;
	InfoListFragment mFragmentInfoList;
	SchoolLoopFragment mFragmentSchoolLoop;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
		// This block thanks to http://stackoverflow.com/q/9790279/517561
		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayShowHomeEnabled(true);
		//
		mViewPager.setOffscreenPageLimit(2);
		mTabsAdapter = new TabsAdapter(this, mViewPager);
		addTabs();
	}

	@Override
	public void onBackPressed() {
		SherlockFragment fragment = mTabsAdapter.getCurrentFragment();
		if (fragment != null) {
			if (fragment != null && fragment instanceof SchoolLoopFragment) {
				SchoolLoopFragment schoolLoopFragment = (SchoolLoopFragment) fragment;
				if (schoolLoopFragment.webView.canGoBack()) {
					schoolLoopFragment.webView.goBack();
				} else {
					super.onBackPressed();
				}
			} else if (fragment != null && fragment instanceof InfoListFragment) {
				if (!((InfoListFragment) fragment).isVisible()) {
					super.onBackPressed();
				}
			} else {
				super.onBackPressed();
			}
		} else
			super.onBackPressed();
	}

	void addTabs() {
		mFragmentEventList = new EventListFragment();
		mFragmentInfoList = new InfoListFragment();
		mFragmentSchoolLoop = new SchoolLoopFragment();
		mTabsAdapter.addTab("Events", mFragmentEventList);
		mTabsAdapter.addTab("Info", mFragmentInfoList);
		mTabsAdapter.addTab("SchoolLoop", mFragmentSchoolLoop);
	}

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost. It relies on a
	 * trick. Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show. This is not sufficient for switching
	 * between pages. So instead we make the content part of the tab host 0dp
	 * high (it is not shown) and the TabsAdapter supplies its own dummy view to
	 * show as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct paged in the ViewPager whenever the selected tab
	 * changes.
	 */
	public static class TabsAdapter extends FragmentPagerAdapter implements
			ViewPager.OnPageChangeListener, ActionBar.TabListener {
		private final SherlockFragmentActivity mContext;
		private final ViewPager mViewPager;
		private final ArrayList<SherlockFragment> mFragments = new ArrayList<SherlockFragment>();

		static class DummyTabFactory implements
				android.widget.TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(CharSequence label, SherlockFragment fragment) {
			ActionBar.Tab tab = mContext.getSupportActionBar().newTab();
			tab.setText(label);
			tab.setTabListener(this);
			mContext.getSupportActionBar().addTab(tab);
			mFragments.add(fragment);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public SherlockFragment getItem(int position) {
			return mFragments.get(position);
		}

		public SherlockFragment getCurrentFragment() {
			return getItem(mViewPager.getCurrentItem());
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			mContext.getSupportActionBar().setSelectedNavigationItem(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see com.actionbarsherlock.app.ActionBar.TabListener#onTabSelected(com.actionbarsherlock.app
		 *      .ActionBar.Tab, android.support.v4.app.FragmentTransaction)
		 */
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			mViewPager.setCurrentItem(mContext.getSupportActionBar()
					.getSelectedNavigationIndex());
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see com.actionbarsherlock.app.ActionBar.TabListener#onTabUnselected(com.actionbarsherlock
		 *      .app.ActionBar.Tab, android.support.v4.app.FragmentTransaction)
		 */
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see com.actionbarsherlock.app.ActionBar.TabListener#onTabReselected(com.actionbarsherlock
		 *      .app.ActionBar.Tab, android.support.v4.app.FragmentTransaction)
		 */
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}

}