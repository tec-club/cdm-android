package com.ampelement.cdm.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class WebAPI {
	
	public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
	public static final String PRETTY_DATE_FORMAT = "MMMMM dd, yyyy";
	public static final String ISO_TIME_FORMAT = "HH:mm:ss";
	public static final String PRETTY_TIME_FORMAT = "HH.mm";

	public static ArrayList<HashMap<String, String>> getEvents() {
		ArrayList<HashMap<String, String>> eventList = new ArrayList<HashMap<String, String>>();
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
			HttpPost httppost = new HttpPost("http://manage.ampelement.com/events-get.php?orgID=1&groupID=1");
			HttpResponse response = httpclient.execute(httppost);
			String jsonString = EntityUtils.toString(response.getEntity());

			try {
				JSONObject jObject = new JSONObject(jsonString);
				Iterator<?> eventJSONKeys = jObject.keys();
				while (eventJSONKeys.hasNext()) {
					String key = (String) eventJSONKeys.next();
					if (jObject.get(key) instanceof JSONObject) {
						JSONObject eventJSONObject = (JSONObject) jObject.get(key);
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("title", eventJSONObject.getString("name"));
						map.put("date", unISODateFormat(eventJSONObject.getString("start_date")));
						map.put("time", unISOTimeFormat(eventJSONObject.getString("start_time")) + " to " + unISOTimeFormat(eventJSONObject.getString("end_time")));
						map.put("desc", eventJSONObject.getString("description"));
						eventList.add(map);
					}
				}
				if (eventList.size() == 0) {
					return null;
				} else {
					return eventList;
				}
			} catch (JSONException e) {
				return null;
			}
		} catch (ClientProtocolException e1) {
			return null;
		} catch (IOException e1) {
			return null;
		} catch (IllegalStateException e) {
			return null;
		}
	}
	
	public static String unISODateFormat(String dateString) {
		try {
			Date inputDate = new SimpleDateFormat(ISO_DATE_FORMAT).parse(dateString);
			return new SimpleDateFormat(PRETTY_DATE_FORMAT).format(inputDate);
		} catch (ParseException e) {
			return "";
		}
	}
	
	public static String unISOTimeFormat(String timeString) {
		try {
			Date inputTime = new SimpleDateFormat(ISO_TIME_FORMAT).parse(timeString);
			return new SimpleDateFormat(PRETTY_TIME_FORMAT).format(inputTime);
		} catch (ParseException e) {
			return "";
		}
	}

}
