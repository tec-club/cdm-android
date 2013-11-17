package com.ampelement.cdm;

import java.util.ArrayList;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ampelement.cdm.constants.CDMColors;
import com.ampelement.cdm.fragments.CalendarFragment;
import com.ampelement.cdm.fragments.InfoListFragment;
import com.ampelement.cdm.fragments.SchoolLoopFragment;
import com.ampelement.cdm.helper.DefaultViewPagerOnChangeListener;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

public class CDMActivity extends SherlockFragmentActivity {

	private static final String TAG = "CDMActivity";

	ViewPager mViewPager;
	PagerSlidingTabStrip mPagerSlidingTabs;
	CDMTabsAdapter mCDMTabsAdapter;

	CalendarFragment mFragmentCalendar;
	InfoListFragment mFragmentInfoList;
	SchoolLoopFragment mFragmentSchoolLoop;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		getSupportActionBar().setLogo(R.drawable.crown);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
//		getSupportActionBar().setTitle("Corona del Mar");
		getSupportActionBar().setTitle(Html.fromHtml("<b><font color='#ffffff'>Corona del Mar</font></b>"));

		mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
		mPagerSlidingTabs = (PagerSlidingTabStrip) findViewById(R.id.main_tabs);

		mCDMTabsAdapter = new CDMTabsAdapter(getSupportFragmentManager());
		setupTabs();
	}

	void setupTabs() {
		mFragmentCalendar = new CalendarFragment();
		mFragmentSchoolLoop = new SchoolLoopFragment();
		mFragmentInfoList = new InfoListFragment();
		mCDMTabsAdapter.addTab(new TitledFragment("Events", mFragmentCalendar, CDMColors.BLUE));
		mCDMTabsAdapter.addTab(new TitledFragment("SchoolLoop", mFragmentSchoolLoop, CDMColors.GREEN));
		mCDMTabsAdapter.addTab(new TitledFragment("Info", mFragmentInfoList, CDMColors.ORANGE));

		mViewPager.setAdapter(mCDMTabsAdapter);
		mPagerSlidingTabs.setViewPager(mViewPager);
		
		mPagerSlidingTabs.setOnPageChangeListener(new DefaultViewPagerOnChangeListener() {
			
			@Override
			public void onPageScrollStateChanged(int position) {
				changeActionBarColor(mCDMTabsAdapter.getCurrentItem().color);
			}
		});

		changeActionBarColor(mCDMTabsAdapter.getCurrentItem().color);

		mViewPager.setOffscreenPageLimit(2);
	}
	
	Drawable oldActionBar = null;
	
	public void changeActionBarColor(int color) {
		mPagerSlidingTabs.setIndicatorColor(mCDMTabsAdapter.getCurrentItem().color);
		
		Drawable colorDrawable = new ColorDrawable(color);
		Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
		LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });
//		getSupportActionBar().setBackgroundDrawable(ld);
//		getSupportActionBar().setDisplayShowTitleEnabled(false);
//		getSupportActionBar().setDisplayShowTitleEnabled(true);
		
		if (oldActionBar != null) {
			TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldActionBar, ld });
			getSupportActionBar().setBackgroundDrawable(td);
//			td.setCallback(drawableCallback);
			td.startTransition(200);
			oldActionBar = td;
		} else {
			getSupportActionBar().setBackgroundDrawable(ld);
			oldActionBar = ld;
		}
		
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		
	}
	
	private Handler handler = new Handler();
	
	private Drawable.Callback drawableCallback = new Drawable.Callback() {
		@Override
		public void invalidateDrawable(Drawable who) {
			getSupportActionBar().setBackgroundDrawable(who);
		}

		@Override
		public void scheduleDrawable(Drawable who, Runnable what, long when) {
			handler.postAtTime(what, when);
		}

		@Override
		public void unscheduleDrawable(Drawable who, Runnable what) {
			handler.removeCallbacks(what);
		}
	};

	@Override
	public void onBackPressed() {
		SherlockFragment fragment = mCDMTabsAdapter.getCurrentFragment();
		if (fragment != null) {
			if (fragment != null && fragment instanceof SchoolLoopFragment) {
				SchoolLoopFragment schoolLoopFragment = (SchoolLoopFragment) fragment;
				if (schoolLoopFragment.webView.canGoBack()) {
					schoolLoopFragment.webView.goBack();
				} else {
					super.onBackPressed();
				}
			} else {
				super.onBackPressed();
			}
		} else
			super.onBackPressed();
	}

	private static class TitledFragment {
		SherlockFragment fragment;
		String title;
		int color;

		TitledFragment(String title, SherlockFragment fragment, int color) {
			this.fragment = fragment;
			this.title = title;
			this.color = color;
		}
	}

	public class CDMTabsAdapter extends FragmentPagerAdapter {
		private final ArrayList<TitledFragment> mFragments = new ArrayList<TitledFragment>();

		public CDMTabsAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragments.get(position).title;
		}

		public void addTab(TitledFragment frag) {
			mFragments.add(frag);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public SherlockFragment getItem(int position) {
			return mFragments.get(position).fragment;
		}

		public TitledFragment getCurrentItem() {
			return mFragments.get(mViewPager.getCurrentItem());
		}

		public SherlockFragment getCurrentFragment() {
			return getItem(mViewPager.getCurrentItem());
		}
	}

}