package com.ampelement.cdm.fragments;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ampelement.cdm.R;
import com.ampelement.cdm.objects.Preferences;
import com.ampelement.cdm.utils.SchoolLoopAPI;

public class SchoolLoopFragment extends Fragment {

	private RelativeLayout schoolLoopScreen;
	private SchoolLoopInterface schoolLoopInterface;

	boolean newCredentials = false;

	public WebView webView;
	SharedPreferences sharedPreferences;

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
		// Loading Screen Handler
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		// Show Tab Indicator
		schoolLoopInterface.setIndicator(R.id.main_school_loop_indicator);
		// Get Screens
		schoolLoopScreen = (RelativeLayout) inflater.inflate(R.layout.school_loop_screen, container, false);
		final LinearLayout loginScreen = (LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_inputs);
		final RelativeLayout loadingScreen = (RelativeLayout) schoolLoopScreen.findViewById(R.id.school_loop_loading);
		// Attempt to retrieve stored credentials
		String username = sharedPreferences.getString(Preferences.SCHOOL_LOOP_USERNAME, "");
		String password = sharedPreferences.getString(Preferences.SCHOOL_LOOP_PASSWORD, "");
		// If credentials don't exist then show login screen
		webView = new WebView(getActivity().getApplicationContext());
		((LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_webview_screen)).addView(webView);
		if (username.matches("") || password.matches("")) {
			// Show Login Screen
			loginScreen.setVisibility(View.VISIBLE);
			final EditText usernameInput = (EditText) schoolLoopScreen.findViewById(R.id.school_loop_username);
			final EditText passwordInput = (EditText) schoolLoopScreen.findViewById(R.id.school_loop_password);
			final Button submitButton = (Button) schoolLoopScreen.findViewById(R.id.school_loop_submit);
			submitButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Get Login Credentials
					String[] credentialStringArray = new String[2];
					credentialStringArray[0] = usernameInput.getEditableText().toString();
					credentialStringArray[1] = passwordInput.getEditableText().toString();
					// Check for blank credentials
					if (credentialStringArray[0].matches("") || credentialStringArray[1].matches("")) {
						TextView errorText = (TextView) schoolLoopScreen.findViewById(R.id.school_loop_error);
						errorText.setVisibility(View.VISIBLE);
						if (credentialStringArray[0].matches("") && credentialStringArray[1].matches("")) {
							errorText.setText("Username and Password are Blank");
						} else {
							errorText.setText(((credentialStringArray[0].matches("")) ? "Username" : "Password") + "is Blank");
						}
					} else {
						// Attempt Login
						loginScreen.setVisibility(View.GONE);
						loadingScreen.setVisibility(View.VISIBLE);
						newCredentials = true;
						new SchoolLoopLoginTask().execute(credentialStringArray);
					}
				}
			});
		} else {
			// Load School Loop Mobile page
			loadingScreen.setVisibility(View.VISIBLE);
			String[] credentialStringArray = new String[2];
			credentialStringArray[0] = username;
			credentialStringArray[1] = password;
			new SchoolLoopLoginTask().execute(credentialStringArray);
		}
		return schoolLoopScreen;
	}

	private class SchoolLoopLoginTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... cred) {
			long lastActiveTime = sharedPreferences.getLong(Preferences.SCHOOL_LOOP_TIME, 0);
			SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
			if (lastActiveTime != 0 && (System.currentTimeMillis() - lastActiveTime) < 900000) { // Logged
																									// in
																									// still
				CookieSyncManager syncManager = CookieSyncManager.createInstance(webView.getContext());
				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.setCookie("http://cdm.schoolloop.com/", "JSESSIONID=" + sharedPreferences.getString(Preferences.SCHOOL_LOOP_JSESSIONID, ""));
				cookieManager.setCookie("http://cdm.schoolloop.com/", "slid=" + sharedPreferences.getString(Preferences.SCHOOL_LOOP_SLID, ""));
				CookieSyncManager.getInstance().sync();
				loadWebView();
				return true;
			} else { // Not logged in
				if (cred.length == 2) {
					try {
						CookieStore loginCookieStore = SchoolLoopAPI.loginToSchoolloop(new DefaultHttpClient(), cred[0], cred[1], true);
						if (loginCookieStore != null) {
							CookieSyncManager syncManager = CookieSyncManager.createInstance(webView.getContext());
							CookieManager cookieManager = CookieManager.getInstance();
							for (Cookie cookie : loginCookieStore.getCookies()) {
								cookieManager.setCookie((cookie.isSecure() ? "https" : "http") + "://" + cookie.getDomain() + cookie.getPath(), cookie.getName() + "=" + cookie.getValue());
								if (cookie.getName().matches("slid")) {
									sharedPrefEditor.putString(Preferences.SCHOOL_LOOP_SLID, cookie.getValue());
									sharedPrefEditor.commit();
								} else if (cookie.getName().matches("JSESSIONID")) {
									sharedPrefEditor.putString(Preferences.SCHOOL_LOOP_JSESSIONID, cookie.getValue());
									sharedPrefEditor.commit();
								}
							}
							CookieSyncManager.getInstance().sync();
							if (newCredentials) {
								sharedPrefEditor.putString(Preferences.SCHOOL_LOOP_USERNAME, cred[0]);
								sharedPrefEditor.putString(Preferences.SCHOOL_LOOP_PASSWORD, cred[1]);
								sharedPrefEditor.commit();
							}
							loadWebView();
							sharedPrefEditor.putLong(Preferences.SCHOOL_LOOP_TIME, System.currentTimeMillis()).commit();
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
		}

		private void loadWebView() {
			webView.getSettings().setJavaScriptEnabled(true);
			webView.setWebViewClient(new SchoolLoopWebViewClient());
			webView.loadUrl(SchoolLoopAPI.BASE_URL_SECURE + "/mobile/index");
		}

		@Override
		protected void onPostExecute(Boolean success) {
			if (success != null) {
				if (success) {
					((LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_webview_screen)).setVisibility(View.VISIBLE);
				} else {
					loginScreenWithErrorMessage("Bad Username or Password");
				}
			} else {
				loginScreenWithErrorMessage("Error Logging In");
			}
		}
	}

	private void loginScreenWithErrorMessage(String errorString) {
		((RelativeLayout) schoolLoopScreen.findViewById(R.id.school_loop_loading)).setVisibility(View.GONE);
		LinearLayout loginScreen = (LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_inputs);
		loginScreen.setVisibility(View.VISIBLE);
		TextView errorText = (TextView) schoolLoopScreen.findViewById(R.id.school_loop_error);
		errorText.setVisibility(View.VISIBLE);
		errorText.setText(errorString);
	}

	private class SchoolLoopWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("portal/logout")) {
				SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
				sharedPrefEditor.putString(Preferences.SCHOOL_LOOP_USERNAME, "");
				sharedPrefEditor.putString(Preferences.SCHOOL_LOOP_PASSWORD, "");
				sharedPrefEditor.putLong(Preferences.SCHOOL_LOOP_TIME, 0);
				sharedPrefEditor.commit();
				((RelativeLayout) schoolLoopScreen.findViewById(R.id.school_loop_loading)).setVisibility(View.GONE);
				((LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_webview_screen)).setVisibility(View.GONE);
				LinearLayout loginScreen = (LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_inputs);
				loginScreen.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
				loginScreen.setVisibility(View.VISIBLE);
			} else {
				view.loadUrl(url);
				sharedPreferences.edit().putLong(Preferences.SCHOOL_LOOP_TIME, System.currentTimeMillis()).commit();
			}
			return true;
		}
	}

}
