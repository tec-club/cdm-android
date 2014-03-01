package com.ampelement.cdm.schoolloop;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ampelement.cdm.Preferences;
import com.ampelement.cdm.R;
import com.ampelement.cdm.utils.android.ExtendedSherlockFragment;
import com.ampelement.cdm.utils.android.NavDrawerEntry;

public class SchoolLoopFragment extends ExtendedSherlockFragment {

	public static final String TAG = "SchoolLoopFragment";

	public static class Entry extends NavDrawerEntry {

		@Override
		public String getTitle() {
			return "School Loop";
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
			return SchoolLoopFragment.class;
		}

		@Override
		public void runAction(Activity activity) {
			// None
		}
	}

	private RelativeLayout schoolLoopScreen;
	private LinearLayout mLoginScreen;
	private RelativeLayout mLoadingScreen;
	private EditText mUsernameInput;
	private EditText mPasswordInput;

	boolean newCredentials = false;

	public WebView webView;
	SharedPreferences sharedPreferences;

	private class Login {
		String user;
		String pass;

		Login(String username, String password) {
			user = username;
			pass = password;
		}

		Login() {
		}

		boolean isBlank() {
			return user.matches("") || pass.matches("");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Loading Screen Handler
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		// Get Screens
		schoolLoopScreen = (RelativeLayout) inflater.inflate(R.layout.school_loop_screen, container, false);
		mLoginScreen = (LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_inputs);
		mLoadingScreen = (RelativeLayout) schoolLoopScreen.findViewById(R.id.school_loop_loading);
		mUsernameInput = (EditText) schoolLoopScreen.findViewById(R.id.school_loop_username);
		mPasswordInput = (EditText) schoolLoopScreen.findViewById(R.id.school_loop_password);

		// Attempt to retrieve stored credentials
		Login login = new Login(sharedPreferences.getString(Preferences.SCHOOL_LOOP_USERNAME, ""), sharedPreferences.getString(
				Preferences.SCHOOL_LOOP_PASSWORD, ""));

		// Setup WebView
		webView = new WebView(getActivity().getApplicationContext());
		((LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_webview_screen)).addView(webView);
		/* Setup Login View */
		// Setup Submit button
		final Button submitButton = (Button) schoolLoopScreen.findViewById(R.id.school_loop_submit);
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitLogin(v);
			}
		});
		// Setup EditText enter click
		mPasswordInput.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO && (event == null || event.getAction() == KeyEvent.ACTION_DOWN)) {
					submitLogin(v);
				}
				return true;
			}
		});

		// Load appropriate view
		if (login.isBlank()) {
			// Show Login Screen
			mLoginScreen.setVisibility(View.VISIBLE);
		} else {
			// Load School Loop Mobile page
			transitionViews(mLoginScreen, mLoadingScreen);
			new SchoolLoopLoginTask().execute(login);
		}
		return schoolLoopScreen;
	}

	void submitLogin(View v) {
		// Hide keyboard
		InputMethodManager inputManager = (InputMethodManager) getSherlockActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		// Create Login object
		Login newLogin = new Login();
		// Get credentials from EditTexts
		newLogin.user = mUsernameInput.getEditableText().toString();
		newLogin.pass = mPasswordInput.getEditableText().toString();
		// Check for blank credentials
		if (newLogin.isBlank()) {
			String errorMessage = "";
			if (newLogin.user.matches(""))
				errorMessage = "Username";
			if (newLogin.user.matches("")) {
				if (!errorMessage.matches(""))
					errorMessage = errorMessage + " and ";
				errorMessage = errorMessage + "Password";
			}
			errorMessage = errorMessage + (errorMessage.contains("and") ? " are" : " is") + " Blank";
			loginScreenWithErrorMessage(errorMessage);
		} else {
			// Attempt Login
			transitionViews(mLoginScreen, mLoadingScreen);
			newCredentials = true;
			new SchoolLoopLoginTask().execute(newLogin);
		}
	}

	void transitionViews(View one, View two) {
		// Attempt Login
		one.setVisibility(View.GONE);
		one.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_out));
		two.setVisibility(View.VISIBLE);
		two.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
	}

	private class SchoolLoopLoginTask extends AsyncTask<Login, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Login... logins) {
			if (logins.length > 0) {
				Login login = logins[0];
				long lastActiveTime = sharedPreferences.getLong(Preferences.SCHOOL_LOOP_TIME, 0);
				SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
				// Calculated as still logged in. Timeout is around 15 min
				if (lastActiveTime != 0 && (System.currentTimeMillis() - lastActiveTime) < 900000) {
					SchoolLoopAPI.Dirty.loadLoginDataToWebView(sharedPreferences, webView);
					return true;
				} else { // Not logged in
					try {
						CookieStore loginCookieStore = SchoolLoopAPI.loginToSchoolloop(new DefaultHttpClient(), login.user, login.pass, true);
						if (loginCookieStore != null) {
							SchoolLoopAPI.Dirty.migrateCookieStore2WebView(loginCookieStore, webView, sharedPrefEditor);
							updateCredentials(newCredentials ? login.user : null, newCredentials ? login.pass : null, System.currentTimeMillis());
							return true;
						} else {
							return false;
						}
					} catch (ClientProtocolException e) {
						return null;
					} catch (IOException e) {
						return null;
					}
				}
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Boolean success) {
			if (success != null) {
				if (success) {
					loadWebView();
					LinearLayout webViewScreen = (LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_webview_screen);
					webViewScreen.setVisibility(View.VISIBLE);
				} else {
					loginScreenWithErrorMessage("Bad Username or Password");
				}
			} else {
				loginScreenWithErrorMessage("Error Logging In");
			}
		}

		@SuppressLint("NewApi")
		private void loadWebView() {
			webView.getSettings().setJavaScriptEnabled(true);
			webView.setWebViewClient(new SchoolLoopWebViewClient());
			webView.loadUrl(SchoolLoopAPI.BASE_URL_SECURE + "/mobile/index");
			webView.getSettings().setSupportZoom(true);
			webView.getSettings().setBuiltInZoomControls(true);
			if (android.os.Build.VERSION.SDK_INT >= 11)
				webView.getSettings().setDisplayZoomControls(false);
			webView.setDownloadListener(new DownloadListener() {
				@Override
				public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
			});
		}
	}

	private void loginScreenWithErrorMessage(String errorString) {
		((RelativeLayout) schoolLoopScreen.findViewById(R.id.school_loop_loading)).setVisibility(View.GONE);
		LinearLayout loginScreen = (LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_inputs);
		loginScreen.setVisibility(View.VISIBLE);
		TextView errorText = (TextView) schoolLoopScreen.findViewById(R.id.school_loop_error);
		errorText.setVisibility(View.VISIBLE);
		errorText.setText(errorString);
		loginScreen.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
		errorText.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
	}

	private class SchoolLoopWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("cdm.schoolloop.com")) {
				if (url.contains("portal/logout")) {
					updateCredentials("", "", 0);
					((RelativeLayout) schoolLoopScreen.findViewById(R.id.school_loop_loading)).setVisibility(View.GONE);
					((LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_webview_screen)).setVisibility(View.GONE);
					LinearLayout loginScreen = (LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_inputs);
					((EditText) schoolLoopScreen.findViewById(R.id.school_loop_username)).setText("");
					((EditText) schoolLoopScreen.findViewById(R.id.school_loop_password)).setText("");
					loginScreen.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
					loginScreen.setVisibility(View.VISIBLE);
				} else {
					view.loadUrl(url);
					updateCredentials(null, null, System.currentTimeMillis());
				}
			} else {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
			return true;
		}
	}

	void updateCredentials(String username, String password, long time) {
		SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
		if (username != null)
			sharedPrefEditor.putString(Preferences.SCHOOL_LOOP_USERNAME, username);
		if (password != null)
			sharedPrefEditor.putString(Preferences.SCHOOL_LOOP_PASSWORD, password);
		if (time >= 0)
			sharedPrefEditor.putLong(Preferences.SCHOOL_LOOP_TIME, time);
		sharedPrefEditor.commit();
	}

}
