package com.ampelement.cdm.calendar.library;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;

import android.util.Log;

public class CalendarHelper {
	
	private static final int DAY_CACHE_SIZE = 52;

	String[] mWeekTitles = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

	int mFisrtDayOfWeek = DateTimeConstants.SUNDAY;

	private MutableDateTime mCurrentDateTime;

	private MutableDateTime mTimeToMessWith;

	private int mDisplayMonth;;
	private int mDisplayYear;

	public CalendarHelper(int firstDayOfWeek) {
		mCurrentDateTime = new MutableDateTime();
		mTimeToMessWith = new MutableDateTime();
		mFisrtDayOfWeek = firstDayOfWeek;
		setDisplayMonth(getMonth(), getYear());
		dayCacheStartingWeek = mCurrentDateTime.getWeekOfWeekyear();
		dayCacheStartingYear = mCurrentDateTime.getYear();
	}

	String[] getWeekTitles() {
		return mWeekTitles;
	}

	void addWeek(int value) {
		mCurrentDateTime.addWeeks(value);
	}
	
	private Day[][] dayCache = new Day[DAY_CACHE_SIZE][7];
	private int dayCacheStartingWeek;
	private int dayCacheStartingYear;
	
	Day[] getWeekByOffset(int offset) {
		setTimeToMessWith(offset);
		return getWeek(mTimeToMessWith.getWeekOfWeekyear(), mTimeToMessWith.getYear());
	}
	
	Day[] getWeek(int weekOfYear, int year) {
		int index = weekToCacheIndex(weekOfYear, year);
		if(index < 0 || index >= DAY_CACHE_SIZE)
			buildDayCache();
		return dayCache[weekToCacheIndex(weekOfYear, year)];
	}
	
	int weekToCacheIndex(int weekOfYear, int year) {
		int index = weekOfYear - dayCacheStartingWeek;
		int yearAdjustment = year - dayCacheStartingYear;
		int indexItem = index + (52 * yearAdjustment);
		return indexItem - 1;
	}
	
	void buildDayCache() {
		setTimeToMessWith(DAY_CACHE_SIZE / -2);
		dayCacheStartingWeek = mTimeToMessWith.getWeekOfWeekyear();
		dayCacheStartingYear = mTimeToMessWith.getYear();
		for (int i = 0; i < DAY_CACHE_SIZE; i++) {
			for (int dayWeek = 0; dayWeek < 7; dayWeek++) {
				dayCache[i][dayWeek] = new Day(mTimeToMessWith.getDayOfMonth(), mTimeToMessWith.getMonthOfYear(), mTimeToMessWith.getYear(), getWithinDisplayMonth(), dayWeek);
				mTimeToMessWith.addDays(1);
			}
		}
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
