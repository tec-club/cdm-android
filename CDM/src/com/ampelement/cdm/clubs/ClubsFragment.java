package com.ampelement.cdm.clubs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.ampelement.cdm.Preferences;
import com.ampelement.cdm.R;
import com.ampelement.cdm.clubs.GetClubsTask.OnUpdateComplete;
import com.ampelement.cdm.utils.Utils;
import com.squareup.picasso.Picasso;

public class ClubsFragment extends SherlockFragment {

	public static final String TAG = "ClubsFragment";

	SharedPreferences mPref;

	ClubData[] mClubData;

	// View variables
	ClubListAdapter mClubListAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Setup view
		View view = inflater.inflate(R.layout.club_screen, container, false);
		ListView viewClubList = (ListView) view.findViewById(R.id.club_screen_listView);

		// Retrieve cached club JSON
		mPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		String cachedClubJson = mPref.getString(Preferences.CLUB_CACHED_DATA, null);
		int cachedClubJsonVersion = mPref.getInt(Preferences.CLUB_CACHED_DATA_VERSION, -1);

		// Start off Club data update
		GetClubsTask clubsTask = new GetClubsTask(cachedClubJsonVersion, mPref, new OnUpdateComplete() {

			@Override
			public void onComplete(ClubData[] clubData) {
				if (clubData != null) {
					mClubListAdapter.clubData = clubData;
					mClubListAdapter.notifyDataSetChanged();
				}
			}
		});
		clubsTask.execute();

		// Setup ListAdapter to show Club data
		mClubData = GetClubsTask.parseClubData(cachedClubJson);
		if (mClubData != null) {
			mClubListAdapter = new ClubListAdapter(getSherlockActivity(), mClubData);
			viewClubList.setAdapter(mClubListAdapter);
		}

		return view;
	}

	private class ClubListAdapter extends BaseAdapter {
		private final Context context;
		private final LayoutInflater inflater;
		private ClubData[] clubData;

		public ClubListAdapter(Context context, ClubData[] values) {
			this.context = context;
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.clubData = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Setup row view
			if (convertView == null)
				convertView = inflater.inflate(R.layout.club_item_view, parent, false);
			TextView viewName = (TextView) convertView.findViewById(R.id.club_item_title_textView);
			TextView viewDesc = (TextView) convertView.findViewById(R.id.club_item_description_textView);
			TextView viewTimes = (TextView) convertView.findViewById(R.id.club_item_meeting_times_textView);
			ImageView viewLogo = (ImageView) convertView.findViewById(R.id.club_item_imageView);

			// Get current row item
			ClubData club = clubData[position];

			// Populate view
			viewName.setText(club.name);
			viewDesc.setText(club.description);
			viewTimes.setText(Utils.combine(club.meetingTimes, "\n"));
			Picasso.with(getActivity()).load(club.getLogoUrl()).into(viewLogo);

			return convertView;
		}

		@Override
		public int getCount() {
			return clubData.length;
		}

		@Override
		public Object getItem(int pos) {
			return clubData[pos];
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}
	}

}
