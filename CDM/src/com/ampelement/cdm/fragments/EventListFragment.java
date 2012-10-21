package com.ampelement.cdm.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ampelement.cdm.R;
import com.ampelement.cdm.utils.SchoolLoopAPI.Event;
import com.ampelement.cdm.utils.SchoolLoopAPI.EventFetcher;
import com.ampelement.cdm.utils.SchoolLoopAPI.EventMap;
import com.ampelement.cdm.calendar.CalendarView;
import com.ampelement.cdm.calendar.Cell;

public class EventListFragment extends Fragment implements CalendarView.OnCellTouchListener {

	private ListView eventListView;
	private RelativeLayout eventLoadingScreen;
	private EventInterface eventInterface;

	private EventMap eventsMap;
	private CalendarView calendarView;

	public static final String TAG = "EventListFragment";

	public interface EventInterface {
		public void setIndicator(int indicatorID);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			eventInterface = (EventInterface) activity;
		} catch (ClassCastException e) {
			Log.e(TAG, activity.toString() + " must implement OnUpdateListener");
			// getActivity().finish();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View eventScreen = inflater.inflate(R.layout.event_screen, container, false);
		eventListView = (ListView) eventScreen.findViewById(R.id.event_screen_list);
		eventLoadingScreen = (RelativeLayout) eventScreen.findViewById(R.id.event_screen_loading);
		
		calendarView = (CalendarView) eventScreen.findViewById(R.id.event_screen_calendar);
		calendarView.setOnCellTouchListener(this);

		new GetEventsTask().execute();
		eventInterface.setIndicator(R.id.main_events_indicator);
		return eventScreen;
	}

	private class GetEventsTask extends AsyncTask<Void, String, EventMap> {

		@Override
		protected EventMap doInBackground(Void... params) {
			EventFetcher eventFetcher = new EventFetcher();
			return eventFetcher.fetchEvents();
		}

		@Override
		protected void onPostExecute(EventMap result) {
			eventsMap = result;
			try {
				eventLoadingScreen.setVisibility(View.GONE);
				if (eventsMap != null && !eventsMap.isEmpty()) {
					calendarView.setVisibility(View.VISIBLE);
					calendarView.setActiveDayList(eventsMap.activeDatesArrayList);
					Date now = new Date();
					EventListAdapter adapter = new EventListAdapter(eventsMap.get(now.getYear(), now.getMonth(), now.getDate()), getActivity().getApplicationContext());
					eventListView.setAdapter(adapter);
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
		private ArrayList<Event> eventList;
		private Context CONTEXT;

		public EventListAdapter(ArrayList<Event> _eventList, Context _context) {
			this.eventList = _eventList;
			this.CONTEXT = _context;
		}

		public int getCount() {
			return eventList.size();
		}

		public Event getItem(int position) {
			return eventList.get(position);
		}

		@Deprecated
		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout itemLayout;
			final Event event = eventList.get(position);
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

	@Override
	public void onTouch(Cell cell) {
		EventListAdapter adapter = new EventListAdapter(eventsMap.get(calendarView.getYear(), cell.getMonth(Calendar.getInstance().get(Calendar.MONTH)), cell.getDayOfMonth()), getActivity().getApplicationContext());
		if (!adapter.eventList.isEmpty())
			eventListView.setAdapter(adapter);
	}

}
