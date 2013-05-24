package com.ampelement.cdm.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
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

import com.actionbarsherlock.app.SherlockFragment;
import com.ampelement.cdm.R;

public class WebViewFragment extends SherlockFragment {
	private LinearLayout mWebViewLinearLayout;

	public WebView mWebView;

	public static final String TAG = "InfoListFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View infoScreen = inflater.inflate(R.layout.info_screen, container,
				false);
		mWebViewLinearLayout = (LinearLayout) infoScreen
				.findViewById(R.id.info_screen_webView);

		mWebView = new WebView(getActivity());
		mWebView.setWebViewClient(new InfoWebViewClient());
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebViewLinearLayout.addView(mWebView);

		return infoScreen;
	}

	private class InfoWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

}
