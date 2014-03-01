package com.ampelement.cdm.other;

import android.app.Activity;

import com.ampelement.cdm.utils.android.ExtendedSherlockFragment;
import com.ampelement.cdm.utils.android.NavDrawerEntry;
import com.ampelement.cdm.utils.android.NavDrawerEntry.EntryStyle;
import com.ampelement.cdm.utils.android.NavDrawerEntry.EntryType;

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
	public Class<? extends ExtendedSherlockFragment> getFragmentType() {
		return null;
	}

	@Override
	public void runAction(Activity activity) {
		// None
	}

}
