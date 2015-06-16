package com.ampelement.cdm.other;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.ampelement.cdm.R;
import com.ampelement.cdm.utils.android.ExtendedFragment;
import com.ampelement.cdm.utils.android.NavDrawerEntry;

public class TridentEntry extends NavDrawerEntry {

	public static final String TAG = "TridentEntry";

	@Override
	public String getTitle() {
		return "Trident Online";
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
		return R.drawable.ic_trident;
	}

	@Override
	public Class<ExtendedFragment> getFragmentType() {
		return null;
	}

	private static final String TRIDENT_URL = "http://www.tridentonline.net/";

	@Override
	public void runAction(Activity activity) {
		Uri uri = Uri.parse(TRIDENT_URL);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		activity.startActivity(intent);
	}
}
