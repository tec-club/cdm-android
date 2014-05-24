package com.ampelement.cdm.other;

import android.app.Activity;
import android.content.Intent;

import com.ampelement.cdm.R;
import com.ampelement.cdm.SettingsActivity;
import com.ampelement.cdm.utils.android.ExtendedFragment;
import com.ampelement.cdm.utils.android.NavDrawerEntry;

public class SettingsEntry extends NavDrawerEntry {

	public static final String TAG = "SettingsEntry";

	@Override
	public String getTitle() {
		return "Settings";
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
		return R.drawable.ic_settings;
	}

	@Override
	public Class<ExtendedFragment> getFragmentType() {
		return null;
	}

	@Override
	public void runAction(Activity activity) {
		Intent intent = new Intent(activity.getApplicationContext(), SettingsActivity.class);
		activity.startActivity(intent);
	}
}
