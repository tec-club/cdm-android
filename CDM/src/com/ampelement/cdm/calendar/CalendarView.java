package com.ampelement.cdm.calendar;

import java.text.DateFormatSymbols;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.MutableDateTime;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import android.widget.SlidingDrawer;

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

	private GestureDetector mGestureDetector;
	private Scroller mScroller;

	private Rect mCellBound;
	private Paint mTextPaint;
	private Paint mBGPaint;
	private TextPaint mCellTextPaint;
	private Paint mCellBGPaint;
	private Paint mCellBorderPaint;
	private Paint mCellEventPaint;
	private Rect mMonthBound;
	private Rect mWeekBound;
	private float mTouchY;
	private float mAdjustedTouchY;
	private float mLastFlingY;
	private float mScrollDistanceY;
	private boolean isBeingTouched;
	private boolean isDragged;
	private float mVerticalOffset;

	String[] mWeekTitles = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

	int selectedCellDay;
	int selectedCellMonth;
	int selectedCellYear;

	private CalendarEvents mCalendarEvents;

	public interface OnCellTouchListener {
		public void onTouch(int year, int month, int day);
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
		String monthName = new DateFormatSymbols().getMonths()[mCalendarHelper
				.getMonth() - 1]; // Adjusts by one because JodaTime has January
									// as 1 while Java.Lang has January as 0
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
		mCalendarHelper = new CalendarHelper(DateTimeConstants.SUNDAY);
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
		// Setup gesture helper
		mGestureDetector = new GestureDetector(context, mOnGestureListener);
		mScroller = new Scroller(context);

		mRightNow = new MutableDateTime();
		// prepare static vars
		Resources res = getResources();

		CELL_TEXT_SIZE = res.getDimension(R.dimen.cell_text_size);
		WEEK_HEIGHT = (int) (CELL_TEXT_SIZE * 1.3);

		mCalendarHelper = new CalendarHelper(DateTimeConstants.SUNDAY);

		mTextPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextSize(CELL_TEXT_SIZE/* 26f */);
		mTextPaint.setColor(Color.GRAY);
		mMonthBound = new Rect(getPaddingLeft(), getPaddingTop(),
				(CELL_WIDTH + 1) * 7, WEEK_HEIGHT);
		mWeekBound = new Rect(getPaddingLeft(), getPaddingTop() + WEEK_HEIGHT,
				CELL_WIDTH + 1, WEEK_HEIGHT * 2);
		mBGPaint = new Paint();
		mBGPaint.setColor(0xffEEEEEE);
		mBGPaint.setStyle(Paint.Style.FILL);
		mCellBound = new Rect(getPaddingLeft(), (int) (getPaddingTop()
				+ WEEK_HEIGHT * 2 - CELL_HEIGHT),
				CELL_WIDTH + getPaddingLeft(),
				(int) (WEEK_HEIGHT * 2 + getPaddingTop()));

		mCellTextPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG
				| TextPaint.SUBPIXEL_TEXT_FLAG);
		mCellTextPaint.setTextSize(CELL_TEXT_SIZE/* 26f */);
		mCellBGPaint = new Paint();
		mCellBGPaint.setStyle(Paint.Style.FILL);
		mCellBorderPaint = new Paint();
		mCellBorderPaint.setAntiAlias(true);
		mCellBorderPaint.setStyle(Paint.Style.STROKE);
		mCellEventPaint = new Paint();
		mCellEventPaint.setStyle(Paint.Style.FILL);
	}

	SimpleOnGestureListener mOnGestureListener = new SimpleOnGestureListener() {
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		};

		public boolean onSingleTapUp(MotionEvent e) {
			try {
				Day selectedDay = mDays[(int) ((e.getY() - mWeekBound.bottom - mVerticalOffset) / CELL_HEIGHT) + 1][(int) (e
						.getX() / CELL_WIDTH)];
//				selectedCellDay = selectedDay.day;
//				selectedCellMonth = selectedDay.month;
//				selectedCellYear = selectedDay.year;
				if (mOnCellTouchListener != null)
					mOnCellTouchListener.onTouch(selectedDay.year,
							selectedDay.month, selectedDay.day);
			} catch (IndexOutOfBoundsException eIndex) {
				return false;
			}
			return true;
		};

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			mScrollDistanceY += -distanceY;
			scrollY(mScrollDistanceY);
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.d(TAG, "fling");
			// Before flinging, aborts the current animation.
			mScroller.forceFinished(true);
			// Begins the animation
			mScroller.fling(
			// Current scroll position
					0, 0, 0, (int) velocityY / 2,
					/*
					 * Minimum and maximum scroll positions. The minimum scroll
					 * position is generally zero and the maximum scroll
					 * position is generally the content size less the screen
					 * size. So if the content width is 1000 pixels and the
					 * screen width is 200 pixels, the maximum scroll offset
					 * should be 800 pixels.
					 */
					0, 0, -CELL_HEIGHT * 8, CELL_HEIGHT * 8);
			// mAdjustedTouchY = mScroller.getStartY() - mVerticalOffset;
			mLastFlingY = mScroller.getStartY();
			// mLastFlingY = mTouchY + mVerticalOffset;
			// Invalidates to trigger computeScroll()
			ViewCompat.postInvalidateOnAnimation(CalendarView.this);
			return false;
		};
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Day selectedDay = mDays[(int) ((event.getY() - mWeekBound.bottom - mVerticalOffset) / CELL_HEIGHT) + 1][(int) (event
					.getX() / CELL_WIDTH)];
			selectedCellDay = selectedDay.day;
			selectedCellMonth = selectedDay.month;
			selectedCellYear = selectedDay.year;
			this.invalidate();
		case MotionEvent.ACTION_UP:
			selectedCellDay = -1;
			selectedCellMonth = -1;
			selectedCellYear = -1;
			if (!isDragged) {
				/*
				 * if (mOnCellTouchListener != null) { for (Cell[] week :
				 * mCells) { for (Cell day : week) { if (day.hitTest((int)
				 * event.getX(), (int) event.getY())) {
				 * mOnCellTouchListener.onTouch(day); break; } } } }
				 */
			} else {
				mCalendarHelper.setDisplayMonthCurrent();
			}
			isDragged = false;
			mScrollDistanceY = mVerticalOffset;

			this.invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			mScrollDistanceY = mVerticalOffset;
			mCalendarHelper.setDisplayMonthCurrent();
			this.invalidate();
		}
		return true;
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			float currentTouchY = mScroller.getCurrY();
			mScrollDistanceY += currentTouchY - mLastFlingY;
			mLastFlingY = currentTouchY;
			scrollY(mScrollDistanceY);
		}
	}

	public void scrollY(float distanceY) {
		mVerticalOffset = distanceY % CELL_HEIGHT;
		if (Math.abs(distanceY / CELL_HEIGHT) >= 1) {
			mCalendarHelper.addWeek(distanceY > 0 ? -1 : 1);
			mScrollDistanceY = mVerticalOffset;
			loadWeeks();
			calculateBounds();
			// this.requestLayout();
		} else
			calculateBounds();
		this.invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		CELL_HEIGHT = (height - WEEK_HEIGHT * 2) / 6 - 1;
		CELL_WIDTH = width / 7;
		mMonthBound.set(getPaddingLeft(), getPaddingTop(),
				(CELL_WIDTH + 1) * 7, WEEK_HEIGHT);
		mWeekBound.set(getPaddingLeft(), getPaddingTop() + WEEK_HEIGHT,
				CELL_WIDTH + 1, WEEK_HEIGHT * 2);
		setMeasuredDimension(width, height);
	}

	@Override
	public void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		loadWeeks();
		calculateBounds();
		super.onLayout(changed, left, top, right, bottom);
	}

	private void loadWeeks() {
		for (int i = 0; i < mDays.length; i++) {
			mDays[i] = mCalendarHelper.getWeekByOffset((i + 1) - 4);
		}
	}

	private void calculateBounds() {
		mCellBound.set(getPaddingLeft(), (int) (getPaddingTop() + WEEK_HEIGHT
				* 2 + mVerticalOffset - CELL_HEIGHT), CELL_WIDTH
				+ getPaddingLeft(),
				(int) (WEEK_HEIGHT * 2 + getPaddingTop() + mVerticalOffset));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// draw background
		super.onDraw(canvas);

		int thisDay = mRightNow.getDayOfMonth();
		int thisMonth = mRightNow.getMonthOfYear();
		int thisYear = mRightNow.getYear();

		int backgroundColor = 0xffEEEEEE;
		int borderColor = 0xffd4d4d4;
		final int columbiaBlue = 0xff9BDDFF;
		int textColor = 0xff888888;
		int eventColor = columbiaBlue;
		boolean underlineText = false;
		boolean boldText = false;

		for (Day[] week : mDays) {
			for (Day day : week) {
				backgroundColor = 0xffEEEEEE;
				borderColor = 0xffd4d4d4;
				textColor = 0xff888888;
				eventColor = columbiaBlue;
				underlineText = false;
				boldText = false;
				/*
				 * Check if the current cell is in this month. If so then set
				 * the text color to DKGRAY
				 */
				if (day.withinCurrentMonth)
					textColor = Color.DKGRAY;
				if (day.month % 2 == 0) {
					textColor = Color.DKGRAY;
					backgroundColor = 0xffDDDDDD;
				}
				/*
				 * Check if the current cell is today. If so then set the
				 * background color to WHITE
				 */
				if (day.day == thisDay && day.month == thisMonth
						&& day.year == thisYear)
					boldText = true;

				if (day.day == selectedCellDay
						&& day.month == selectedCellMonth
						&& day.year == selectedCellYear)
					backgroundColor = 0xffFCFCFC;

				// Draw cell
				// Setup for drawing
				mCellTextPaint.setColor(textColor);
				mCellTextPaint.setFakeBoldText(boldText);
				mCellTextPaint.setUnderlineText(underlineText);

				mCellBGPaint.setColor(backgroundColor);
				mCellBorderPaint.setColor(borderColor);

				mCellEventPaint.setColor(columbiaBlue);

				int charWidth = (int) mCellTextPaint.measureText("7");
				int dayWidth = (int) mCellTextPaint.measureText(String
						.valueOf(day.day));
				int dayHeight = (int) (-mCellTextPaint.ascent() + mCellTextPaint
						.descent());

				// Actual draw calls
				canvas.drawRect(mCellBound, mCellBGPaint);
				canvas.drawRect(mCellBound, mCellBorderPaint);

				CalendarEvent[] events = mCalendarEvents
						.getEvents(SchoolLoopEventMap.toIsoDate(day.year,
								day.month - 1, day.day));
				if (events != null) {
					underlineText = true;
					for (int i = 0; i < events.length; i++) {
						float left = mCellBound.left + CELL_WIDTH / 5;
						float right = left + CELL_WIDTH / 5;
						float top = mCellBound.top
								+ 1
								+ ((float) events[i].getStartMinute() / 1440.0f)
								* CELL_HEIGHT;
						float bottom = mCellBound.top + 1
								+ ((float) events[i].getEndMinute() / 1440.0f)
								* CELL_HEIGHT;
						if (bottom - top < 2)
							bottom = top + 10;
						if (bottom - top > CELL_HEIGHT - 4) {
							top = top + 2;
							bottom = top + CELL_HEIGHT - 6;
						}
						canvas.drawRect(left, top, right, bottom,
								mCellEventPaint);
					}
				}

				canvas.drawText(String.valueOf(day.day), mCellBound.right
						- (dayWidth + charWidth), mCellBound.top + dayHeight,
						mCellTextPaint);

				mCellBound.offset(CELL_WIDTH + 0, 0); // move to next column
			}
			mCellBound.offset(0, CELL_HEIGHT + 0); // move to next row and first
													// column
			mCellBound.left = getPaddingLeft();
			mCellBound.right = getPaddingLeft() + CELL_WIDTH;
		}

		calculateBounds();

		canvas.drawRect(mMonthBound, mBGPaint);
		int monthX = (int) mTextPaint.measureText(getMonthString() + " - "
				+ String.valueOf(mCalendarHelper.getYear()));
		int monthY = (int) (-mTextPaint.ascent() + mTextPaint.descent());
		canvas.drawText(
				getMonthString() + " - "
						+ String.valueOf(mCalendarHelper.getYear()),
				mMonthBound.centerX() - monthX / 2, mMonthBound.top
						+ (WEEK_HEIGHT - (monthY / 4)), mTextPaint);

		for (String weekTitle : mWeekTitles) {
			canvas.drawRect(mWeekBound, mBGPaint);
			int dx = (int) mTextPaint.measureText(weekTitle);
			int dy = (int) (-mTextPaint.ascent() + mTextPaint.descent());
			canvas.drawText(weekTitle, mWeekBound.right - (dx + 7),
					mWeekBound.top + (WEEK_HEIGHT - (dy / 4)), mTextPaint);
			mWeekBound.offset(CELL_WIDTH + 1, 0);
		}
		mWeekBound.offset(-(CELL_WIDTH + 1) * 7, 0);
	}
}
