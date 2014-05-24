package com.ampelement.cdm.calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.ampelement.cdm.R;
import com.ampelement.cdm.schoolloop.SchoolLoopEvent;

import org.joda.time.DateTime;

import java.text.DateFormatSymbols;

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
	/*
	 * @Override public void show(FragmentManager manager, String tag) {
	 * FragmentTransaction ft = manager.beginTransaction(); ft.add(this, tag);
	 * ft.commitAllowingStateLoss(); }
	 */

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
		if (Build.VERSION.SDK_INT > 11)
			f.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Dialog);
		return f;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View calendarDayScreen = inflater.inflate(R.layout.calendar_day_view, container, false);
		ListView calendarDayList = (ListView) calendarDayScreen.findViewById(R.id.calendar_day_view_listView);

		if (getArguments() != null)
			mCalendarEvents = (SchoolLoopEvent[]) getArguments().getParcelableArray("events");

		if (mCalendarEvents != null && mCalendarEvents.length > 0) {
			// Setup title
			DateTime date = mCalendarEvents[0].eventPeriod.getStart();
			String[] months = new DateFormatSymbols().getShortMonths();
			getDialog().setTitle("Events on " + months[date.getMonthOfYear() - 1] + " " + date.getDayOfMonth() + ", " + date.getYear());
			// Setup listview
			calendarDayList.setAdapter(new CalendarDayAdapter(inflater));
			calendarDayList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Object item = parent.getAdapter().getItem(position);
					if (item != null && item instanceof SchoolLoopEvent) {
						final SchoolLoopEvent event = ((SchoolLoopEvent) item);
						if (!event.location.equals("")) {
							Context context = Build.VERSION.SDK_INT >= 11 ? new ContextThemeWrapper(getSherlockActivity(), android.R.style.Theme_Holo_Dialog)
									: getSherlockActivity();
							AlertDialog.Builder adb = new AlertDialog.Builder(context);
							adb.setTitle(event.title);
							View adView = LayoutInflater.from(context).inflate(R.layout.calendar_day_map_dialog, null);
							((TextView) adView.findViewById(R.id.calendar_day_map_first)).setText("Would you like to open the address:");
							((TextView) adView.findViewById(R.id.calendar_day_map_location)).setText(event.location);
							((TextView) adView.findViewById(R.id.calendar_day_map_second)).setText("in a mapping application?");
							adb.setView(adView);
							adb.setPositiveButton("Map it", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
									String uri = "geo:0,0?q=" + event.location;
									getSherlockActivity().startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
								}
							});
							adb.setNegativeButton("Close", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							adb.create().show();
						}
					}
				}
			});
		} else
			getDialog().dismiss();

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

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = aInflater.inflate(R.layout.calendar_day_row_item, null);

			final SchoolLoopEvent event = mCalendarEvents[position];

			TextView textViewTitle = (TextView) convertView.findViewById(R.id.calendar_day_title);
			TextView textViewDesc = (TextView) convertView.findViewById(R.id.calendar_day_description);
			TextView textViewTime = (TextView) convertView.findViewById(R.id.calendar_day_time);
			View viewBlock = convertView.findViewById(R.id.calendar_day_block);
			View imageViewLocation = convertView.findViewById(R.id.calendar_day_location);

			textViewTitle.setText(event.title);
			if (event.description.equals(""))
				textViewDesc.setVisibility(View.GONE);
			else {
				textViewDesc.setVisibility(View.VISIBLE);
				textViewDesc.setText(event.description);
			}
			DateTime start = event.eventPeriod.getStart();
			DateTime end = event.eventPeriod.getEnd();
			textViewTime.setText(minutesToFriendly(start.getMinuteOfDay()) + " - " + minutesToFriendly(end.getMinuteOfDay()));
			if (event.location.equals(""))
				imageViewLocation.setVisibility(View.INVISIBLE);
			else
				imageViewLocation.setVisibility(View.VISIBLE);
			// convertView.measure(MeasureSpec.makeMeasureSpec(0,
			// MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0,
			// MeasureSpec.UNSPECIFIED));
			// viewBlock.getLayoutParams().height = (int)
			// (convertView.getMeasuredHeight() *
			// (event.eventPeriod.toDurationMillis() / 86400000f));

			if (Build.VERSION.SDK_INT >= 11) {
				convertView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
					@Override
					public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
						v.findViewById(R.id.calendar_day_block).getLayoutParams().height = (int) ((bottom - top - 20) * (event.eventPeriod.toDurationMillis() / 86400000f));
					}
				});
			}

			return convertView;
		}
	}

	private static String minutesToFriendly(int minutesInDay) {
		// Setup hours
		int hours = minutesInDay / 60;
		// Get AM/PM
		boolean isPM = hours > 11;
		// Convert to 12 hr time
		hours = hours % 12;

		// Setup minutes
		int minutes = minutesInDay % 60;

		// Build friendly string
		return hours + ":" + pad(minutes) + " " + (isPM ? "PM" : "AM");
	}

	public static String pad(int time) {
		if (time < 10)
			return "0" + Integer.toString(time);
		else
			return Integer.toString(time);
	}
}
