package com.ampelement.cdm.clubs;

import android.net.Uri;

public class ClubData {

	public String name;
	public String logoURL;// = "http://thecatapi.com/api/images/get?format=src&type=jpg&ran=" + Integer.toHexString((int) (Math.random() * 10000));
	public String description;
	public String[] meetingTimes;
	public String president;
	public String advisor;

	public Uri getLogoUrl() {
		return Uri.parse(logoURL);
	}

}
