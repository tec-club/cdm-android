package com.ampelement.cdm.other;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.ampelement.cdm.R;
import com.ampelement.cdm.utils.android.ExtendedFragment;
import com.ampelement.cdm.utils.android.NavDrawerEntry;

/**
 * This class represents an entry on the Navigation Drawer
 */
public class TwitterEntry extends NavDrawerEntry {

	public static final String TAG = "TwitterEntry";

	@Override
	public String getTitle() {
		return "Twitter";
	}

	@Override
	public EntryType getType() {
		return EntryType.ACTION;
	}

	@Override
	public EntryStyle getStyle() {
		return EntryStyle.SMALL;
	}

	@Override
	public int getIcon() {
		return R.drawable.ic_twitter;
	}

	@Override
	public Class<ExtendedFragment> getFragmentType() {
		return null;
	}

	private static final String TWITTER_USERNAME = "CdMASB";

	@Override
	public void runAction(Activity activity) {
		try {
			activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + TWITTER_USERNAME)));
		} catch (Exception e) {
			activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + TWITTER_USERNAME)));
		}
	}
}
