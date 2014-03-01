package com.ampelement.cdm.other;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.ampelement.cdm.utils.android.ExtendedSherlockFragment;
import com.ampelement.cdm.utils.android.NavDrawerEntry;

public class InstagramEntry extends NavDrawerEntry {

	public static final String TAG = "InstagramEntry";

	@Override
	public String getTitle() {
		return "Instagram";
	}

	@Override
	public boolean isCategory() {
		return false;
	}

	@Override
	public int getIcon() {
		return 0;
	}

	@Override
	public boolean isFragment() {
		return false;
	}

	@Override
	public Class<ExtendedSherlockFragment> getFragmentType() {
		return null;
	}

	private static final String INSTAGRAM_URL = "http://instagram.com/cdm_asb";

	@Override
	public void runAction(Activity activity) {
		Uri uri = Uri.parse(INSTAGRAM_URL);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		activity.startActivity(intent);
	}
}
