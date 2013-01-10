/*
 * Copyright (C) 2011 Chris Gao <chris@exina.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ampelement.cdm.calendar;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.MonthDisplayHelper;
import android.view.MotionEvent;
import android.view.View;

import com.ampelement.cdm.R;
import com.ampelement.cdm.utils.SchoolLoopAPI;

public class CalendarView extends View {
	private static int WEEK_HEIGHT = 0;
	private static int CELL_WIDTH = 58;
	private static int CELL_HEIGHT = 53;
	private static float CELL_TEXT_SIZE;

	private static final String TAG = "CalendarView";
	private Calendar mRightNow = null;
	private Cell[][] mCells = new Cell[8][7];
	private OnCellTouchListener mOnCellTouchListener = null;
	MonthDisplayHelper mHelper;

	private Paint mTextPaint;
	private Paint mBGPaint;
	private Rect mMonthBound;
	private Rect mWeekBound;
	private float touchY, ogTouchY;
	private boolean isBeingTouched;
	private boolean isDragged;
	private float ZERO;
	private int MONTH_ROWS;
	private int WEEK_OFFSET;
	private int MONTH_OFFSET;

	String[] mWeekTitles = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

	int selectedCellDay;
	int selectedCellMonth;

	private ArrayList<String> activeDaysList = new ArrayList<String>(0);

	public interface OnCellTouchListener {
		public void onTouch(Cell cell);
	}

	public CalendarView(Context context) {
		this(context, null);
	}

	public CalendarView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initCalendarView();
	}

	public void setActiveDayList(ArrayList<String> days) {
		activeDaysList = days;
		this.invalidate();
	}

	public void setSelectedDate(int day, int month) {
		selectedCellDay = day;
		selectedCellMonth = month;
		this.invalidate();
	}

	private void initCalendarView() {
		mRightNow = Calendar.getInstance();
		// prepare static vars
		Resources res = getResources();

		CELL_TEXT_SIZE = res.getDimension(R.dimen.cell_text_size);
		WEEK_HEIGHT = (int) (CELL_TEXT_SIZE * 1.3);

		mHelper = new MonthDisplayHelper(mRightNow.get(Calendar.YEAR), mRightNow.get(Calendar.MONTH));

		mTextPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextSize(CELL_TEXT_SIZE/* 26f */);
		mTextPaint.setColor(Color.GRAY);
		mMonthBound = new Rect(getPaddingLeft(), getPaddingTop(), (CELL_WIDTH + 1) * 7, WEEK_HEIGHT);
		mWeekBound = new Rect(getPaddingLeft(), getPaddingTop() + WEEK_HEIGHT, CELL_WIDTH + 1, WEEK_HEIGHT * 2);
		mBGPaint = new Paint();
		mBGPaint.setColor(0xffEEEEEE);
		mBGPaint.setStyle(Paint.Style.FILL);
	}

	int[] getWeek(int row) {
		int adjRow = row + WEEK_OFFSET;
		if (adjRow >= 0 && adjRow <= 5) {
			MONTH_ROWS++;
			return mHelper.getDigitsForRow(adjRow);
		} else if (adjRow < 0) {
			for (int i = 0; i < MONTH_OFFSET; i++) {
				mHelper.previousMonth();
			}
			int[] weekDays = mHelper.getDigitsForRow((2 - adjRow) % 5);
			for (int i = 0; i < MONTH_OFFSET; i++) {
				mHelper.nextMonth();
			}
			return weekDays;
		} else {
			for (int i = 0; i < MONTH_OFFSET; i++) {
				mHelper.nextMonth();
			}
			int[] weekDays = mHelper.getDigitsForRow(adjRow - 5);
			for (int i = 0; i < MONTH_OFFSET; i++) {
				mHelper.previousMonth();
			}
			return weekDays;
		}
	}

	boolean getWithinDrawMonth(int row, int column) {
		boolean value = false;
		for (int i = 0; i < MONTH_OFFSET; i++) {
			if (MONTH_OFFSET > 0)
				mHelper.previousMonth();
			else
				mHelper.nextMonth();
		}
		int adjRow = row + WEEK_OFFSET + (MONTH_OFFSET * MONTH_ROWS);
		if (adjRow >= 0 && adjRow <= 5)
			value = mHelper.isWithinCurrentMonth(adjRow, column);
		for (int i = 0; i < MONTH_OFFSET; i++) {
			if (MONTH_OFFSET > 0)
				mHelper.nextMonth();
			else
				mHelper.previousMonth();
		}
		return value;
	}

	class _calendar {
		public int day;
		public boolean thisMonth;

		public _calendar(int d, boolean b) {
			day = d;
			thisMonth = b;
		}

		public _calendar(int d) {
			this(d, false);
		}
	}

	private void initCells() {

		_calendar tmp[][] = new _calendar[8][7];

		/*
		 * _calendar tmp6[][] = new _calendar[6][7]; for (int i = 0; i < tmp6.length; i++) { int n[] = mHelper.getDigitsForRow(i); for (int d = 0; d <
		 * n.length; d++) { if (mHelper.isWithinCurrentMonth(i, d)) tmp6[i][d] = new _calendar(n[d], true); else tmp6[i][d] = new _calendar(n[d]);
		 * 
		 * } } mHelper.previousMonth(); int n1[] = mHelper.getDigitsForRow(3); mHelper.nextMonth(); mHelper.nextMonth(); int n8[] =
		 * mHelper.getDigitsForRow(1); mHelper.previousMonth();
		 * 
		 * for (int i = 0; i < tmp.length; i++) { if (i != 0 && i != 7) { for (int d = 0; d < tmp[i].length; d++) { tmp[i][d] = tmp6[i - 1][d]; } }
		 * else { for (int d = 0; d < tmp[i].length; d++) { if (i == 0) tmp[i][d] = new _calendar(n1[d], false); else tmp[i][d] = new _calendar(n8[d],
		 * false); } } }
		 */

		for (int i = 0; i < tmp.length; i++) {
			int row[] = getWeek(i - 1);
			for (int d = 0; d < tmp[i].length; d++) {
				tmp[i][d] = new _calendar(row[d], getWithinDrawMonth(i - 1, d));
			}
		}
		
		MONTH_ROWS = 0;

		Calendar today = Calendar.getInstance();
		int thisDay = 0;
		if (mHelper.getYear() == today.get(Calendar.YEAR) && mHelper.getMonth() == today.get(Calendar.MONTH)) {
			thisDay = today.get(Calendar.DAY_OF_MONTH);
		}
		// build cells
		Rect Bound = new Rect(getPaddingLeft(), (int) (getPaddingTop() + WEEK_HEIGHT * 2 + ZERO - CELL_HEIGHT), CELL_WIDTH + getPaddingLeft(), (int) (WEEK_HEIGHT * 2 + getPaddingTop() + ZERO));
		for (int week = 0; week < mCells.length; week++) {
			for (int day = 0; day < mCells[week].length; day++) {
				int backgroundColor = 0xffEEEEEE;
				int columbiaBlue = 0xff9BDDFF;
				int textColor = Color.LTGRAY;
				/* Calculate the current displayed month, store into int */
				int cellMonth = tmp[week][day].thisMonth ? mHelper.getMonth() : (tmp[week][day].day > 15) ? mHelper.getMonth() - 1 : mHelper.getMonth() + 1;
				/* Check if the current cell has events on it. If so then set background color to RED */
				if (activeDaysList.contains(SchoolLoopAPI.EventMap.toIsoDate(mHelper.getYear(), cellMonth, tmp[week][day].day)))
					backgroundColor = columbiaBlue;
				/* Check if the current cell is today. If so then set the background color to WHITE */
				if (tmp[week][day].day == thisDay && tmp[week][day].thisMonth)
					backgroundColor = Color.WHITE;
				/* Check if the current cell is not in this month. If so then set the text color to LTGRAY */
				if (tmp[week][day].thisMonth) {
					textColor = Color.DKGRAY;
					backgroundColor = 0xffDDDDDD;
				}

				mCells[week][day] = new Cell(tmp[week][day].day, cellMonth, new Rect(Bound), CELL_TEXT_SIZE, false, textColor, backgroundColor);

				if (tmp[week][day].day == selectedCellDay && mCells[week][day].getMonth() == selectedCellMonth)
					mCells[week][day].setSelected();
				Bound.offset(CELL_WIDTH + 1, 0); // move to next column
			}
			Bound.offset(0, CELL_HEIGHT + 1); // move to next row and first column
			Bound.left = getPaddingLeft();
			Bound.right = getPaddingLeft() + CELL_WIDTH;
		}
	}

	public void setTimeInMillis(long milliseconds) {
		mRightNow.setTimeInMillis(milliseconds);
		initCells();
		this.invalidate();
	}

	public int getYear() {
		return mHelper.getYear();
	}

	public int getMonth() {
		return mHelper.getMonth();
	}

	public String getMonthString() {
		String monthName = new DateFormatSymbols().getMonths()[mHelper.getMonth()];
		return monthName;
	}

	public void nextMonth() {
		mHelper.nextMonth();
		initCells();
		invalidate();
	}

	public void previousMonth() {
		mHelper.previousMonth();
		initCells();
		invalidate();
	}

	public boolean firstDay(int day) {
		return day == 1;
	}

	public boolean lastDay(int day) {
		return mHelper.getNumberOfDaysInMonth() == day;
	}

	public void goToday() {
		Calendar cal = Calendar.getInstance();
		mHelper = new MonthDisplayHelper(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
		initCells();
		invalidate();
	}

	public Calendar getDate() {
		return mRightNow;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isBeingTouched = true;
			touchY = ogTouchY = event.getRawY();
			break;
		case MotionEvent.ACTION_UP:
			if (!isDragged) {
				if (mOnCellTouchListener != null) {
					for (Cell[] week : mCells) {
						for (Cell day : week) {
							if (day.hitTest((int) event.getX(), (int) event.getY())) {
								mOnCellTouchListener.onTouch(day);
							}
						}
					}
				}
			}
			isBeingTouched = false;
			isDragged = false;
			MONTH_OFFSET = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			if (isDragged || Math.abs(event.getRawY() - ogTouchY) > 10) {
				isDragged = true;
				float change = (event.getRawY() - ogTouchY);
				ZERO = change % CELL_HEIGHT;
				WEEK_OFFSET = WEEK_OFFSET + (int) -((change - ZERO) / CELL_HEIGHT);
				if (Math.abs(WEEK_OFFSET) == MONTH_ROWS) {
					if (WEEK_OFFSET > 0) {
						MONTH_OFFSET++;
					} else {
						MONTH_OFFSET--;
					}
					WEEK_OFFSET = 0;
					ogTouchY = event.getRawY();
				}
				this.requestLayout();
				this.invalidate();
			} else {
				isDragged = false;
			}
			touchY = event.getRawY();
			break;
		}
		return true;
	}

	public void setOnCellTouchListener(OnCellTouchListener p) {
		mOnCellTouchListener = p;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		CELL_HEIGHT = (height - WEEK_HEIGHT * 2) / 6 - 1;
		CELL_WIDTH = width / 7;
		mMonthBound.set(getPaddingLeft(), getPaddingTop(), (CELL_WIDTH + 1) * 7, WEEK_HEIGHT);
		mWeekBound.set(getPaddingLeft(), getPaddingTop() + WEEK_HEIGHT, CELL_WIDTH + 1, WEEK_HEIGHT * 2);
		setMeasuredDimension(width, height);
	}

	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom) {
		initCells();
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// draw background
		super.onDraw(canvas);

		// draw cells
		for (Cell[] week : mCells) {
			for (Cell day : week) {
				day.draw(canvas);
			}
		}

		canvas.drawRect(mMonthBound, mBGPaint);
		int monthX = (int) mTextPaint.measureText(getMonthString());
		int monthY = (int) (-mTextPaint.ascent() + mTextPaint.descent());
		canvas.drawText(getMonthString() + " - " + String.valueOf(mHelper.getYear()), mMonthBound.centerX() - monthX / 2, mMonthBound.top + (WEEK_HEIGHT - (monthY / 4)), mTextPaint);

		for (String weekTitle : mWeekTitles) {
			canvas.drawRect(mWeekBound, mBGPaint);
			int dx = (int) mTextPaint.measureText(weekTitle);
			int dy = (int) (-mTextPaint.ascent() + mTextPaint.descent());
			canvas.drawText(weekTitle, mWeekBound.right - (dx + 7), mWeekBound.top + (WEEK_HEIGHT - (dy / 4)), mTextPaint);
			mWeekBound.offset(CELL_WIDTH + 1, 0);
		}
		mWeekBound.offset(-(CELL_WIDTH + 1) * 7, 0);
	}

}
