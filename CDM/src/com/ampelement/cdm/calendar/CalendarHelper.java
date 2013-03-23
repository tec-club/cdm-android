package com.ampelement.cdm.calendar;

import org.joda.time.DateTimeConstants;
import org.joda.time.MutableDateTime;

import android.util.Log;

public class CalendarHelper {

	String[] mWeekTitles = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

	int mFisrtDayOfWeek = DateTimeConstants.SUNDAY;

	private MutableDateTime mCurrentDateTime;

	private MutableDateTime mTimeToMessWith;

	private int mDisplayMonth;;
	private int mDisplayYear;

	public CalendarHelper(int firstDayOfWeek) {
		mCurrentDateTime = new MutableDateTime();
		mFisrtDayOfWeek = firstDayOfWeek;
		setDisplayMonth(getMonth(), getYear());
	}

	String[] getWeekTitles() {
		return mWeekTitles;
	}

	void addWeek(int value) {
		mCurrentDateTime.addWeeks(1);
	}

	Day[] getWeek(int offset) {
		long startTime = System.nanoTime();
		Day[] days = new Day[7];
		Log.d("a", String.valueOf(System.nanoTime() - startTime));
		setTimeToMessWith(offset);
		Log.d("c", String.valueOf(System.nanoTime() - startTime));
		for (int dayWeek = 0; dayWeek < 7; dayWeek++) {
			days[dayWeek] = new Day(mTimeToMessWith.getDayOfMonth(), mTimeToMessWith.getMonthOfYear(), mTimeToMessWith.getYear(), getWithinDisplayMonth(), dayWeek);
			mTimeToMessWith.addDays(1);
		}
		Log.d("d", String.valueOf(System.nanoTime() - startTime));
		return days;
	}

	void setTimeToMessWith(int weekOffset) {
		mTimeToMessWith.setDate(mCurrentDateTime.getMillis());
		mTimeToMessWith.addWeeks(weekOffset);
		mTimeToMessWith.setDayOfWeek(mFisrtDayOfWeek);
	}

	void nextMonth() {
		mCurrentDateTime.addMonths(1);
	}

	void previousMonth() {
		mCurrentDateTime.addMonths(-1);
	}

	void setDisplayMonthCurrent() {
		setDisplayMonth(getMonth(), getYear());
	}

	void setDisplayMonth(int month, int year) {
		mDisplayMonth = month;
		mDisplayYear = year;
	}

	boolean getWithinDisplayMonth() {
		if (mTimeToMessWith.getMonthOfYear() == mDisplayMonth)
			if (mTimeToMessWith.getYear() == mDisplayYear)
				return true;
		return false;
	}

	int getYear() {
		return mCurrentDateTime.getYear();
	}

	int getMonth() {
		return mCurrentDateTime.getMonthOfYear();
	}

	int getDaysInMonth(int offset) {
		mTimeToMessWith.setDate(mCurrentDateTime.getMillis());
		mTimeToMessWith.addMonths(offset);
		return mTimeToMessWith.dayOfMonth().getMaximumValue();
	}

	public static class Day {
		int day;
		int month;
		int year;
		boolean withinCurrentMonth;
		int dayOfWeek;

		public Day(int day, int month, int year, boolean withinCurrentMonth, int dayOfWeek) {
			this.day = day;
			this.month = month;
			this.year = year;
			this.withinCurrentMonth = withinCurrentMonth;
			this.dayOfWeek = dayOfWeek;
		}
	}

}
