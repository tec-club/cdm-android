package com.ampelement.cdm.infoscreen;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ampelement.cdm.R;
import com.ampelement.cdm.utils.android.ExtendedSherlockFragment;
import com.ampelement.cdm.utils.android.NavDrawerEntry;

public class InfoListFragment extends ExtendedSherlockFragment {

	public static final String TAG = "InfoListFragment";

	public static class Entry extends NavDrawerEntry {

		@Override
		public String getTitle() {
			return "Info / Handbook";
		}

		@Override
		public boolean isCategory() {
			return false;
		}

		@Override
		public int getIcon() {
			return 0;
		}

		@Override
		public boolean isFragment() {
			return true;
		}

		@Override
		public Class<? extends ExtendedSherlockFragment> getFragmentType() {
			return InfoListFragment.class;
		}

		@Override
		public void runAction(Activity activity) {
			// None
		}
	}

	private static final String BASE_URL = "http://www.ampelement.com/cdm";

	private ListView mInfoListView;

	private InfoAdapter mInfoAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View infoScreen = inflater.inflate(R.layout.info_screen, container, false);
		mInfoListView = (ListView) infoScreen.findViewById(R.id.info_screen_gridView);

		mInfoAdapter = new InfoAdapter(getSherlockActivity());
		mInfoListView.setAdapter(mInfoAdapter);

		mInfoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View rowView, int position, long itemID) {
				InfoItem item = mInfoAdapter.getItem(position);
				WebViewDialogFragment.newInstance(item.url, false, item.initialScaleFull).show(getSherlockActivity().getSupportFragmentManager(), item.name);
			}
		});

		return infoScreen;
	}

	static class ViewHolder {
		ImageView thumbnail;
		TextView title;
		TextView description;
	}

	static class ViewHolderRight extends ViewHolder {
	}

	static class ViewHolderLeft extends ViewHolder {
	}

	private class InfoItem {
		String name;
		String description;
		int thumbnailRes;
		String url;
		boolean initialScaleFull;

		public InfoItem(String name, int thumbnailRes, String url, String description, boolean initialScaleFull) {
			this.name = name;
			this.thumbnailRes = thumbnailRes;
			this.url = url;
			this.description = description;
			this.initialScaleFull = initialScaleFull;
		}
	}

	private class InfoAdapter extends BaseAdapter {
		ArrayList<InfoItem> infoList;
		LayoutInflater layoutInflater;

		public InfoAdapter(Context context) {
			layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			infoList = new ArrayList<InfoItem>();
			infoList.add(new InfoItem(
					"Handbook",
					R.drawable.info_handbook,
					BASE_URL + "/handbook",
					"The school handbook. Contains information about dresscode policy, pick-up and drop-off, school contact info, graaduation requirements, and more...",
					true));
			infoList.add(new InfoItem(
					"Bell Schedule",
					R.drawable.info_bell_schedule,
					BASE_URL + "/bell_schedule",
					"The school bell schedule. Contains time information for Late Starts, Minimum Days, Regular Days, Final Exams, Assmebly/Rally Schedule, CST Exams and Senior Projects.",
					false));
			/*
			 * infoList.add(new InfoItem( "Year Schedule",
			 * R.drawable.info_bell_schedule,
			 * "http://cdm.schoolloop.com/file/1211914146706/1229223566913/7406136305481272074.pdf"
			 * ,
			 * "The yearly schedule. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum malesuada purus quis lacus adipiscing elementum."
			 * , false));
			 */
		}

		@Override
		public int getCount() {
			return infoList.size();
		}

		@Override
		public InfoItem getItem(int position) {
			return infoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Get InfoItem information
			final String name = infoList.get(position).name;
			final int thumbnailRes = infoList.get(position).thumbnailRes;
			final String url = infoList.get(position).url;
			final String description = infoList.get(position).description;
			final boolean initialScale = infoList.get(position).initialScaleFull;

			final boolean isViewRight = position % 2 == 0;

			ViewHolder holder = convertView != null ? (ViewHolder) convertView.getTag() : null;

			// If the convertView exists and is for the proper ViewHolder
			// (either Right or Left)
			if ((holder != null) && ((isViewRight && holder instanceof ViewHolderRight) || (!isViewRight && holder instanceof ViewHolderLeft))) {
			} else {
				holder = isViewRight ? new ViewHolderRight() : new ViewHolderLeft();
				// Create view elements
				int resMain = isViewRight ? R.layout.info_item_r_view : R.layout.info_item_l_view;
				convertView = (LinearLayout) layoutInflater.inflate(resMain, null);
				int resImg = isViewRight ? R.id.info_item_r_imageView : R.id.info_item_l_imageView;
				holder.thumbnail = (ImageView) convertView.findViewById(resImg);
				int resTitle = isViewRight ? R.id.info_item_r_textView_title : R.id.info_item_l_textView_title;
				holder.title = (TextView) convertView.findViewById(resTitle);
				int resDesc = isViewRight ? R.id.info_item_r_textView_description : R.id.info_item_l_textView_description;
				holder.description = (TextView) convertView.findViewById(resDesc);
				convertView.setTag(holder);
			}

			// Set view items to corresponding InfoItem information
			holder.thumbnail.setImageResource(thumbnailRes);
			holder.title.setText(name);
			holder.description.setText(description);

			return convertView;
		}
	}

}
