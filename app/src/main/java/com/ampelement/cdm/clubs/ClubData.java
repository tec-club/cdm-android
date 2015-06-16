package com.ampelement.cdm.clubs;

import android.net.Uri;

public class ClubData {

	public String name;
//	public String logoURL;
	public String logoURL = "http://dummyimage.com/256/" + Integer.toHexString((int) (Math.random() * 255)) + Integer.toHexString((int) (Math.random() * 255)) + Integer.toHexString((int) (Math.random() * 255));
	public String description;
	public String[] meetingTimes;
	public String president;
	public String advisor;

	public Uri getLogoUrl() {
		return Uri.parse(logoURL);
	}

}
