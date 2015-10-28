package com.asb.cdm.infoscreen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.asb.cdm.R;
import com.asb.cdm.infoscreen.WebViewDialogFragment.DialogWebViewClient.OnPageLoaded;

/* TODO Redesign implementation
I don't really agree with this design structure. Right now all of the InfoItems open a WebViewDialogFragment
to display the information, which requires an inefficient amount of data usage. I think that each of the
displays should be given a SHA256 hash, and displays should be cached onto the devices. The phone should only
check if the hash on our server is different than the one cached and then download the new version in the background.
This would allow for smaller data usage, on average faster loading, and the ability for a native UI design that doesn't
 depend on the webview. Instead we could have a similar styled ImageFragment class that holds the drawable resources.
 I want the new app to be capable of presenting cached data in the case of users not having network access.
 */
public class WebViewDialogFragment extends DialogFragment {

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
	public static WebViewDialogFragment newInstance(String url, boolean allowNav, boolean setInitialScaleFull) {
		WebViewDialogFragment f = new WebViewDialogFragment();
		Bundle b = new Bundle();
		b.putString("url", url);
		b.putBoolean("allownav", allowNav);
		b.putBoolean("initialScaleFull", setInitialScaleFull);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout v = (LinearLayout) inflater.inflate(R.layout.webview, container);
		final View progressView = v.findViewById(R.id.webview_loading);

		String url = "";
		boolean allowURLLoading = false;
		boolean setInitialScaleFull = false;

		Bundle args = getArguments();
		if (args != null) {
			url = args.getString("url");
			allowURLLoading = args.getBoolean("allownav");
			setInitialScaleFull = args.getBoolean("initialScaleFull");
		}
        /*
        Setup the WebView display
        When done loading close the progressView and open the page
         */
        final WebView webView = new WebView(getActivity());
		DialogWebViewClient dwvClient = new DialogWebViewClient(allowURLLoading);
		dwvClient.setOnPageLoaded(new OnPageLoaded() {
			@Override
			public void finishedLoading() {
				progressView.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
			}
		});

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(dwvClient);
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		if (android.os.Build.VERSION.SDK_INT >= 11)
			webView.getSettings().setDisplayZoomControls(false); // @SuppressLint("NewApi")
		if (setInitialScaleFull)
			webView.setInitialScale(100);
		webView.setVisibility(View.GONE);
		webView.loadUrl(url);
		v.addView(webView);

		return v;
	}

    /**
     * The client for handling the WebView in the DialogFragment
     */
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
