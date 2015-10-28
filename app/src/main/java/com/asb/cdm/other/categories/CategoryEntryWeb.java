package com.asb.cdm.other.categories;

import android.app.Activity;

import com.asb.cdm.utils.android.ExtendedFragment;
import com.asb.cdm.utils.android.NavDrawerEntry;

public class CategoryEntryWeb extends NavDrawerEntry {

	@Override
	public String getTitle() {
		return "Web";
	}

	@Override
	public EntryType getType() {
		return EntryType.LABEL;
	}
	
	@Override
	public EntryStyle getStyle() {
		return EntryStyle.CATEGORY;
	}

	@Override
	public int getIcon() {
		return 0;
	}

	@Override
	public Class<? extends ExtendedFragment> getFragmentType() {
		return null;
	}

	@Override
	public void runAction(Activity activity) {
		// None
	}

}
