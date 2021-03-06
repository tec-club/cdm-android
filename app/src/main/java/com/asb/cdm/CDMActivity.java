package com.asb.cdm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.asb.cdm.NavAdapter.OnNavChangeListener;
import com.asb.cdm.infoscreen.InfoListFragment;
import com.asb.cdm.other.InstagramEntry;
import com.asb.cdm.other.SettingsEntry;
import com.asb.cdm.other.TridentEntry;
import com.asb.cdm.other.TwitterEntry;
import com.asb.cdm.other.categories.CategoryEntryOther;
import com.asb.cdm.other.categories.CategoryEntryWeb;
import com.asb.cdm.schoolloop.SchoolLoopFragment;
import com.asb.cdm.utils.android.AndroidUtils;
import com.asb.cdm.utils.android.ExtendedFragment;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CDMActivity extends AppCompatActivity {

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

        getSupportActionBar().setLogo(R.drawable.ic_launcher);
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
                SchoolLoopFragment.Entry.class, InfoListFragment.Entry.class, //CalendarFragment.Entry.class,
                // Web Views/External
				CategoryEntryWeb.class, InstagramEntry.class, TwitterEntry.class, TridentEntry.class,
				// Other/Settings
				CategoryEntryOther.class, SettingsEntry.class);
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (mSharedPreferences.getBoolean(Preferences.SETTINGS_ALLOW_PUSH_NOTIFS, true)) {
            //TODO add code to initiate service for notifications pulled from Flask server


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
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "1tolsmar@gmail.com", null));
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