package com.ampelement.cdm.other;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.ampelement.cdm.R;
import com.ampelement.cdm.utils.android.ExtendedSherlockFragment;
import com.ampelement.cdm.utils.android.NavDrawerEntry;
import com.ampelement.cdm.utils.android.NavDrawerEntry.EntryStyle;
import com.ampelement.cdm.utils.android.NavDrawerEntry.EntryType;

public class InstagramEntry extends NavDrawerEntry {

	public static final String TAG = "InstagramEntry";

	@Override
	public String getTitle() {
		return "Instagram";
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
		return R.drawable.ic_instagram;
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
