package com.ampelement.cdm.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.ampelement.cdm.R;
import com.ampelement.cdm.calendar.CalendarView;
import com.ampelement.cdm.calendar.CalendarView.OnCellTouchListener;
import com.ampelement.cdm.calendar.CalendarView.OnMonthChangeListener;
import com.ampelement.cdm.utils.SchoolLoopAPI.EventFetcher;
import com.ampelement.cdm.utils.SchoolLoopEvent;
import com.ampelement.cdm.utils.SchoolLoopEventMap;

public class CalendarFragment extends SherlockFragment {

	private RelativeLayout mEventLoadingScreen;
	private RelativeLayout mEventErrorLoadingScreen;
	private CalendarView mCalendarView;
	private TextView mMonthTextView;
	private TextView mYearTextView;

	private SchoolLoopEventMap mEventsMap;

	public static final String TAG = "CalendarFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View eventScreen = inflater.inflate(R.layout.event_screen, container, false);
		mEventLoadingScreen = (RelativeLayout) eventScreen.findViewById(R.id.event_screen_loading);
		mEventErrorLoadingScreen = (RelativeLayout) eventScreen.findViewById(R.id.event_screen_error_loading);
		mMonthTextView = (TextView) eventScreen.findViewById(R.id.event_screen_month);
		mYearTextView = (TextView) eventScreen.findViewById(R.id.event_screen_year);

		mCalendarView = (CalendarView) eventScreen.findViewById(R.id.event_screen_calendar);
		mCalendarView.setOnCellTouchListener(mOnCellTouchListener);
		mCalendarView.setOnMonthChangeListener(mOnMonthChangeListener);
		mCalendarView.setShowMonth(false);

		new GetEventsTask().execute();
		return eventScreen;
	}

	private OnCellTouchListener mOnCellTouchListener = new OnCellTouchListener() {
		@Override
		public void onTouch(int year, int month, int day) {
			SchoolLoopEvent[] daysEvents = mEventsMap.getEvents(SchoolLoopEventMap.toIsoDate(year, month - 1, day));
			if (daysEvents != null && daysEvents.length > 0)
				showDaysEventDialog(daysEvents);
		}
	};

	private OnMonthChangeListener mOnMonthChangeListener = new OnMonthChangeListener() {
		@Override
		public void onChange(int year, int month) {
			mMonthTextView.setText(mCalendarView.getMonthStringShort());
			mYearTextView.setText(Integer.toString(mCalendarView.getYear()));
		}
	};

	private void showDaysEventDialog(SchoolLoopEvent[] events) {
		FragmentManager fm = getSherlockActivity().getSupportFragmentManager();
		CalendarDayFragment calendarDayDialogFragment = CalendarDayFragment.newInstance(events);
		calendarDayDialogFragment.show(fm, "calendar_day_fragment");
	}

	private class GetEventsTask extends AsyncTask<Void, String, SchoolLoopEventMap> {

		@Override
		protected SchoolLoopEventMap doInBackground(Void... params) {
			EventFetcher eventFetcher = new EventFetcher();
			return eventFetcher.fetchEvents();
		}

		@Override
		protected void onPostExecute(SchoolLoopEventMap result) {
			mEventsMap = result;
			try {
				mEventLoadingScreen.setVisibility(View.GONE);
				if (mEventsMap != null && !mEventsMap.isEmpty()) {
					mCalendarView.setVisibility(View.VISIBLE);
					mCalendarView.setCalendarEvents(mEventsMap);
				} else {
					mEventErrorLoadingScreen.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
			}
		}
	}

}
