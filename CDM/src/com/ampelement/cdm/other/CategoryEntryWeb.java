package com.ampelement.cdm.other;

import android.app.Activity;

import com.ampelement.cdm.utils.android.ExtendedSherlockFragment;
import com.ampelement.cdm.utils.android.NavDrawerEntry;

public class CategoryEntryWeb extends NavDrawerEntry {

	@Override
	public String getTitle() {
		return "Web";
	}

	@Override
	public boolean isCategory() {
		return true;
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
	public Class<? extends ExtendedSherlockFragment> getFragmentType() {
		return null;
	}

	@Override
	public void runAction(Activity activity) {
		// None
	}

}
