package com.ampelement.cdm.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ampelement.cdm.R;
import com.ampelement.cdm.utils.SchoolLoopAPI;
import com.ampelement.cdm.utils.SchoolLoopAPI.Event;
import com.ampelement.cdm.utils.SchoolLoopAPI.EventFetcher;
import com.ampelement.cdm.utils.WebAPI;
import com.ampelement.cdm.calendar.CalendarView;
import com.ampelement.cdm.calendar.Cell;

public class EventListFragment extends Fragment implements CalendarView.OnCellTouchListener {

	private ListView eventListView;
	private RelativeLayout eventLoadingScreen;
	private EventInterface eventInterface;

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
		
		CalendarView mView = (CalendarView)eventScreen.findViewById(R.id.event_screen_calendar);
        mView.setOnCellTouchListener(this);

		new GetEventsTask().execute();
		eventInterface.setIndicator(R.id.main_events_indicator);
		return eventScreen;
	}

	private class GetEventsTask extends AsyncTask<Void, String, ArrayList<Event>> {

		@Override
		protected ArrayList<Event> doInBackground(Void... params) {
			EventFetcher eventFetcher = new EventFetcher();
			return eventFetcher.fetchEvents();
		}

		@Override
		protected void onPostExecute(ArrayList<Event> result) {
			try {
				eventLoadingScreen.setVisibility(View.GONE);
				if (result != null && result.size() > 0) {
					EventListAdapter adapter = new EventListAdapter(result, getActivity().getApplicationContext());

					eventListView.setAdapter(adapter);
					eventListView.setAnimationCacheEnabled(false);
					LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.list_view_slide_in_controller);
					eventListView.setLayoutAnimation(controller);
					// ((RelativeLayout)
					// findViewById(R.id.main_loading)).setVisibility(View.GONE);
				} else {
				}
			} catch (Exception e) {
				// TODO: handle exception
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
		Toast.makeText(getActivity(), cell.toString(), Toast.LENGTH_LONG).show();
	}

}
