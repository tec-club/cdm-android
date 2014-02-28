package com.ampelement.cdm;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.ampelement.cdm.NavAdapter.OnNavChangeListener;
import com.ampelement.cdm.calendar.CalendarFragment;
import com.ampelement.cdm.clubs.ClubsFragment;
import com.ampelement.cdm.infoscreen.InfoListFragment;
import com.ampelement.cdm.schoolloop.SchoolLoopFragment;
import com.ampelement.cdm.utils.android.AndroidUtils;
import com.ampelement.cdm.utils.android.TitledSherlockFragment;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class CDMActivity extends SherlockFragmentActivity {

	private static final String TAG = "CDMActivity";

	private NavAdapter mNavAdapter;

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mNavAdapter = new NavAdapter(this, findViewById(android.R.id.content), R.id.main_drawer_layout, R.id.main_nav_drawer, R.id.main_frame,
				new OnNavChangeListener() {

					@Override
					public void onFragmentLoaded(TitledSherlockFragment oldFragment, TitledSherlockFragment newFragment) {
						getSupportActionBar().setTitle(newFragment.getTitle());
					}

					@SuppressLint("NewApi")
					@Override
					public void onDrawerOpen(View drawerView) {
						getSupportActionBar().setTitle(R.string.drawerOpen);
						if (AndroidUtils.API_LEVEL() >= 11)
							invalidateOptionsMenu();
					}

					@SuppressLint("NewApi")
					@Override
					public void onDrawerClose(View drawerView) {
						getSupportActionBar().setTitle(mNavAdapter.getCurrentTitle());
						if (AndroidUtils.API_LEVEL() >= 11)
							invalidateOptionsMenu();
					}

				}, CalendarFragment.class, InfoListFragment.class, SchoolLoopFragment.class, ClubsFragment.class);

		/* Setup Parse for notifications */
		Parse.initialize(this, "gsXQZjeTDxb3Ekjp8PJ8TrY5X9NJROPpIq2E5ljm", "BMHgC1jqWcF3H8QFdqNFKnw1JJgeT1cuWct1W449");
		PushService.setDefaultPushCallback(this, CDMActivity.class);
		ParseInstallation.getCurrentInstallation().saveInBackground();
		/* Use Parse to track analytics */
		ParseAnalytics.trackAppOpened(getIntent());
	}

	@Override
	public void onBackPressed() {
		try {
			TitledSherlockFragment currentFragment = mNavAdapter.getCurrentFragment();
			if (currentFragment != null) {
				if (currentFragment != null && currentFragment instanceof SchoolLoopFragment) {
					SchoolLoopFragment schoolLoopFragment = (SchoolLoopFragment) currentFragment;
					if (schoolLoopFragment.webView != null && schoolLoopFragment.webView.canGoBack()) {
						schoolLoopFragment.webView.goBack();
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