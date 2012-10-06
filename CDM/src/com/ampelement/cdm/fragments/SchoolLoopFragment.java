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
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ampelement.cdm.R;
import com.ampelement.cdm.objects.Preferences;
import com.ampelement.cdm.utils.SchoolLoopAPI;
import com.ampelement.cdm.utils.SchoolLoopAPI.Event;
import com.ampelement.cdm.utils.SchoolLoopAPI.EventFetcher;
import com.ampelement.cdm.utils.WebAPI;

public class SchoolLoopFragment extends Fragment {

	private ListView eventListView;
	private LinearLayout schoolLoopScreen;
	private SchoolLoopInterface schoolLoopInterface;
	
	boolean newCredentials = false;

	public WebView webView;

	public static final String TAG = "SchoolLoopFragment";

	public interface SchoolLoopInterface {
		public void setIndicator(int indicatorID);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			schoolLoopInterface = (SchoolLoopInterface) activity;
		} catch (ClassCastException e) {
			Log.e(TAG, activity.toString() + " must implement OnUpdateListener");
			// getActivity().finish();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Get Screens
		schoolLoopScreen = (LinearLayout) inflater.inflate(R.layout.school_loop_screen, container, false);
		final LinearLayout loginScreen = (LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_inputs);
		final RelativeLayout loadingScreen = (RelativeLayout) schoolLoopScreen.findViewById(R.id.school_loop_loading);
		final LinearLayout webViewScreen = (LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_webview_screen);
		// Attempt to retrieve stored credentials
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(inflater.getContext());
		String username = sharedPreferences.getString(Preferences.SCHOOL_LOOP_USERNAME, "");
		String password = sharedPreferences.getString(Preferences.SCHOOL_LOOP_PASSWORD, "");
		// If credentials don't exist then show login screen
		webView = new WebView(getActivity().getApplicationContext());
		if (username.matches("") && password.matches("")) {
			loginScreen.setVisibility(View.VISIBLE);
			final EditText usernameInput = (EditText) schoolLoopScreen.findViewById(R.id.school_loop_username);
			final EditText passwordInput = (EditText) schoolLoopScreen.findViewById(R.id.school_loop_password);
			final Button submitButton = (Button) schoolLoopScreen.findViewById(R.id.school_loop_submit);
			submitButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String[] credentialStringArray = new String[2];
					credentialStringArray[0] = usernameInput.getEditableText().toString();
					credentialStringArray[1] = passwordInput.getEditableText().toString();
					if (credentialStringArray[0].matches("") || credentialStringArray[1].matches("")) {
						TextView errorText = (TextView) schoolLoopScreen.findViewById(R.id.school_loop_error);
						errorText.setVisibility(View.VISIBLE);
						if (credentialStringArray[0].matches("") && credentialStringArray[1].matches("")) {
							errorText.setText("Username and Password are Blank");
						} else {
							errorText.setText(((credentialStringArray[0].matches("")) ? "Username" : "Password") + "is Blank");
						}
					} else {
						loginScreen.setVisibility(View.GONE);
						loadingScreen.setVisibility(View.VISIBLE);
						newCredentials = true;
						new SchoolLoopLoginTask().execute(credentialStringArray);
					}
				}
			});
		} else {
			loadingScreen.setVisibility(View.VISIBLE);
			String[] credentialStringArray = new String[2];
			credentialStringArray[0] = username;
			credentialStringArray[1] = password;
			new SchoolLoopLoginTask().execute(credentialStringArray);
		}
		schoolLoopInterface.setIndicator(R.id.main_school_loop_indicator);
		return schoolLoopScreen;
	}

	private class SchoolLoopLoginTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... cred) {
			if (cred.length == 2) {
				try {
					CookieStore loginCookieStore = SchoolLoopAPI.loginToSchoolloop(new DefaultHttpClient(), cred[0], cred[1], true);
					if (loginCookieStore != null) {
						webView.getSettings().setJavaScriptEnabled(true);
						webView.setWebViewClient(new SchoolLoopWebViewClient());
						CookieSyncManager syncManager = CookieSyncManager.createInstance(webView.getContext());
						CookieManager cookieManager = CookieManager.getInstance();
						for (Cookie cookie : loginCookieStore.getCookies()) {
							cookieManager.setCookie((cookie.isSecure() ? "https" : "http") + "://" + cookie.getDomain() + cookie.getPath(), cookie.getName() + "=" + cookie.getValue());
						}
						CookieSyncManager.getInstance().sync();
						webView.loadUrl(SchoolLoopAPI.BASE_URL_SECURE + "/mobile/index");
						if (newCredentials) {
							SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(webView.getContext());
							SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
							sharedPrefEditor.putString(Preferences.SCHOOL_LOOP_USERNAME, cred[0]);
							sharedPrefEditor.putString(Preferences.SCHOOL_LOOP_PASSWORD, cred[1]);
							sharedPrefEditor.commit();
						}
						return true;
					} else {
						return false;
					}
				} catch (ClientProtocolException e) {
					return null;
				} catch (IOException e) {
					return null;
				}
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Boolean success) {
			RelativeLayout loadingScreen = (RelativeLayout) schoolLoopScreen.findViewById(R.id.school_loop_loading);
			loadingScreen.setVisibility(View.GONE);
			if (success != null) {
				if (success) {
					LinearLayout webViewScreen = (LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_webview_screen);
					webViewScreen.addView(webView);
					webViewScreen.setVisibility(View.VISIBLE);
				} else {
					LinearLayout loginScreen = (LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_inputs);
					loginScreen.setVisibility(View.VISIBLE);
					TextView errorText = (TextView) schoolLoopScreen.findViewById(R.id.school_loop_error);
					errorText.setVisibility(View.VISIBLE);
					errorText.setText("Bad Username or Password");
				}
			} else {
				LinearLayout loginScreen = (LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_inputs);
				loginScreen.setVisibility(View.VISIBLE);
				TextView errorText = (TextView) schoolLoopScreen.findViewById(R.id.school_loop_error);
				errorText.setVisibility(View.VISIBLE);
				errorText.setText("Error Logging In");
			}
		}
	}

	private class SchoolLoopWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

}
