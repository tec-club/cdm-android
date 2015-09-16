package com.ampelement.cdm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;

import com.ampelement.cdm.NavAdapter.OnNavChangeListener;
import com.ampelement.cdm.calendar.CalendarFragment;
import com.ampelement.cdm.deprecated.infoscreen.InfoListFragment;
import com.ampelement.cdm.other.InstagramEntry;
import com.ampelement.cdm.other.SettingsEntry;
import com.ampelement.cdm.other.TridentEntry;
import com.ampelement.cdm.other.TwitterEntry;
import com.ampelement.cdm.other.categories.CategoryEntryOther;
import com.ampelement.cdm.other.categories.CategoryEntryWeb;
import com.ampelement.cdm.schoolloop.SchoolLoopFragment;
import com.ampelement.cdm.utils.android.AndroidUtils;
import com.ampelement.cdm.utils.android.ExtendedFragment;


import java.io.PrintWriter;
import java.io.StringWriter;

public class CDMActivity extends ActionBarActivity {

	public static final String TAG = "CDMActivity";

	private NavAdapter mNavAdapter;

	private SharedPreferences mSharedPreferences;

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		getSupportActionBar().setIcon(R.drawable.trident);
		getSupportActionBar().setTitle("Corona del Mar HS");

		mNavAdapter = new NavAdapter(this, savedInstanceState, findViewById(android.R.id.content), R.id.main_drawer_layout, R.id.main_nav_drawer,
				R.id.main_frame, new OnNavChangeListener() {

					@Override
					public void onFragmentLoaded(ExtendedFragment oldFragment, ExtendedFragment newFragment) {
						// getSupportActionBar().setTitle(newFragment.getTitle());
					}

					@SuppressLint("NewApi")
					@Override
					public void onDrawerOpen(View drawerView) {
						// getSupportActionBar().setTitle(R.string.cdm_drawer_open);
						if (AndroidUtils.API_LEVEL() >= 11)
							invalidateOptionsMenu();
					}

					@SuppressLint("NewApi")
					@Override
					public void onDrawerClose(View drawerView) {
						// getSupportActionBar().setTitle(mNavAdapter.getCurrentTitle());
						if (AndroidUtils.API_LEVEL() >= 11)
							invalidateOptionsMenu();
					}

				},// Main items
				SchoolLoopFragment.Entry.class, InfoListFragment.Entry.class, CalendarFragment.Entry.class,
				// Web Views/External
				CategoryEntryWeb.class, InstagramEntry.class, TwitterEntry.class, TridentEntry.class,
				// Other/Settings
				CategoryEntryOther.class, SettingsEntry.class);
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (mSharedPreferences.getBoolean(Preferences.SETTINGS_ALLOW_PUSH_NOTIFS, true)) {



		}
	}

	public void loadPosition(int p) {
		mNavAdapter.loadPosition(p);
	}

	@Override
	public void onBackPressed() {
		try {
			ExtendedFragment currentFragment = mNavAdapter.getCurrentFragment();
			if (currentFragment != null) {
				if (currentFragment != null && currentFragment instanceof SchoolLoopFragment) {
					SchoolLoopFragment schoolLoopFragment = (SchoolLoopFragment) currentFragment;
					if (schoolLoopFragment.mWebView != null && schoolLoopFragment.mWebView.canGoBack()) {
						schoolLoopFragment.mWebView.goBack();
					} else {
						super.onBackPressed();
					}
				} else {
					super.onBackPressed();
				}
			} else
				super.onBackPressed();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "me@alexwendland.com", null));
			emailIntent.putExtra(Intent.EXTRA_TEXT, e.getMessage() + "\n" + sw.toString());
			startActivity(Intent.createChooser(emailIntent, "Send email..."));
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mNavAdapter.onPostCreate(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mNavAdapter.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		mNavAdapter.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mNavAdapter.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

}