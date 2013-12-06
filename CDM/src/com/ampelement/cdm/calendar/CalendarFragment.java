package com.ampelement.cdm.calendar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;
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
import com.ampelement.cdm.utils.android.TitledSherlockFragment;

public class CalendarFragment extends TitledSherlockFragment {

	private RelativeLayout mEventLoadingScreen;
	private RelativeLayout mEventErrorLoadingScreen;
	private CalendarView mCalendarView;
	private TextView mMonthTextView;
	private TextView mYearTextView;

	private GetEventsTask mGetEventsTask;
	private SharedPreferences mSharedPref;
	private SchoolLoopEventMap mEventsMap;

	public static final String TAG = "CalendarFragment";

	@Override
	public String getTitle() {
		return "Calendar";
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mSharedPref = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
		String cachedXMLData = mSharedPref.getString(Preferences.CALENDAR_CACHED_DATA, null);

		mGetEventsTask = new GetEventsTask(mSharedPref, cachedXMLData, new OnUpdateComplete() {

			@Override
			public void onComplete(SchoolLoopEventMap eventsMap) {
				if (eventsMap != null) {
					mEventsMap = eventsMap;
					mCalendarView.setCalendarEvents(mEventsMap);
				}
			}
		});
		mGetEventsTask.execute();

		if (cachedXMLData != null)
			mEventsMap = new EventFetcher().loadEvents(cachedXMLData);
	}

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
		} else {// If no data exists
			if (mGetEventsTask.isFinished()) // and the update task finished
				// then show an error loading data screen
				mEventErrorLoadingScreen.setVisibility(View.VISIBLE);
			else
				// otherwise show a loading screen
				mEventLoadingScreen.setVisibility(View.VISIBLE);
		}

		return eventScreen;
	}
	
	@Override
	public void onDestroy() {
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
		FragmentManager fm = getSherlockActivity().getSupportFragmentManager();
		CalendarDayFragment calendarDayDialogFragment = CalendarDayFragment.newInstance(events);
		calendarDayDialogFragment.show(fm, "calendar_day_fragment");
	}

}
