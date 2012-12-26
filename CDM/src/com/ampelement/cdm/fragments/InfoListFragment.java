package com.ampelement.cdm.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.ampelement.cdm.R;

public class InfoListFragment extends SherlockFragment {

	private static final String BASE_URL = "http://www.ampelement.com/cdm";

	private GridView mInfoGridView;
	private LinearLayout mWebViewLinearLayout;
	private ScrollView mInfoScrollView;

	public WebView mWebView;

	private InfoInterface infoInterface;

	public static final String TAG = "InfoListFragment";

	public interface InfoInterface {
		public void setIndicator(int indicatorID);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			infoInterface = (InfoInterface) activity;
		} catch (ClassCastException e) {
			Log.e(TAG, activity.toString() + " must implement OnUpdateListener");
			// getActivity().finish();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View infoScreen = inflater.inflate(R.layout.info_screen, container, false);
		mInfoGridView = (GridView) infoScreen.findViewById(R.id.info_screen_gridView);
		mWebViewLinearLayout = (LinearLayout) infoScreen.findViewById(R.id.info_screen_webView);
		mInfoScrollView = (ScrollView)infoScreen.findViewById(R.id.info_screen_scrollView);

		mWebView = new WebView(getActivity());
		mWebView.setWebViewClient(new InfoWebViewClient());
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebViewLinearLayout.addView(mWebView);

		infoInterface.setIndicator(R.id.main_info_indicator);

		mInfoGridView.setAdapter(new InfoAdapter(getActivity().getApplicationContext()));

		return infoScreen;
	}

	private class InfoItem {
		String name;
		int thumbnailRes;
		String url;

		public InfoItem(String name, int thumbnailRes, String url) {
			this.name = name;
			this.thumbnailRes = thumbnailRes;
			this.url = url;
		}
	}

	private class InfoAdapter extends BaseAdapter {
		ArrayList<InfoItem> infoList;
		LayoutInflater layoutInflater;

		public InfoAdapter(Context context) {
			layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			infoList = new ArrayList<InfoItem>();
			infoList.add(new InfoItem("Handbook", R.drawable.info_handbook, BASE_URL + "/handbook"));
			infoList.add(new InfoItem("Bell Schedule", R.drawable.info_bell_schedule, BASE_URL + "/bell_schedule"));
		}

		@Override
		public int getCount() {
			return infoList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
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
			// Create view elements
			LinearLayout itemLayout = (LinearLayout) layoutInflater.inflate(R.layout.info_item_view, null);
			ImageView itemThumbnail = (ImageView) itemLayout.findViewById(R.id.info_item_imageView);
			TextView itemTitle = (TextView) itemLayout.findViewById(R.id.info_item_textView);
			// Set view items to corresponding InfoItem information
			itemThumbnail.setImageResource(thumbnailRes);
			itemTitle.setText(name);
			// Set webView to load page on InfoItem click
			itemThumbnail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mWebView.loadUrl(url);
					mInfoScrollView.setVisibility(View.GONE);
					mWebViewLinearLayout.setVisibility(View.VISIBLE);
				}
			});

			return itemLayout;
		}
	}

	private class InfoWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			Toast.makeText(getActivity(), "", Toast.LENGTH_LONG).show();
		}
	}

}
