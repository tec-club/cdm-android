package com.ampelement.cdm.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.ampelement.cdm.R;

public class InfoListFragment extends SherlockFragment {

	public static final String TAG = "InfoListFragment";
	private static final String BASE_URL = "http://www.ampelement.com/cdm";
	private ListView mInfoListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View infoScreen = inflater.inflate(R.layout.info_screen, container,
				false);
		mInfoListView = (ListView) infoScreen
				.findViewById(R.id.info_screen_gridView);

		// ScaleInAnimationAdapter testAdapter = new ScaleInAnimationAdapter(
		// new InfoAdapter(getSherlockActivity().getApplicationContext()));
		//
		// SwingBottomInAnimationAdapter swingBottomRightInAnimationAdapter =
		// new SwingBottomInAnimationAdapter(
		// new InfoAdapter(getSherlockActivity().getApplicationContext()));
		//
		// testAdapter.setListView(mInfoListView);
		// mInfoListView.setAdapter(swingBottomRightInAnimationAdapter);
		mInfoListView.setAdapter(new InfoAdapter(getSherlockActivity()));
		mInfoListView.setVisibility(View.INVISIBLE);
		mInfoListView.setLayoutAnimation(new LayoutAnimationController(
				AnimationUtils.loadAnimation(getSherlockActivity(),
						R.anim.slide_up), .5f));
		return infoScreen;
	}

	boolean shownAnimation = false;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (!shownAnimation) {
				mInfoListView.setVisibility(View.VISIBLE);
				shownAnimation = true;
			}
		} else {
		}

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

		public InfoItem(String name, int thumbnailRes, String url,
				String description) {
			this.name = name;
			this.thumbnailRes = thumbnailRes;
			this.url = url;
			this.description = description;
		}
	}

	private class InfoAdapter extends BaseAdapter {
		ArrayList<InfoItem> infoList;
		LayoutInflater layoutInflater;

		public InfoAdapter(Context context) {
			layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			infoList = new ArrayList<InfoItem>();
			infoList.add(new InfoItem(
					"Handbook",
					R.drawable.info_handbook,
					BASE_URL + "/handbook",
					"The school handbook. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum malesuada purus quis lacus adipiscing elementum."));
			infoList.add(new InfoItem(
					"Bell Schedule",
					R.drawable.info_bell_schedule,
					BASE_URL + "/bell_schedule",
					"The school bell schedule. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum malesuada purus quis lacus adipiscing elementum."));
			infoList.add(new InfoItem(
					"Year Schedule",
					R.drawable.info_bell_schedule,
					"http://cdm.schoolloop.com/file/1211914146706/1229223566913/7406136305481272074.pdf",
					"The yearly schedule. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum malesuada purus quis lacus adipiscing elementum."));
		}

		@Override
		public int getCount() {
			return infoList.size();
		}

		@Override
		public Object getItem(int position) {
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

			final boolean isViewRight = position % 2 == 0;

			ViewHolder holder = convertView != null ? (ViewHolder) convertView
					.getTag() : null;

			// If the convertView exists and is for the proper ViewHolder
			// (either Right or Left)
			if ((holder != null)
					&& ((isViewRight && holder instanceof ViewHolderRight) || (!isViewRight && holder instanceof ViewHolderLeft))) {
				Log.d(TAG, "Wow, isn't scrolling so smooth?");
			} else {
				Log.d(TAG, "Darn. We have to create objects all the time.");
				holder = isViewRight ? new ViewHolderRight()
						: new ViewHolderLeft();
				// Create view elements
				int resMain = isViewRight ? R.layout.info_item_r_view
						: R.layout.info_item_l_view;
				convertView = (LinearLayout) layoutInflater.inflate(resMain,
						null);
				int resImg = isViewRight ? R.id.info_item_r_imageView
						: R.id.info_item_l_imageView;
				holder.thumbnail = (ImageView) convertView.findViewById(resImg);
				int resTitle = isViewRight ? R.id.info_item_r_textView_title
						: R.id.info_item_l_textView_title;
				holder.title = (TextView) convertView.findViewById(resTitle);
				int resDesc = isViewRight ? R.id.info_item_r_textView_description
						: R.id.info_item_l_textView_description;
				holder.description = (TextView) convertView
						.findViewById(resDesc);
				convertView.setTag(holder);
			}

			// Set view items to corresponding InfoItem information
			holder.thumbnail.setImageResource(thumbnailRes);
			holder.title.setText(name);
			holder.description.setText(description);
			// Set webView to load page on InfoItem click
			holder.thumbnail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					WebViewDialogFragment.newInstance(url, false).show(
							getSherlockActivity().getSupportFragmentManager(),
							name);
				}
			});

			return convertView;
		}
	}

	private class InfoWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

}
