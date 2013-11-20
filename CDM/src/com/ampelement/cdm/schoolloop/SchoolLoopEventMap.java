package com.ampelement.cdm.schoolloop;

import com.ampelement.cdm.calendar.library.CalendarEvents;
import com.ampelement.cdm.schoolloop.SchoolLoopEvent;

public class SchoolLoopEventMap extends CalendarEvents<SchoolLoopEvent> {

	public boolean isEmpty() {
		return eventMap.isEmpty();
	}

	public SchoolLoopEvent[] getEvents(int year, int month, int day) {
		return (SchoolLoopEvent[]) getEvents(toIsoDate(year, month, day));
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
