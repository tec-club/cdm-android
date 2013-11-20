package com.ampelement.cdm.clubs;

import android.net.Uri;

public class ClubData {

	public String name;
	public String logoURL;
	public String description;
	public String[] meetingTimes;
	

	public Uri getLogoUrl() {
		return Uri.parse(logoURL);
	}

}
