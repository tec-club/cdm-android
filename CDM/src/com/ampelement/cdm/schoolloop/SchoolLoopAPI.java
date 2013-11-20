package com.ampelement.cdm.schoolloop;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.ampelement.cdm.Preferences;
import com.ampelement.cdm.schoolloop.SchoolLoopEvent.SchoolLoopEventBuilder;

public class SchoolLoopAPI {
	public final static String TAG = "SchoolLoopAPI";
	public static final String BASE_URL_SECURE = "https://cdm.schoolloop.com";
	public static final String BASE_URL = "http://cdm.schoolloop.com";

	public static class EventFetcher {
		// public static final String EVENT_RSS_URL =
		// "http://ampelement.com/cdm/test_rss.xml";

		public static final String EVENT_RSS_URL = BASE_URL + "/cms/rss?d=x&group_id=1204427108703&types=_assignment__event_&include_subgroups=t";

		public EventFetcher() {
		}

		public SchoolLoopEventMap fetchEvents() {
			try {
				// SetUp Parser
				SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
				SAXParser saxParser = saxParserFactory.newSAXParser();
				XMLReader xmlReader = saxParser.getXMLReader();
				URL url = new URL(EVENT_RSS_URL);
				EventParser eventParser = new EventParser();
				xmlReader.setContentHandler(eventParser);
				xmlReader.parse(new InputSource(url.openStream()));
				// Add final item which isn't added due to their not being a
				// start element ("item") after it
				eventParser.eventMap.addEvent(eventParser.currentEventBuilder.isoDate, eventParser.currentEventBuilder.build());
				// Return Results
				return eventParser.eventMap;
			} catch (Exception e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				return null;
			}
		}
	}

	private static class EventParser extends DefaultHandler {
		StringBuilder content = null;
		Boolean elementOn = false;
		SchoolLoopEventBuilder currentEventBuilder = null;
		SchoolLoopEventMap eventMap = new SchoolLoopEventMap();

		/**
		 * This will be called when the tags of the XML starts.
		 **/
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			elementOn = true;
			content = new StringBuilder();
			if (localName.equals("item")) {
				if (currentEventBuilder != null) {
					eventMap.addEvent(currentEventBuilder.isoDate, currentEventBuilder.build());
				}
				currentEventBuilder = new SchoolLoopEventBuilder();
			}
		}

		/**
		 * This will be called when the tags of the XML end.
		 **/
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			elementOn = false;
			String elementValue = content.toString();
			if (currentEventBuilder != null) {
				if (localName.equalsIgnoreCase("title")) {
					currentEventBuilder.setTitle(elementValue);
				} else if (localName.equalsIgnoreCase("location")) {
					currentEventBuilder.setLocation(elementValue);
				} else if (localName.equalsIgnoreCase("isodate")) {
					currentEventBuilder.setDate(elementValue);
				} else if (localName.equalsIgnoreCase("allday")) {
					currentEventBuilder.setAllDay(elementValue);
				} else if (localName.equalsIgnoreCase("starttime")) {
					currentEventBuilder.setStartTime(elementValue);
				} else if (localName.equalsIgnoreCase("endtime")) {
					currentEventBuilder.setEndTime(elementValue);
				} else if (localName.equalsIgnoreCase("additionalDesc")) {
					currentEventBuilder.setDescription(elementValue.trim());
				}
			}
		}

		/**
		 * This is called to get the tags value
		 **/
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			content.append(ch, start, length);
		}
	}

	public static CookieStore loginToSchoolloop(DefaultHttpClient httpclient, String pUserName, String pPassword, boolean checkLogin)
			throws ClientProtocolException, IOException {
		HttpResponse schoolloopLoginGetResponse = null;
		HttpGet schoolloopLoginHttpGet = new HttpGet(BASE_URL + "/portal/login");
		schoolloopLoginGetResponse = httpclient.execute(schoolloopLoginHttpGet);

		String schoolLoopString = EntityUtils.toString(schoolloopLoginGetResponse.getEntity());
		Pattern p = Pattern.compile("<input\\b[^>]+\\bname=\"form_data_id\"[^>]+\\bvalue=\"([0-9]*)\"");
		Matcher m = p.matcher(schoolLoopString);
		if (m.find()) {
			String formDataIDString = m.group(1);

			HttpPost schoolloopLoginHttpPost = new HttpPost(BASE_URL + "/portal/login?etarget=login_form");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("login_name", pUserName));
			nameValuePairs.add(new BasicNameValuePair("password", pPassword));
			nameValuePairs.add(new BasicNameValuePair("form_data_id", formDataIDString));
			nameValuePairs.add(new BasicNameValuePair("event_override", "login"));
			String[] blankFields = { "reverse", "sort", "login_form_reverse", "login_form_page_index", "login_form_page_item_count", "login_form_sort",
					"return_url", "forward", "redirect", "login_form_letter", "login_form_filter" };
			populateNVListWithBlank(nameValuePairs, blankFields);
			schoolloopLoginHttpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse schoolloopLoginPostResponse = httpclient.execute(schoolloopLoginHttpPost);
			if (checkLogin) {
				if (EntityUtils.toString(schoolloopLoginPostResponse.getEntity()).contains("form_data_id")) {
					return null;
				} else {
					return httpclient.getCookieStore();
				}
			} else {
				return httpclient.getCookieStore();
			}
		} else {
			return null;
		}
	}

	private static void populateNVListWithBlank(List<NameValuePair> list, String[] array) {
		for (String name : array) {
			list.add(new BasicNameValuePair(name, ""));
		}
	}

	public static class Dirty {

		public static void loadLoginDataToWebView(SharedPreferences sharedPref, WebView webView) {
			CookieSyncManager syncManager = CookieSyncManager.createInstance(webView.getContext());
			CookieManager cookieManager = CookieManager.getInstance();
			loadLoginDataToCookieManager(sharedPref, cookieManager);
			CookieSyncManager.getInstance().sync();
		}

		public static void loadLoginDataToCookieManager(SharedPreferences sharedPref, CookieManager dest) {
			dest.setCookie("http://cdm.schoolloop.com/", "JSESSIONID=" + sharedPref.getString(Preferences.SCHOOL_LOOP_JSESSIONID, ""));
			dest.setCookie("http://cdm.schoolloop.com/", "slid=" + sharedPref.getString(Preferences.SCHOOL_LOOP_SLID, ""));
		}

		public static void migrateCookieStore2CookieManager(CookieStore orig, CookieManager dest, SharedPreferences.Editor prefEditor) {
			for (Cookie cookie : orig.getCookies()) {
				dest.setCookie((cookie.isSecure() ? "https" : "http") + "://" + cookie.getDomain() + cookie.getPath(),
						cookie.getName() + "=" + cookie.getValue());
				// For saving the cookie values to Preferences
				// TODO This is dirty and I don't like it - Alex Wendland
				if (prefEditor != null) {
					if (cookie.getName().matches("slid")) {
						prefEditor.putString(Preferences.SCHOOL_LOOP_SLID, cookie.getValue());
						prefEditor.commit();
					} else if (cookie.getName().matches("JSESSIONID")) {
						prefEditor.putString(Preferences.SCHOOL_LOOP_JSESSIONID, cookie.getValue());
						prefEditor.commit();
					}
				}
			}
		}

		public static void migrateCookieStore2WebView(CookieStore orig, WebView webView, SharedPreferences.Editor prefEditor) {
			CookieSyncManager syncManager = CookieSyncManager.createInstance(webView.getContext());
			CookieManager cookieManager = CookieManager.getInstance();
			migrateCookieStore2CookieManager(orig, cookieManager, prefEditor);
			CookieSyncManager.getInstance().sync();
		}
	}

}