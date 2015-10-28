package com.asb.cdm.other;

import com.asb.cdm.R;
import com.asb.cdm.utils.android.WebEntry;

/**
 * This class represents an entry on the Navigation Drawer
 */
public class InstagramEntry extends WebEntry {

	public static final String TAG = "InstagramEntry";

	@Override
	public String getTitle() {
		return "Instagram";
	}


	@Override
	public int getIcon() {
		return R.drawable.ic_instagram;
	}

    @Override
    public String getURL() {
        return INSTAGRAM_URL;
    }


    private static final String INSTAGRAM_URL = "https://instagram.com/cdmasb";

}
