package com.ampelement.cdm.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.ampelement.cdm.R;
import com.ampelement.cdm.utils.SchoolLoopEvent;

public class CalendarDayFragment extends SherlockDialogFragment {

	public static final String TAG = "InstagramListFragment";

	SchoolLoopEvent[] mCalendarEvents;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.DialogFragment#show(android.support.v4.app.
	 * FragmentManager, java.lang.String)
	 * 
	 * Overrode due to an exception being thrown
	 */
	/*@Override
	public void show(FragmentManager manager, String tag) {
		FragmentTransaction ft = manager.beginTransaction();
		ft.add(this, tag);
		ft.commitAllowingStateLoss();
	}*/

	public CalendarDayFragment() {

	}

	/**
	 * Creates a new instance of CalendarDayFragment with the supplied
	 * {@link SchoolLoopEvent}[] passed in a bundle
	 * 
	 * @param events
	 *            - The {@link SchoolLoopEvent}[] to bundle and pass to the new
	 *            CalendarDayFragment
	 * @return new CalendarDayFragment
	 */
	public static CalendarDayFragment newInstance(SchoolLoopEvent[] events) {
		CalendarDayFragment f = new CalendarDayFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelableArray("events", events);
		f.setArguments(bundle);
		return f;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View calendarDayScreen = inflater.inflate(R.layout.calendar_day_view,
				container, false);
		ListView calendarDayList = (ListView) calendarDayScreen
				.findViewById(R.id.calendar_day_view_listView);

		if (getArguments() != null)
			mCalendarEvents = (SchoolLoopEvent[]) getArguments()
					.getParcelableArray("events");

		if (mCalendarEvents != null)
			calendarDayList.setAdapter(new CalendarDayAdapter(inflater));

		return calendarDayScreen;
	}

	private class CalendarDayAdapter extends BaseAdapter {

		LayoutInflater aInflater;

		public CalendarDayAdapter(LayoutInflater inflater) {
			aInflater = inflater;
		}

		@Override
		public int getCount() {
			return mCalendarEvents.length;
		}

		@Override
		public Object getItem(int position) {
			return mCalendarEvents[position];
		}

		@Override
		public long getItemId(int position) {
			return mCalendarEvents[position].hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = aInflater.inflate(R.layout.calendar_day_row_item,
						null);

			TextView textViewTitle = (TextView) convertView
					.findViewById(R.id.calendar_day_textView1);
			TextView textViewDesc = (TextView) convertView
					.findViewById(R.id.calendar_day_textView2);
			TextView textViewTime = (TextView) convertView
					.findViewById(R.id.calendar_day_textView3);

			textViewTitle.setText(mCalendarEvents[position].title);
			textViewDesc.setText(mCalendarEvents[position].description);
			textViewTime.setText(mCalendarEvents[position].eventPeriod
					.toString());

			return convertView;
		}

	}

}
