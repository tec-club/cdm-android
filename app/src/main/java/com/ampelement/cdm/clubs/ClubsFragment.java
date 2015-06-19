package com.ampelement.cdm.clubs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ampelement.cdm.Preferences;
import com.ampelement.cdm.R;
import com.ampelement.cdm.clubs.GetClubsTask.OnUpdateComplete;
import com.ampelement.cdm.utils.android.ExtendedFragment;
import com.ampelement.cdm.utils.android.NavDrawerEntry;
import com.ampelement.cdm.utils.android.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

public class ClubsFragment extends ExtendedFragment {

	public static final String TAG = "ClubsFragment";
	
	@Override
	public String getFragmentTag() {
		return ClubsFragment.class.getSimpleName();
	}

	public static class Entry extends NavDrawerEntry {

		@Override
		public String getTitle() {
			return "Clubs";
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
			return ClubsFragment.class;
		}

		@Override
		public void runAction(Activity activity) {
			// None
		}
	}

	SharedPreferences mPref;

	GetClubsTask mGetClubsTask;
    ClubData[] mClubData; //holds the array of Club values

	// View variables
	ClubListAdapter mClubListAdapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Retrieve cached club JSON
		mPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		String cachedClubJson = mPref.getString(Preferences.CLUB_CACHED_DATA, null);
		int cachedClubJsonVersion = mPref.getInt(Preferences.CLUB_CACHED_DATA_VERSION, -1);

        // Start off Club data update, its asynchronous
        mGetClubsTask = new GetClubsTask(cachedClubJsonVersion, mPref, new OnUpdateComplete() {

			@Override
			public void onComplete(ClubData[] clubData) {
				if (clubData != null) {
					mClubData = clubData;
					if (mClubListAdapter != null)
						mClubListAdapter.notifyDataSetChanged();
				}
			}
		});

		mGetClubsTask.execute();

		// Setup ListAdapter to show Club data
		mClubData = GetClubsTask.parseClubData(cachedClubJson);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Setup view
		View view = inflater.inflate(R.layout.club_screen, container, false);
		ListView viewClubList = (ListView) view.findViewById(R.id.club_screen_listView);
        //set the viewClubList adapter
		if (mClubData != null) {
			mClubListAdapter = new ClubListAdapter(getActivity());
			viewClubList.setAdapter(mClubListAdapter);
		} else {
            // TODO Implement "no data found" \
            /*
            What should be done here???
            A "sorry no clubs found" page?
             */
        }

		return view;
	}

	@Override
	public void onDestroyView() {
        mGetClubsTask.setOnUpdateCompleteListener(null); //Why?????

		super.onDestroyView();
	}

    /*
    Adapter for the listview that contains the club data
     */
    private class ClubListAdapter extends BaseAdapter {
		private final Context context;
		private final LayoutInflater inflater;

		CircleTransform picassoCircleTransform = new CircleTransform();

		public ClubListAdapter(Context context) {
			this.context = context;
			this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Setup row view
			if (convertView == null)
				convertView = inflater.inflate(R.layout.club_item_view, parent, false);
            /*
             *The first time around that the adapter cycles through the club_items
              * the getTag() call will return null, this means that every single
               * club_item view will pass through the following if statement and
               * have it's tag set to the viewHolder object
             */
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();

			if (viewHolder == null) {
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			}

			try {
				// Get current row item
				ClubData club = getItem(position);

				// Populate view
				viewHolder.viewName.setText(club.name);
				viewHolder.viewDesc.setText(club.description);
				// viewTimes.setText(Utils.combine(club.meetingTimes, "\n"));
				viewHolder.viewTimes.setText(club.president);
                //perform Picasso animation
                Picasso.with(getActivity()).load(club.getLogoUrl()).placeholder(R.drawable.avatar_missing_circle).transform(picassoCircleTransform)
						.into(viewHolder.viewLogo);
			} catch (Exception e) {

			}

			return convertView;
		}

		@Override
		public int getCount() {
			return mClubData.length;
		}

		@Override
		public ClubData getItem(int pos) {
			return mClubData[pos];
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}
	}

    /**
     * This ViewHolder class is used to limit resource consumption when looking for Views
     * by setting View references in it which is set in each club_item View tag as static data
     */
    static class ViewHolder {
		TextView viewName;
		TextView viewDesc;
		TextView viewTimes;
		ImageView viewLogo;

		public ViewHolder(View rowRootView) {
			viewName = (TextView) rowRootView.findViewById(R.id.club_item_title_textView);
			viewDesc = (TextView) rowRootView.findViewById(R.id.club_item_description_textView);
			viewTimes = (TextView) rowRootView.findViewById(R.id.club_item_meeting_times_textView);
			viewLogo = (ImageView) rowRootView.findViewById(R.id.club_item_imageView);
		}
	}

}
