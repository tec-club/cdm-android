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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.ampelement.cdm.R;
import com.ampelement.cdm.utils.SchoolLoopAPI;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.MonthDisplayHelper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class CalendarView extends View {
	private static int WEEK_HEIGHT = 0;
	private static int CELL_WIDTH = 58;
	private static int CELL_HEIGHT = 53;
	private static float CELL_TEXT_SIZE;

	private static final String TAG = "CalendarView";
	private Calendar mRightNow = null;
	private Cell[][] mCells = new Cell[6][7];
	private OnCellTouchListener mOnCellTouchListener = null;
	MonthDisplayHelper mHelper;

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
		WEEK_HEIGHT = (int) (CELL_TEXT_SIZE * 2);

		mHelper = new MonthDisplayHelper(mRightNow.get(Calendar.YEAR), mRightNow.get(Calendar.MONTH));

	}

	private void initCells() {
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
		;
		_calendar tmp[][] = new _calendar[6][7];

		for (int i = 0; i < tmp.length; i++) {
			int n[] = mHelper.getDigitsForRow(i);
			for (int d = 0; d < n.length; d++) {
				if (mHelper.isWithinCurrentMonth(i, d))
					tmp[i][d] = new _calendar(n[d], true);
				else
					tmp[i][d] = new _calendar(n[d]);

			}
		}

		Calendar today = Calendar.getInstance();
		int thisDay = 0;
		if (mHelper.getYear() == today.get(Calendar.YEAR) && mHelper.getMonth() == today.get(Calendar.MONTH)) {
			thisDay = today.get(Calendar.DAY_OF_MONTH);
		}
		// build cells
		Rect Bound = new Rect(getPaddingLeft(), getPaddingTop() + WEEK_HEIGHT, CELL_WIDTH + getPaddingLeft(), CELL_HEIGHT + WEEK_HEIGHT + getPaddingTop());
		for (int week = 0; week < mCells.length; week++) {
			for (int day = 0; day < mCells[week].length; day++) {
				int backgroundColor = 0xffDDDDDD;
				int textColor = Color.DKGRAY;
				/* Calculate the current displayed month, store into int */
				int cellMonth = tmp[week][day].thisMonth ? mHelper.getMonth() : (tmp[week][day].day > 15) ? mHelper.getMonth() - 1 : mHelper.getMonth() + 1;
				/* Check if the current cell has events on it. If so then set background color to RED */
				if (activeDaysList.contains(SchoolLoopAPI.EventMap.toIsoDate(mHelper.getYear(), cellMonth, tmp[week][day].day)))
					backgroundColor = Color.RED;
				/* Check if the current cell is today. If so then set the background color to WHITE */
				if (tmp[week][day].day == thisDay && tmp[week][day].thisMonth)
					backgroundColor = Color.WHITE;
				/* Check if the current cell is not in this month. If so then set the text color to LTGRAY */
				if (!tmp[week][day].thisMonth) {
					textColor = Color.LTGRAY;
					backgroundColor = 0xffEEEEEE;
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
		Calendar calendar = Calendar.getInstance();
		calendar.set(mHelper.getYear(), mHelper.getMonth(), 1);
		return "";// calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
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
		if (mOnCellTouchListener != null) {
			for (Cell[] week : mCells) {
				for (Cell day : week) {
					if (day.hitTest((int) event.getX(), (int) event.getY())) {
						mOnCellTouchListener.onTouch(day);
					}
				}
			}
		}
		return super.onTouchEvent(event);
	}

	public void setOnCellTouchListener(OnCellTouchListener p) {
		mOnCellTouchListener = p;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		CELL_HEIGHT = (height - WEEK_HEIGHT) / 6 - 1;
		CELL_WIDTH = width / 7;
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
	}

}
