package com.asb.cdm.other;

import com.asb.cdm.R;
import com.asb.cdm.utils.android.ExtendedFragment;
import com.asb.cdm.utils.android.WebEntry;

/**
 * This class represents an entry on the Navigation Drawer
 */
public class TwitterEntry extends WebEntry {

	public static final String TAG = "TwitterEntry";

	@Override
	public String getTitle() {
		return "Twitter";
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
    private static final String TWITTER_URL = "https://mobile.twitter.com/" + TWITTER_USERNAME;

    @Override
    public String getURL() {
        return TWITTER_URL;
    }

}
