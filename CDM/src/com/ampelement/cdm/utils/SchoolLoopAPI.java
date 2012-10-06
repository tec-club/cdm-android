package com.ampelement.cdm.utils;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SchoolLoopAPI {
	public static final String BASE_URL = "https://cdm.schoolloop.com";

	public static class EventFetcher {
		public static final String EVENT_RSS_URL = BASE_URL + "/cms/rss?d=x&group_id=1204427108703&types=_assignment__event_&include_subgroups=t";
		public EventFetcher() {
		}
		public ArrayList<Event> fetchEvents() {
			try {
				//SetUp Parser
			    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			    SAXParser saxParser = saxParserFactory.newSAXParser();
			    XMLReader xmlReader = saxParser.getXMLReader();
			    URL url = new URL(EVENT_RSS_URL);
			    EventParser eventParser = new EventParser();
			    xmlReader.setContentHandler(eventParser);
			    xmlReader.parse(new InputSource(url.openStream()));
			    // Get results
			    ArrayList<Event> eventList = eventParser.eventList;
			    // Add final item which isn't added due to their not being a start element ("item") after it
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
}
