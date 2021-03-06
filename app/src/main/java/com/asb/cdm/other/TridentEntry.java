package com.asb.cdm.other;

import com.asb.cdm.R;
import com.asb.cdm.utils.android.ExtendedFragment;
import com.asb.cdm.utils.android.WebEntry;

/**
 * This class represents an entry on the Navigation Drawer
 */
public class TridentEntry extends WebEntry {

	public static final String TAG = "TridentEntry";

	@Override
	public String getTitle() {
		return "Trident Online";
	}


	@Override
	public int getIcon() {
		return R.drawable.ic_trident;
	}

	@Override
	public Class<ExtendedFragment> getFragmentType() {
		return null;
	}

    @Override
    public String getURL() {
        return TRIDENT_URL;
    }

	private static final String TRIDENT_URL = "http://www.tridentonline.net/";


}
