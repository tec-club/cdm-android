package com.ampelement.cdm.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.ampelement.cdm.R;
import com.ampelement.cdm.calendar.CalendarView;
import com.ampelement.cdm.calendar.CalendarView.OnCellTouchListener;
import com.ampelement.cdm.calendar.Cell;
import com.ampelement.cdm.utils.SchoolLoopEvent;
import com.ampelement.cdm.utils.SchoolLoopAPI.EventFetcher;
import com.ampelement.cdm.utils.SchoolLoopEventMap;
import com.ampelement.cdm.utils.SchoolLoopEvents;

public class EventListFragment extends SherlockFragment {

	private ListView eventListView;
	private RelativeLayout eventLoadingScreen;
	private CalendarView calendarView;
	private Button mPrevCalendarButton;
	private Button mNextCalendarButton;
	private Button mMonthButton;

	private SchoolLoopEventMap eventsMap;

	public static final String TAG = "EventListFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View eventScreen = inflater.inflate(R.layout.event_screen, container, false);
		eventListView = (ListView) eventScreen.findViewById(R.id.event_screen_list);
		eventLoadingScreen = (RelativeLayout) eventScreen.findViewById(R.id.event_screen_loading);

		calendarView = (CalendarView) eventScreen.findViewById(R.id.event_screen_calendar);
		calendarView.setOnCellTouchListener(mOnCellTouchListener);

		setupCalendarButtons(eventScreen);

		new GetEventsTask().execute();
		return eventScreen;
	}

	void setupCalendarButtons(View view) {
		mPrevCalendarButton = (Button) view.findViewById(R.id.event_screen_prev_button);
		mNextCalendarButton = (Button) view.findViewById(R.id.event_screen_next_button);
		mPrevCalendarButton.setText(getResources().getString(R.string.previous));
		mNextCalendarButton.setText(getResources().getString(R.string.next));
		mPrevCalendarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				calendarView.previousMonth();
				mMonthButton.setText(calendarView.getMonthString());
			}
		});
		mNextCalendarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				calendarView.nextMonth();
				mMonthButton.setText(calendarView.getMonthString());
			}
		});
		setupMonthButton(view);
	}

	void setupMonthButton(View view) {
		mMonthButton = (Button) view.findViewById(R.id.event_screen_month_button);
		mMonthButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());
				final Map<String, int[]> displayDateMap = new HashMap<String, int[]>();
				for (String dateString : eventsMap.activeDatesArrayList) {
					try {
						SimpleDateFormat spd = new SimpleDateFormat("yyyy-MM-dd");
						Date date = new Date();
						date = spd.parse(dateString);
						SimpleDateFormat month_date = new SimpleDateFormat("MMMMMMMMM");
						String monthName = month_date.format(date);
						String displayString = monthName + " - " + String.valueOf(date.getYear() + 1900);
						if (!displayDateMap.containsKey(displayString)) {
							int[] monthYearPair = new int[2];
							monthYearPair[0] = date.getMonth();
							monthYearPair[1] = date.getYear();
							displayDateMap.put(displayString, monthYearPair);
						}
					} catch (ParseException e) {
					}
				}
				int i = 0;
				final CharSequence[] displayNames = new CharSequence[displayDateMap.size()];
				for (String key : displayDateMap.keySet()) {
					displayNames[i] = key;
					i++;
				}
				adBuilder.setItems(displayNames, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						boolean incrementUp = true;
						int monthsAway = 0;

						int[] monthYearPair = displayDateMap.get(displayNames[which]);
						int month = monthYearPair[0];
						int year = monthYearPair[1];

						monthsAway = (year + 1900 - calendarView.getYear()) * 12;
						monthsAway = monthsAway + (month - calendarView.getMonth());
						incrementUp = monthsAway < 0 ? false : true;
						int i = 0;
						while (i != monthsAway) {
							if (incrementUp)
								calendarView.nextMonth();
							else
								calendarView.previousMonth();
							i = incrementUp ? i + 1 : i - 1;
						}
						mMonthButton.setText(calendarView.getMonthString());
					}
				});
				adBuilder.show();
			}
		});
	}

	private OnCellTouchListener mOnCellTouchListener = new OnCellTouchListener() {
		@Override
		public void onTouch(Cell cell) {
			ArrayList<SchoolLoopEvent> events = eventsMap.get(calendarView.getYear(), cell.getMonth(), cell.getDayOfMonth());
			SchoolLoopEvent event = events.get(0);
			AlertDialog.Builder adBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Theme_Sherlock_Light_Dialog));
			if (event.description.matches("")) {
				adBuilder.setTitle("Event " + event.isoDate);
				adBuilder.setMessage(event.title);
			} else {
				adBuilder.setTitle(event.title);
				adBuilder.setMessage(event.description);
			}
			adBuilder.setCancelable(true);
			adBuilder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			adBuilder.create().show();
		}
	};

	private class GetEventsTask extends AsyncTask<Void, String, SchoolLoopEventMap> {

		@Override
		protected SchoolLoopEventMap doInBackground(Void... params) {
			EventFetcher eventFetcher = new EventFetcher();
			return eventFetcher.fetchEvents();
		}

		@Override
		protected void onPostExecute(SchoolLoopEventMap result) {
			eventsMap = result;
			try {
				eventLoadingScreen.setVisibility(View.GONE);
				if (eventsMap != null && !eventsMap.isEmpty()) {
					//					mPrevCalendarButton.setVisibility(View.VISIBLE);
					//					mNextCalendarButton.setVisibility(View.VISIBLE);
					calendarView.setVisibility(View.VISIBLE);
					calendarView.setCalendarEvents(new SchoolLoopEvents(eventsMap));
					Date now = new Date();
					EventListAdapter adapter = new EventListAdapter(eventsMap.get(now.getYear(), now.getMonth(), now.getDate()), getActivity().getApplicationContext());
					eventListView.setAdapter(adapter);
					mMonthButton.setText(calendarView.getMonthString());
					eventListView.setAnimationCacheEnabled(false);
					LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.list_view_slide_in_controller);
					eventListView.setLayoutAnimation(controller);
				} else {
				}
			} catch (Exception e) {
			}
		}
	}

	private class EventListAdapter extends BaseAdapter {
		private ArrayList<SchoolLoopEvent> eventList;
		private Context CONTEXT;

		public EventListAdapter(ArrayList<SchoolLoopEvent> _eventList, Context _context) {
			this.eventList = _eventList;
			this.CONTEXT = _context;
		}

		public int getCount() {
			return eventList.size();
		}

		public SchoolLoopEvent getItem(int position) {
			return eventList.get(position);
		}

		@Deprecated
		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout itemLayout;
			final SchoolLoopEvent event = eventList.get(position);
			itemLayout = (LinearLayout) LayoutInflater.from(CONTEXT).inflate(R.layout.rss_row_view, parent, false);

			TextView tvTitle = (TextView) itemLayout.findViewById(R.id.rss_row_view_title);
			TextView tvDate = (TextView) itemLayout.findViewById(R.id.rss_row_view_date);
			TextView tvTime = (TextView) itemLayout.findViewById(R.id.rss_row_view_time);

			tvTitle.setText(event.title);
			tvDate.setText(event.isoDate);
			tvTime.setText(event.startTime);

			return itemLayout;
		}
	}

	/*@Override
	public void onTouch(Cell cell) {
		EventListAdapter adapter = new EventListAdapter(eventsMap.get(calendarView.getYear(), cell.getMonth(), cell.getDayOfMonth()), getActivity().getApplicationContext());
		if (!adapter.eventList.isEmpty()) {
			eventListView.setAdapter(adapter);
			calendarView.setSelectedDate(cell.getDayOfMonth(), cell.getMonth());
		}
	}*/

}
