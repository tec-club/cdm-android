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
import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.MutableDateTime;

import android.content.Context;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.VelocityTrackerCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import com.ampelement.cdm.R;
import com.ampelement.cdm.calendar.CalendarHelper.Day;
import com.ampelement.cdm.utils.SchoolLoopEventMap;

public class CalendarView extends View {
	private static int WEEK_HEIGHT = 0;
	private static int CELL_WIDTH = 58;
	private static int CELL_HEIGHT = 53;
	private static float CELL_TEXT_SIZE;

	private static final String TAG = "CalendarView";
	private MutableDateTime mRightNow = null;
	private Day[][] mDays = new Day[8][7];
	private OnCellTouchListener mOnCellTouchListener = null;
	private CalendarHelper mCalendarHelper;

	private VelocityTracker mVelocityTracker = null;

	private Rect mCellBound;
	private Paint mTextPaint;
	private Paint mBGPaint;
	private TextPaint mCellTextPaint;
	private Paint mCellBGPaint;
	private Paint mCellBorderPaint;
	private Rect mMonthBound;
	private Rect mWeekBound;
	private float mTouchY;
	private float mAdjustedTouchY;
	private boolean isBeingTouched;
	private boolean isDragged;
	private float mVerticalOffset;

	String[] mWeekTitles = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

	int selectedCellDay;
	int selectedCellMonth;

	private CalendarEvents mCalendarEvents;

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
		initCalendarView(context);
	}

	public void setCalendarEvents(CalendarEvents calendarEvents) {
		this.mCalendarEvents = calendarEvents;
		this.invalidate();
	}

	public void setSelectedDate(int day, int month) {
		selectedCellDay = day;
		selectedCellMonth = month;
		this.invalidate();
	}

	public void setDate(long milliseconds) {
		mRightNow.setDate(milliseconds);
		this.requestLayout();
		this.invalidate();
	}

	public int getYear() {
		return mCalendarHelper.getYear();
	}

	public int getMonth() {
		return mCalendarHelper.getMonth();
	}

	public String getMonthString() {
		String monthName = new DateFormatSymbols().getMonths()[mCalendarHelper.getMonth()];
		return monthName;
	}

	public void nextMonth() {
		mCalendarHelper.nextMonth();
		this.requestLayout();
		this.invalidate();
	}

	public void previousMonth() {
		mCalendarHelper.previousMonth();
		this.requestLayout();
		this.invalidate();
	}

	public void goToday() {
		mCalendarHelper = new CalendarHelper(Calendar.SUNDAY);
		this.requestLayout();
		this.invalidate();
	}

	public DateTime getDateTime() {
		return mRightNow.toDateTime();
	}

	public MutableDateTime getMutableDateTime() {
		return mRightNow.copy();
	}

	public void setOnCellTouchListener(OnCellTouchListener p) {
		mOnCellTouchListener = p;
	}

	private void initCalendarView(Context context) {

		mRightNow = new MutableDateTime();
		// prepare static vars
		Resources res = getResources();

		CELL_TEXT_SIZE = res.getDimension(R.dimen.cell_text_size);
		WEEK_HEIGHT = (int) (CELL_TEXT_SIZE * 1.3);

		mCalendarHelper = new CalendarHelper(DateTimeConstants.SUNDAY);

		mTextPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextSize(CELL_TEXT_SIZE/* 26f */);
		mTextPaint.setColor(Color.GRAY);
		mMonthBound = new Rect(getPaddingLeft(), getPaddingTop(), (CELL_WIDTH + 1) * 7, WEEK_HEIGHT);
		mWeekBound = new Rect(getPaddingLeft(), getPaddingTop() + WEEK_HEIGHT, CELL_WIDTH + 1, WEEK_HEIGHT * 2);
		mBGPaint = new Paint();
		mBGPaint.setColor(0xffEEEEEE);
		mBGPaint.setStyle(Paint.Style.FILL);
		mCellBound = new Rect(getPaddingLeft(), (int) (getPaddingTop() + WEEK_HEIGHT * 2 - CELL_HEIGHT), CELL_WIDTH + getPaddingLeft(), (int) (WEEK_HEIGHT * 2 + getPaddingTop()));

		mCellTextPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG | TextPaint.SUBPIXEL_TEXT_FLAG);
		mCellBGPaint = new Paint();
		mCellBGPaint.setStyle(Paint.Style.FILL);
		mCellBorderPaint = new Paint();
		mCellBorderPaint.setStyle(Paint.Style.STROKE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// Initiate velocity tracking
			if (mVelocityTracker == null) {
				// Retrieve a new VelocityTracker object to watch the velocity of a motion.
				mVelocityTracker = VelocityTracker.obtain();
			} else {
				// Reset the velocity tracker back to its initial state.
				mVelocityTracker.clear();
			}
			// Add a user's movement to the tracker.
			mVelocityTracker.addMovement(event);

			// Non gesture stuff
			isBeingTouched = true;
			mTouchY = event.getRawY();
			mAdjustedTouchY = mTouchY - mVerticalOffset;
			break;
		case MotionEvent.ACTION_MOVE:
			mVelocityTracker.addMovement(event);
			// When you want to determine the velocity, call 
			// computeCurrentVelocity(). Then call getXVelocity() 
			// and getYVelocity() to retrieve the velocity for each pointer ID. 
			mVelocityTracker.computeCurrentVelocity(1000);
			// Log velocity of pixels per second
			// Best practice to use VelocityTrackerCompat where possible.
			Log.d("", "X velocity: " + VelocityTrackerCompat.getXVelocity(mVelocityTracker, event.getPointerId(event.getActionIndex())));
			Log.d("", "Y velocity: " + VelocityTrackerCompat.getYVelocity(mVelocityTracker, event.getPointerId(event.getActionIndex())));

			// Non-gesture stuff
			float currentTouchY = event.getRawY();
			float differenceSinceAdjustedTouch = currentTouchY - mAdjustedTouchY;

			if (isDragged || Math.abs(differenceSinceAdjustedTouch) > 5)
				isDragged = true;

			mVerticalOffset = differenceSinceAdjustedTouch % CELL_HEIGHT;
			if (Math.abs(differenceSinceAdjustedTouch / CELL_HEIGHT) >= 1) {
				mAdjustedTouchY = currentTouchY;
				mCalendarHelper.addWeek(differenceSinceAdjustedTouch > 0 ? -1 : 1);
				this.requestLayout();
			} else
				calculateBounds();
			this.invalidate();
			break;
		case MotionEvent.ACTION_UP:
			if (!isDragged) {
				/*if (mOnCellTouchListener != null) {
					for (Cell[] week : mCells) {
						for (Cell day : week) {
							if (day.hitTest((int) event.getX(), (int) event.getY())) {
								mOnCellTouchListener.onTouch(day);
								break;
							}
						}
					}
				}*/
			} else {
				mCalendarHelper.setDisplayMonthCurrent();
			}
			isBeingTouched = false;
			isDragged = false;

			this.requestLayout();
			this.invalidate();
            // Return a VelocityTracker object back to be re-used by others.
            mVelocityTracker.recycle();
			break;
		case MotionEvent.ACTION_CANCEL:
            // Return a VelocityTracker object back to be re-used by others.
            mVelocityTracker.recycle();
			break;
		}
		return true;
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
		loadWeeks();
		calculateBounds();
		super.onLayout(changed, left, top, right, bottom);
	}

	private void loadWeeks() {
		for (int i = 0; i < mDays.length; i++) {
			mDays[i] = mCalendarHelper.getWeek((i + 1) - 4);
		}
	}

	private void calculateBounds() {
		mCellBound.set(getPaddingLeft(), (int) (getPaddingTop() + WEEK_HEIGHT * 2 + mVerticalOffset - CELL_HEIGHT), CELL_WIDTH + getPaddingLeft(), (int) (WEEK_HEIGHT * 2 + getPaddingTop() + mVerticalOffset));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// draw background
		super.onDraw(canvas);

		int thisDay = 0;
		if (mCalendarHelper.getYear() == mRightNow.getYear() && mCalendarHelper.getMonth() == mRightNow.getMonthOfYear()) {
			thisDay = mRightNow.getDayOfMonth();
		}

		for (Day[] week : mDays) {
			for (Day day : week) {
				int backgroundColor = 0xffEEEEEE;
				int columbiaBlue = 0xff9BDDFF;
				int textColor = Color.LTGRAY;
				/* Check if the current cell is in this month. If so then set the text color to DKGRAY */
				if (day.withinCurrentMonth) {
					textColor = Color.DKGRAY;
					backgroundColor = 0xffDDDDDD;
				}
				/* Check if the current cell has events on it. If so then set background color to Columbia Blue */
				if (mCalendarEvents.contains(SchoolLoopEventMap.toIsoDate(mCalendarHelper.getYear(), day.month, day.day)))
					backgroundColor = columbiaBlue;
				/* Check if the current cell is today. If so then set the background color to WHITE */
				if (day.day == thisDay && day.month == mRightNow.getMonthOfYear())
					backgroundColor = Color.WHITE;

				if (day.day == selectedCellDay && day.month == selectedCellMonth)
					backgroundColor = Color.RED;

				// Draw cell
				// Setup for drawing
				mCellTextPaint.setTextSize(CELL_TEXT_SIZE/* 26f */);
				mCellTextPaint.setColor(textColor);
				mCellTextPaint.setFakeBoldText(day.withinCurrentMonth);

				mCellBGPaint.setColor(backgroundColor);
				mCellBGPaint.setStyle(Paint.Style.FILL);
				mCellBorderPaint.setColor(0xffd4d4d4);
				mCellBorderPaint.setStyle(Paint.Style.STROKE);

				int charWidth = (int) mCellTextPaint.measureText("7");
				int dayWidth = (int) mCellTextPaint.measureText(String.valueOf(day.day));
				int dayHeight = (int) (-mCellTextPaint.ascent() + mCellTextPaint.descent());

				// Actual draw calls
				canvas.drawRect(mCellBound, mCellBGPaint);
				canvas.drawRect(mCellBound, mCellBorderPaint);
				canvas.drawText(String.valueOf(day.day), mCellBound.right - (dayWidth + charWidth), mCellBound.top + dayHeight, mCellTextPaint);

				mCellBound.offset(CELL_WIDTH + 1, 0); // move to next column
			}
			mCellBound.offset(0, CELL_HEIGHT + 1); // move to next row and first column
			mCellBound.left = getPaddingLeft();
			mCellBound.right = getPaddingLeft() + CELL_WIDTH;
		}

		canvas.drawRect(mMonthBound, mBGPaint);
		int monthX = (int) mTextPaint.measureText(getMonthString() + " - " + String.valueOf(mCalendarHelper.getYear()));
		int monthY = (int) (-mTextPaint.ascent() + mTextPaint.descent());
		canvas.drawText(getMonthString() + " - " + String.valueOf(mCalendarHelper.getYear()), mMonthBound.centerX() - monthX / 2, mMonthBound.top + (WEEK_HEIGHT - (monthY / 4)), mTextPaint);

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
