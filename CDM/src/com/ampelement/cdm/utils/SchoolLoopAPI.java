package com.ampelement.cdm.utils;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class SchoolLoopAPI {
	public static final String BASE_URL_SECURE = "https://cdm.schoolloop.com";
	public static final String BASE_URL = "http://cdm.schoolloop.com";

	public static class EventFetcher {
		public static final String EVENT_RSS_URL = BASE_URL + "/cms/rss?d=x&group_id=1204427108703&types=_assignment__event_&include_subgroups=t";

		public EventFetcher() {
		}

		public ArrayList<Event> fetchEvents() {
			try {
				// SetUp Parser
				SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
				SAXParser saxParser = saxParserFactory.newSAXParser();
				XMLReader xmlReader = saxParser.getXMLReader();
				URL url = new URL(EVENT_RSS_URL);
				EventParser eventParser = new EventParser();
				xmlReader.setContentHandler(eventParser);
				xmlReader.parse(new InputSource(url.openStream()));
				// Get results
				ArrayList<Event> eventList = eventParser.eventList;
				// Add final item which isn't added due to their not being a
				// start element ("item") after it
				eventList.add(eventParser.currentEvent);
				// Return Results
				return eventList;
			} catch (Exception e) {
				return null;
			}
		}
	}

	private static class EventParser extends DefaultHandler {
		StringBuilder content = null;
		Boolean elementOn = false;
		Event currentEvent = null;
		ArrayList<Event> eventList = new ArrayList<Event>();

		/**
		 * This will be called when the tags of the XML starts.
		 **/
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			elementOn = true;
			content = new StringBuilder();
			if (localName.equals("item")) {
				if (currentEvent != null) {
					eventList.add(currentEvent);
				}
				currentEvent = new Event();
			}
		}

		/**
		 * This will be called when the tags of the XML end.
		 **/
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			elementOn = false;
			String elementValue = content.toString();
			if (currentEvent != null) {
				if (localName.equalsIgnoreCase("title")) {
					currentEvent.title = elementValue;
				} else if (localName.equalsIgnoreCase("location")) {
					currentEvent.location = elementValue;
				} else if (localName.equalsIgnoreCase("isodate")) {
					currentEvent.isoDate = elementValue;
				} else if (localName.equalsIgnoreCase("allday")) {
					currentEvent.allDay = elementValue;
				} else if (localName.equalsIgnoreCase("starttime")) {
					currentEvent.startTime = elementValue;
				} else if (localName.equalsIgnoreCase("endtime")) {
					currentEvent.endTime = elementValue;
				} else if (localName.equalsIgnoreCase("additionalDesc")) {
					currentEvent.description = elementValue.trim();
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

	public static class Event implements Serializable {
		public String title;
		public String location;
		public String isoDate;
		public String allDay;
		public String startTime;
		public String endTime;
		public String description;
	}

	public static CookieStore loginToSchoolloop(DefaultHttpClient httpclient, String pUserName, String pPassword, boolean checkLogin) throws ClientProtocolException, IOException {
		HttpResponse schoolloopLoginGetResponse = null;
		HttpGet schoolloopLoginHttpGet = new HttpGet(BASE_URL + "/portal/login");
		schoolloopLoginGetResponse = httpclient.execute(schoolloopLoginHttpGet);

		Document schoolloopLoginGetDocument = Jsoup.parse(EntityUtils.toString(schoolloopLoginGetResponse.getEntity()));
		Element formDataIDElement = schoolloopLoginGetDocument.getElementById("form_data_id");
		String formDataIDString = formDataIDElement.attr("value").toString();

		HttpPost schoolloopLoginHttpPost = new HttpPost(BASE_URL + "/portal/login?etarget=login_form");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("login_name", pUserName));
		nameValuePairs.add(new BasicNameValuePair("password", pPassword));
		nameValuePairs.add(new BasicNameValuePair("form_data_id", formDataIDString));
		nameValuePairs.add(new BasicNameValuePair("event_override", "login"));
		String[] blankFields = { "reverse", "sort", "login_form_reverse", "login_form_page_index", "login_form)page_item_count", "login_form_sort", "return_url", "forward", "redirect", "login_form_letter", "login_form_filter" };
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
	}

	static void populateNVListWithBlank(List<NameValuePair> list, String[] array) {
		for (String name : array) {
			list.add(new BasicNameValuePair(name, ""));
		}
	}

}
