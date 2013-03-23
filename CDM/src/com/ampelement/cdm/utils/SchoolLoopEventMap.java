package com.ampelement.cdm.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ampelement.cdm.utils.SchoolLoopEvent;

public class SchoolLoopEventMap implements Serializable {
	/**
	 * Auto generated seriazable ID
	 */
	private static final long serialVersionUID = 116540645L;
	Map<String, ArrayList<SchoolLoopEvent>> eventMap;
	public ArrayList<String> activeDatesArrayList = new ArrayList<String>();

	public SchoolLoopEventMap() {
		this.eventMap = new HashMap<String, ArrayList<SchoolLoopEvent>>();
	}

	public boolean isEmpty() {
		return eventMap.isEmpty();
	}

	void add(SchoolLoopEvent event) {
		if (eventMap.containsKey(event.isoDate)) {
			ArrayList<SchoolLoopEvent> dayArrayList = eventMap.get(event.isoDate);
			dayArrayList.add(event);
		} else {
			ArrayList<SchoolLoopEvent> newArrayList = new ArrayList<SchoolLoopEvent>();
			newArrayList.add(event);
			eventMap.put(event.isoDate, newArrayList);
			activeDatesArrayList.add(event.isoDate);
		}
	}

	public ArrayList<SchoolLoopEvent> get(String key) {
		return eventMap.get(key);
	}

	public ArrayList<SchoolLoopEvent> get(int year, int month, int day) {
		ArrayList<SchoolLoopEvent> list = eventMap.get(toIsoDate(year, month, day));
		if (list == null)
			return new ArrayList<SchoolLoopEvent>();
		else
			return list;
	}

	public static String toIsoDate(int pyear, int pmonth, int pday) {
		String year = String.valueOf(pyear);
		String month = String.valueOf(pmonth + 1);
		String day = String.valueOf(pday);
		if (month.length() < 2)
			month = "0" + month;
		if (day.length() < 2)
			day = "0" + day;
		return year + "-" + month + "-" + day;
	}
}
