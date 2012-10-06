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

import com.ampelement.cdm.R;
import com.ampelement.cdm.utils.SchoolLoopAPI;
import com.ampelement.cdm.utils.SchoolLoopAPI.Event;
import com.ampelement.cdm.utils.SchoolLoopAPI.EventFetcher;
import com.ampelement.cdm.utils.WebAPI;

public class EventListFragment extends Fragment {

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

		eventListView.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (visibleItemCount > 0) {
					// TODO Auto-generated method stub
					ImageView upArrow = (ImageView) getView().findViewById(R.id.event_screen_arrow_up);
					ImageView downArrow = (ImageView) getView().findViewById(R.id.event_screen_arrow_down);
					Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
					Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
					if (firstVisibleItem == 0) {
						if (upArrow.getVisibility() == View.VISIBLE) {
							upArrow.startAnimation(fadeOutAnimation);
						}
						upArrow.setVisibility(View.INVISIBLE);
					} else {
						if (upArrow.getVisibility() == View.INVISIBLE) {
							upArrow.startAnimation(fadeInAnimation);
						}
						upArrow.setVisibility(View.VISIBLE);
					}
					if (firstVisibleItem + visibleItemCount == totalItemCount) {
						if (downArrow.getVisibility() == View.VISIBLE) {
							downArrow.startAnimation(fadeOutAnimation);
						}
						downArrow.setVisibility(View.INVISIBLE);
					} else {
						if (downArrow.getVisibility() == View.INVISIBLE) {
							downArrow.startAnimation(fadeInAnimation);
						}
						downArrow.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		/*		eventListView.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						HashMap<String, String> eventItem = (HashMap<String, String>) parent.getAdapter().getItem(position);
						LinearLayout expandedLinearLayout = (LinearLayout) getView().findViewById(R.id.event_screen_expanded);
						TextView tvTitle = (TextView) getView().findViewById(R.id.event_screen_title);
						TextView tvDate = (TextView) getView().findViewById(R.id.event_screen_date);
						TextView tvTime = (TextView) getView().findViewById(R.id.event_screen_time);
						TextView tvDesc = (TextView) getView().findViewById(R.id.event_screen_description);

						tvTitle.setText(eventItem.get("title"));
						tvDate.setText(eventItem.get("date"));
						tvTime.setText(eventItem.get("time"));
						if (eventItem.get("desc").matches("")) {
							tvDesc.setText("No additional information");
						} else {
							tvDesc.setText(eventItem.get("desc"));
						}
						expandedLinearLayout.setVisibility(View.VISIBLE);
					}
				});*/

		/*		assignmentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
						Assignment assignment = (Assignment) av.getAdapter().getItem(pos);
						assignmentListener.onAssignmentLongClicked(assignment);
						return true;
					}
				});*/

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

}
