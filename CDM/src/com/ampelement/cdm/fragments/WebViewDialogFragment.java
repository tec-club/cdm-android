package com.ampelement.cdm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.ampelement.cdm.fragments.WebViewDialogFragment.DialogWebViewClient.OnPageLoaded;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.ampelement.cdm.R;

public class WebViewDialogFragment extends SherlockDialogFragment {

	public static final String TAG = "InfoListFragment";

	/**
	 * Create a Dialog containing a WebView
	 * 
	 * @param url
	 *            - The URL to load in the WebView
	 * @param allowNav
	 *            - If hyperlinks should be followed in the WebView
	 * @return The Dialog Fragment to be shown
	 */
	public static WebViewDialogFragment newInstance(String url, boolean allowNav) {
		WebViewDialogFragment f = new WebViewDialogFragment();
		Bundle b = new Bundle();
		b.putString("url", url);
		b.putBoolean("allownav", allowNav);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LinearLayout v = (LinearLayout) inflater.inflate(R.layout.webview,
				container);
		final View progressView = v.findViewById(R.id.webview_loading);

		String url = "";
		boolean allowURLLoading = false;

		Bundle args = getArguments();
		if (args != null) {
			url = args.getString("url");
			allowURLLoading = args.getBoolean("allownav");
		}

		final WebView webView = new WebView(getActivity());
		DialogWebViewClient dwvClient = new DialogWebViewClient(allowURLLoading);
		dwvClient.setOnPageLoaded(new OnPageLoaded() {
			@Override
			public void finishedLoading() {
				progressView.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
			}
		});
		webView.setWebViewClient(dwvClient);
		webView.getSettings().setBuiltInZoomControls(false);
		webView.setVisibility(View.GONE);
		webView.loadUrl(url);
		webView.setInitialScale(100);
		v.addView(webView);

		return v;
	}

	public static class DialogWebViewClient extends WebViewClient {
		private boolean allowURLLoading = false;
		private OnPageLoaded onPageLoaded;

		public interface OnPageLoaded {
			void finishedLoading();
		}

		void setOnPageLoaded(OnPageLoaded opl) {
			onPageLoaded = opl;
		}

		public DialogWebViewClient(boolean canFollow) {
			super();
			allowURLLoading = canFollow;
		}

		private boolean hasLoaded = false;

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!hasLoaded || allowURLLoading)
				view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (onPageLoaded != null)
				onPageLoaded.finishedLoading();
		}
	}

}
