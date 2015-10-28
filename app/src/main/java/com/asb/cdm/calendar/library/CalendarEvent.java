package com.asb.cdm.calendar.library;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 */
public class CalendarEvent {

	public Interval eventPeriod;

    /*
    All constructors here create the CalendarEvent object with a joda.time.Interval
     */
    public CalendarEvent(Interval timeSpan) {
        eventPeriod = timeSpan;
	}

	public CalendarEvent(long startInstant, long endInstant, Chronology chronology) {
		this(new Interval(startInstant, endInstant, chronology));
	}

	public CalendarEvent(DateTime startTime, DateTime endTime) {
		this(startTime.getMillis(), endTime.getMillis(), startTime.getChronology());
	}

    public CalendarEvent(DateTime endTime) {
        this(System.currentTimeMillis(), endTime.getMillis(), new DateTime(System.currentTimeMillis()).getChronology());
    }


    /**
     * @return Date of when the Interval started
     */
    public String getIsoDate() {
        return eventPeriod.getStart().toString("yyyy-MM-dd");
	}

    /**
     * @return Minute of the day when the Interval started
     */
    int getStartMinute() {
        return eventPeriod.getStart().getMinuteOfDay();
	}

    /**
     *
     * @return Minute of the day when the Interval will end
     */
    int getEndMinute() {
        return eventPeriod.getEnd().getMinuteOfDay();
	}

}
