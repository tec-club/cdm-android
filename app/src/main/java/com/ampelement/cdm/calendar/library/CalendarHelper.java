package com.ampelement.cdm.calendar.library;

import org.joda.time.DateTimeConstants;
import org.joda.time.MutableDateTime;

public class CalendarHelper {
	
	private static final int DAY_CACHE_SIZE = 52;

	String[] mWeekTitles = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

    int mFirstDayOfWeek = DateTimeConstants.SUNDAY;

	private MutableDateTime mCurrentDateTime;

    private MutableDateTime mTimeToMessWith; //The DateTime that is used to modify other dates than the current one

    private int mDisplayMonth;
    private int mDisplayYear;

    /**
     * @param firstDayOfWeek Self-explanatory
     */
    public CalendarHelper(int firstDayOfWeek) {
        mCurrentDateTime = new MutableDateTime();
		mTimeToMessWith = new MutableDateTime();
        mFirstDayOfWeek = firstDayOfWeek;
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


    private Day[][] dayCache = new Day[DAY_CACHE_SIZE][7]; //Array of every single day of this year
    private int dayCacheStartingWeek;
    private int dayCacheStartingYear;

    /**
     * @param offset Number of weeks difference from current week
     * @return A Day[] containing the days of the specified week
     */
    Day[] getWeekByOffset(int offset) {
        setTimeToMessWith(offset);
		return getWeek(mTimeToMessWith.getWeekOfWeekyear(), mTimeToMessWith.getYear());
    }

    /**
     * @param weekOfYear The week number of the year <=52
     * @param year       The year
     * @return The days of the week in that year and week number
     */
    Day[] getWeek(int weekOfYear, int year) {
        int index = weekToCacheIndex(weekOfYear, year);
		if(index < 0 || index >= DAY_CACHE_SIZE)
			buildDayCache();
		return dayCache[weekToCacheIndex(weekOfYear, year)];
    }

    /**
     * @param weekOfYear The week number <=52
     * @param year       The year
     * @return The difference in weeks between weekOfYear, year and the current date; <0 means previous
     */
    int weekToCacheIndex(int weekOfYear, int year) {
        int index = weekOfYear - dayCacheStartingWeek;
		int yearAdjustment = year - dayCacheStartingYear;
		int indexItem = index + (52 * yearAdjustment);
		return indexItem - 1;
    }

    /**
     * For every value in dayCache[][] create a new Day object with values in mTimeToMessWith and
     * whether it is in the current displayMonth, this will create a value for every single day in
     * this currentYear
     */
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

    /**
     * Sets mTimeToMessWith at mFirstDayOfWeek  in weekOffset weeks if you get what I mean there
     * @param weekOffset Number of weeks in the future from the current date
     */
    void setTimeToMessWith(int weekOffset) {
        mTimeToMessWith.setDate(mCurrentDateTime.getMillis());
		mTimeToMessWith.addWeeks(weekOffset);
        mTimeToMessWith.setDayOfWeek(mFirstDayOfWeek);
    }

    //adds a month to the currentDateTime
    void nextMonth() {
        mCurrentDateTime.addMonths(1);
    }

    //subtracts a month from the currentDateTime
    void previousMonth() {
        mCurrentDateTime.addMonths(-1);
    }

    //sets the current display to this month and year
    void setDisplayMonthCurrent() {
        setDisplayMonth(getMonth(), getYear());
    }

    //setter
    void setDisplayMonth(int month, int year) {
        mDisplayMonth = month;
		mDisplayYear = year;
	}

    /**
     *
     * @return True if mTimeToMessWith is in the same year and month as the current mDisplayMonth nad mDisplayYear
     */
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
