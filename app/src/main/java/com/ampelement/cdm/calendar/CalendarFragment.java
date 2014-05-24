package com.ampelement.cdm.calendar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ampelement.cdm.Preferences;
import com.ampelement.cdm.R;
import com.ampelement.cdm.calendar.GetEventsTask.OnUpdateComplete;
import com.ampelement.cdm.calendar.library.CalendarView;
import com.ampelement.cdm.calendar.library.CalendarView.OnCellTouchListener;
import com.ampelement.cdm.calendar.library.CalendarView.OnMonthChangeListener;
import com.ampelement.cdm.schoolloop.SchoolLoopAPI.EventFetcher;
import com.ampelement.cdm.schoolloop.SchoolLoopEvent;
import com.ampelement.cdm.schoolloop.SchoolLoopEventMap;
import com.ampelement.cdm.utils.android.ExtendedFragment;
import com.ampelement.cdm.utils.android.NavDrawerEntry;

public class CalendarFragment extends ExtendedFragment {

	public static final String TAG = "CalendarFragment";

	@Override
	public String getFragmentTag() {
		return CalendarFragment.class.getSimpleName();
	}

	public static class Entry extends NavDrawerEntry {

		@Override
		public String getTitle() {
			return "Calendar";
		}

		@Override
		public EntryType getType() {
			return EntryType.FRAGMENT;
		}

		@Override
		public EntryStyle getStyle() {
			return EntryStyle.NORMAL;
		}

		@Override
		public int getIcon() {
			return 0;
		}

		@Override
		public Class<? extends ExtendedFragment> getFragmentType() {
			return CalendarFragment.class;
		}

		@Override
		public void runAction(Activity activity) {
			// None
		}
	}

	private RelativeLayout mEventLoadingScreen;
	private RelativeLayout mEventErrorLoadingScreen;
	private CalendarView mCalendarView;
	private TextView mMonthTextView;
	private TextView mYearTextView;

	private GetEventsTask mGetEventsTask;
	private SharedPreferences mSharedPref;
	private SchoolLoopEventMap mEventsMap;

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

		// If data exists then show it
		if (mEventsMap != null) {
			mCalendarView.setCalendarEvents(mEventsMap);
			mCalendarView.setVisibility(View.VISIBLE);
		} else {
			// otherwise show a loading screen
			mEventLoadingScreen.setVisibility(View.VISIBLE);
		}

		runAsyncTasks();

		return eventScreen;
	}

	void runAsyncTasks() {
		mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		final String cachedXMLData = mSharedPref.getString(Preferences.CALENDAR_CACHED_DATA, null);

		if (mGetEventsTask == null) {
			mGetEventsTask = new GetEventsTask(mSharedPref, cachedXMLData, new OnUpdateComplete() {

				@Override
				public void onComplete(SchoolLoopEventMap eventsMap) {
					if (eventsMap != null) {
						mEventsMap = eventsMap;
						mCalendarView.setCalendarEvents(mEventsMap);
					} else {
						mEventLoadingScreen.setVisibility(View.GONE);
						mEventErrorLoadingScreen.setVisibility(View.VISIBLE);
					}
				}
			});
			mGetEventsTask.execute();

			if (cachedXMLData != null) {
				new AsyncTask<Void, Void, SchoolLoopEventMap>() {

					@Override
					protected SchoolLoopEventMap doInBackground(Void... params) {
						return new EventFetcher().loadEvents(cachedXMLData);
					}

					@Override
					protected void onPostExecute(SchoolLoopEventMap result) {
						if (result != null) {
							mEventsMap = result;
							mCalendarView.setCalendarEvents(mEventsMap);
						}
					}
				}.execute();
			}
		}
	}

	@Override
	public void onDestroy() {
		if (mGetEventsTask != null)
			mGetEventsTask.setOnUpdateCompleteListener(null);

		super.onDestroy();
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
		FragmentManager fm = getActivity().getSupportFragmentManager();
		CalendarDayFragment calendarDayDialogFragment = CalendarDayFragment.newInstance(events);
		calendarDayDialogFragment.show(fm, "calendar_day_fragment");
	}

}
