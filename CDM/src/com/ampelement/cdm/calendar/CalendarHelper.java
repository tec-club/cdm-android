package com.ampelement.cdm.calendar;

import java.util.Calendar;

public class CalendarHelper {

	String[] mWeekTitles = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

	int mFisrtDayOfWeek = Calendar.SUNDAY;

	private Calendar mCurrentCalendar;

	public CalendarHelper(int firstDayOfWeek) {
		mCurrentCalendar = Calendar.getInstance();
		mFisrtDayOfWeek = firstDayOfWeek;
	}

	String[] getWeekTitles() {
		return mWeekTitles;
	}

	void addWeek(int value) {
		mCurrentCalendar.add(Calendar.DATE, value * 7);
	}

	Day[] getWeek(int offset) {
		Day[] days = new Day[7];
		Calendar calendar = getCalendarRelativeToCurrent(offset);
		calendar.set(Calendar.DAY_OF_WEEK, mFisrtDayOfWeek);
		for (int dayWeek = 0; dayWeek < 7; dayWeek++) {
			days[dayWeek] = new Day(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), sameMonth(calendar), dayWeek);
			calendar.add(Calendar.DATE, 1);
		}
		return days;
	}

	void nextMonth() {
		mCurrentCalendar.add(Calendar.MONTH, 1);
	}

	void previousMonth() {
		mCurrentCalendar.add(Calendar.MONTH, -1);
	}

	boolean sameMonth(Calendar calendar) {
		if (calendar.get(Calendar.MONTH) == mCurrentCalendar.get(Calendar.MONTH))
			if (calendar.get(Calendar.YEAR) == mCurrentCalendar.get(Calendar.YEAR))
				return true;

		return false;
	}

	int getYear() {
		return mCurrentCalendar.get(Calendar.YEAR);
	}

	int getMonth() {
		return mCurrentCalendar.get(Calendar.MONTH);
	}

	Calendar getCalendarRelativeToCurrent(int weekOffset) {
		Calendar calendar = getCopyCurrentCalendar();
		calendar.add(Calendar.DATE, weekOffset * 7);
		return calendar;
	}

	Calendar getCopyCurrentCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(mCurrentCalendar.get(Calendar.YEAR), mCurrentCalendar.get(Calendar.MONTH), mCurrentCalendar.get(Calendar.DAY_OF_MONTH), mCurrentCalendar.get(Calendar.HOUR_OF_DAY), mCurrentCalendar.get(Calendar.MINUTE), mCurrentCalendar.get(Calendar.SECOND));
		return calendar;
	}

	int getDaysInMonth(int offset) {
		Calendar calendar = getCopyCurrentCalendar();
		calendar.add(Calendar.MONTH, offset);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
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
