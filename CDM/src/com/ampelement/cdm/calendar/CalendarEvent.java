package com.ampelement.cdm.calendar;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.Interval;

public class CalendarEvent {

	public Interval eventPeriod;

	public CalendarEvent(Interval timeSpan) {
		eventPeriod = timeSpan;
	}

	public CalendarEvent(long startInstant, long endInstant, Chronology chronology) {
		this(new Interval(startInstant, endInstant, chronology));
	}

	public CalendarEvent(DateTime startTime, DateTime endTime) {
		this(startTime.getMillis(), endTime.getMillis(), startTime.getChronology());
	}

	public String getIsoDate() {
		return eventPeriod.getStart().toString("yyyy-MM-dd");
	}

	int getStartMinute() {
		return eventPeriod.getStart().getMinuteOfDay();
	}

	int getEndMinute() {
		return eventPeriod.getEnd().getMinuteOfDay();
	}

}
