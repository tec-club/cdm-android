package com.ampelement.cdm;

//import com.actionbarsherlock.app.SherlockFragmentActivity;
//import com.actionbarsherlock.view.Window;
import com.ampelement.cdm.fragments.EventListFragment;
import com.ampelement.cdm.fragments.EventListFragment.EventInterface;
import com.ampelement.cdm.fragments.MediaFragment;
import com.ampelement.cdm.fragments.MediaFragment.MediaInterface;
import com.ampelement.cdm.services.Update_Service;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

public class CDMActivity extends FragmentActivity implements EventInterface, MediaInterface {

	private static final String TAG = "CDMActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (savedInstanceState != null) {
			/*if (savedInstanceState.getString("fragment", EventListFragment.TAG).matches(EventListFragment.TAG)) {
				transitionFragments(new EventListFragment(), EventListFragment.TAG);
			} else {
				transitionFragments(new MediaFragment(), MediaFragment.TAG);
			}*/
		} else {
			transitionFragments(new EventListFragment(), EventListFragment.TAG);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		/*String savedFragment = EventListFragment.TAG;
		if (getSupportFragmentManager().findFragmentByTag(MediaFragment.TAG) != null) {
			savedFragment = MediaFragment.TAG;
		}
		outState.putString("fragment", savedFragment);*/
	}

	public void OnClickEvents(View view) {
		if (getSupportFragmentManager().findFragmentByTag(EventListFragment.TAG) != null) {
		} else {
			transitionFragments(new EventListFragment(), EventListFragment.TAG);
		}
	}

	public void OnClickMedia(View view) {
		if (getSupportFragmentManager().findFragmentByTag(MediaFragment.TAG) != null) {
		} else {
			transitionFragments(new MediaFragment(), MediaFragment.TAG);
		}
	}

	public void OnClickFacebook(View view) {
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/pages/You-know-you-go-to-CdM-when/239305217972"));
		startActivity(i);
	}

	public void OnClickHome(View view) {
		startService(new Intent(getApplicationContext(), Update_Service.class));
	}

	public void setIndicator(int indicatorID) {
		((LinearLayout) findViewById(R.id.main_events_indicator)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.main_facebook_indicator)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.main_media_indicator)).setVisibility(View.GONE);
		LinearLayout indicator = (LinearLayout) findViewById(indicatorID);
		indicator.setVisibility(View.VISIBLE);
	}

	private void transitionFragments(Fragment fragment, String fragmentTag) {
		// get an instance of FragmentTransaction from your Activity
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		// add a fragment
		// if (fragmentToRemove != null) {
		// fragmentTransaction.remove(fragmentToRemove);
		// }
		fragmentTransaction.replace(R.id.main_fragment, fragment, fragmentTag);
		fragmentTransaction.commit();
	}

}