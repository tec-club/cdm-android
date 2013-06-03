package com.ampelement.cdm.fragments;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.ampelement.cdm.R;
import com.ampelement.cdm.objects.Preferences;
import com.ampelement.cdm.utils.SchoolLoopAPI;

public class SchoolLoopFragment extends SherlockFragment {

	private RelativeLayout schoolLoopScreen;

	boolean newCredentials = false;

	public WebView webView;
	SharedPreferences sharedPreferences;

	public static final String TAG = "SchoolLoopFragment";

	private class Login {
		String user;
		String pass;

		Login(String username, String password) {
			user = username;
			pass = password;
		}

		public Login() {
			// TODO Auto-generated constructor stub
		}

		boolean blank() {
			return user.matches("") || pass.matches("");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Loading Screen Handler
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());
		// Get Screens
		schoolLoopScreen = (RelativeLayout) inflater.inflate(
				R.layout.school_loop_screen, container, false);
		final LinearLayout loginScreen = (LinearLayout) schoolLoopScreen
				.findViewById(R.id.school_loop_inputs);
		final RelativeLayout loadingScreen = (RelativeLayout) schoolLoopScreen
				.findViewById(R.id.school_loop_loading);

		// Attempt to retrieve stored credentials
		Login login = new Login(sharedPreferences.getString(
				Preferences.SCHOOL_LOOP_USERNAME, ""),
				sharedPreferences.getString(Preferences.SCHOOL_LOOP_PASSWORD,
						""));

		// If credentials don't exist then show login screen
		webView = new WebView(getActivity().getApplicationContext());
		((LinearLayout) schoolLoopScreen
				.findViewById(R.id.school_loop_webview_screen))
				.addView(webView);

		if (login.blank()) {
			// Show Login Screen
			loginScreen.setVisibility(View.VISIBLE);
			final EditText usernameInput = (EditText) schoolLoopScreen
					.findViewById(R.id.school_loop_username);
			final EditText passwordInput = (EditText) schoolLoopScreen
					.findViewById(R.id.school_loop_password);
			final Button submitButton = (Button) schoolLoopScreen
					.findViewById(R.id.school_loop_submit);
			submitButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Get Login Credentials
					Login newLogin = new Login();
					newLogin.user = usernameInput.getEditableText().toString();
					newLogin.pass = passwordInput.getEditableText().toString();
					// Check for blank credentials
					if (newLogin.blank()) {
						String errorMessage = "";
						if (newLogin.user.matches(""))
							errorMessage = "Username";
						if (newLogin.user.matches("")) {
							if (!errorMessage.matches(""))
								errorMessage = errorMessage + " and ";
							errorMessage = errorMessage + "Password";
						}
						errorMessage = errorMessage
								+ (errorMessage.contains("and") ? " are"
										: " is") + " Blank";
						loginScreenWithErrorMessage(errorMessage);
					} else {
						// Attempt Login
						transitionViews(loginScreen, loadingScreen);
						newCredentials = true;
						new SchoolLoopLoginTask().execute(newLogin);
					}
				}
			});
		} else {
			// Load School Loop Mobile page
			transitionViews(loginScreen, loadingScreen);
			new SchoolLoopLoginTask().execute(login);
		}
		return schoolLoopScreen;
	}

	void transitionViews(View one, View two) {
		// Attempt Login
		one.setVisibility(View.GONE);
		one.startAnimation(AnimationUtils.loadAnimation(getActivity()
				.getApplicationContext(), android.R.anim.fade_out));
		two.setVisibility(View.VISIBLE);
		two.startAnimation(AnimationUtils.loadAnimation(getActivity()
				.getApplicationContext(), android.R.anim.fade_in));
	}

	private class SchoolLoopLoginTask extends AsyncTask<Login, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Login... logins) {
			if (logins.length > 0) {
				Login login = logins[0];
				long lastActiveTime = sharedPreferences.getLong(
						Preferences.SCHOOL_LOOP_TIME, 0);
				SharedPreferences.Editor sharedPrefEditor = sharedPreferences
						.edit();
				// Calculated as still logged in. Timeout is around 15 min
				if (lastActiveTime != 0
						&& (System.currentTimeMillis() - lastActiveTime) < 900000) {
					CookieSyncManager syncManager = CookieSyncManager
							.createInstance(webView.getContext());
					CookieManager cookieManager = CookieManager.getInstance();
					cookieManager.setCookie(
							"http://cdm.schoolloop.com/",
							"JSESSIONID="
									+ sharedPreferences.getString(
											Preferences.SCHOOL_LOOP_JSESSIONID,
											""));
					cookieManager.setCookie(
							"http://cdm.schoolloop.com/",
							"slid="
									+ sharedPreferences.getString(
											Preferences.SCHOOL_LOOP_SLID, ""));
					CookieSyncManager.getInstance().sync();
					loadWebView();
					return true;
				} else { // Not logged in
					try {
						CookieStore loginCookieStore = SchoolLoopAPI
								.loginToSchoolloop(new DefaultHttpClient(),
										login.user, login.pass, true);
						if (loginCookieStore != null) {
							CookieSyncManager syncManager = CookieSyncManager
									.createInstance(webView.getContext());
							CookieManager cookieManager = CookieManager
									.getInstance();
							for (Cookie cookie : loginCookieStore.getCookies()) {
								cookieManager.setCookie(
										(cookie.isSecure() ? "https" : "http")
												+ "://" + cookie.getDomain()
												+ cookie.getPath(),
										cookie.getName() + "="
												+ cookie.getValue());
								if (cookie.getName().matches("slid")) {
									sharedPrefEditor.putString(
											Preferences.SCHOOL_LOOP_SLID,
											cookie.getValue());
									sharedPrefEditor.commit();
								} else if (cookie.getName().matches(
										"JSESSIONID")) {
									sharedPrefEditor.putString(
											Preferences.SCHOOL_LOOP_JSESSIONID,
											cookie.getValue());
									sharedPrefEditor.commit();
								}
							}
							CookieSyncManager.getInstance().sync();
							updateCredentials(newCredentials ? login.user
									: null, newCredentials ? login.pass : null,
									System.currentTimeMillis());
							loadWebView();
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
				public void onDownloadStart(String url, String userAgent,
						String contentDisposition, String mimetype,
						long contentLength) {
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
			});
		}

		@Override
		protected void onPostExecute(Boolean success) {
			if (success != null) {
				if (success) {
					LinearLayout webViewScreen = (LinearLayout) schoolLoopScreen
							.findViewById(R.id.school_loop_webview_screen);
					webViewScreen.setVisibility(View.VISIBLE);
				} else {
					loginScreenWithErrorMessage("Bad Username or Password");
				}
			} else {
				loginScreenWithErrorMessage("Error Logging In");
			}
		}
	}

	private void loginScreenWithErrorMessage(String errorString) {
		((RelativeLayout) schoolLoopScreen
				.findViewById(R.id.school_loop_loading))
				.setVisibility(View.GONE);
		LinearLayout loginScreen = (LinearLayout) schoolLoopScreen
				.findViewById(R.id.school_loop_inputs);
		loginScreen.setVisibility(View.VISIBLE);
		TextView errorText = (TextView) schoolLoopScreen
				.findViewById(R.id.school_loop_error);
		errorText.setVisibility(View.VISIBLE);
		errorText.setText(errorString);
		loginScreen.startAnimation(AnimationUtils.loadAnimation(getActivity()
				.getApplicationContext(), android.R.anim.fade_in));
		errorText.startAnimation(AnimationUtils.loadAnimation(getActivity()
				.getApplicationContext(), android.R.anim.fade_in));
	}

	private class SchoolLoopWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("cdm.schoolloop.com")) {
				if (url.contains("portal/logout")) {
					updateCredentials("", "", 0);
					((RelativeLayout) schoolLoopScreen
							.findViewById(R.id.school_loop_loading))
							.setVisibility(View.GONE);
					((LinearLayout) schoolLoopScreen
							.findViewById(R.id.school_loop_webview_screen))
							.setVisibility(View.GONE);
					LinearLayout loginScreen = (LinearLayout) schoolLoopScreen
							.findViewById(R.id.school_loop_inputs);
					loginScreen.startAnimation(AnimationUtils.loadAnimation(
							getActivity().getApplicationContext(),
							android.R.anim.fade_in));
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
			sharedPrefEditor.putString(Preferences.SCHOOL_LOOP_USERNAME,
					username);
		if (password != null)
			sharedPrefEditor.putString(Preferences.SCHOOL_LOOP_PASSWORD,
					password);
		if (time >= 0)
			sharedPrefEditor.putLong(Preferences.SCHOOL_LOOP_TIME, time);
		sharedPrefEditor.commit();
	}

}
