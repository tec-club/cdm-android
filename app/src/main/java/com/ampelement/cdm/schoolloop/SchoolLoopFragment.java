package com.ampelement.cdm.schoolloop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
import com.ampelement.cdm.utils.android.ExtendedFragment;
import com.ampelement.cdm.utils.android.NavDrawerEntry;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SchoolLoopFragment extends ExtendedFragment {

    public static final String TAG = "SchoolLoopFragment";
    public WebView mWebView;
    boolean newCredentials = false;
    SharedPreferences mSharedPreferences;
    private RelativeLayout schoolLoopScreen;
    private LinearLayout mLoginScreen;
    private RelativeLayout mLoadingScreen;
    private EditText mUsernameInput;
    private EditText mPasswordInput;

    @Override
    public String getFragmentTag() {
        return SchoolLoopFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Loading Screen Handler
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        // Get Screens
        schoolLoopScreen = (RelativeLayout) inflater.inflate(R.layout.school_loop_screen, container, false);
        mLoginScreen = (LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_inputs);
        mLoadingScreen = (RelativeLayout) schoolLoopScreen.findViewById(R.id.school_loop_loading);
        mUsernameInput = (EditText) schoolLoopScreen.findViewById(R.id.school_loop_username);
        mPasswordInput = (EditText) schoolLoopScreen.findViewById(R.id.school_loop_password);

        // Attempt to retrieve stored credentials
        Login login = new Login(mSharedPreferences.getString(Preferences.SCHOOL_LOOP_USERNAME, ""), mSharedPreferences.getString(
                Preferences.SCHOOL_LOOP_PASSWORD, ""));

        // Setup WebView
        mWebView = new WebView(getActivity().getApplicationContext());
        ((LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_webview_screen)).addView(mWebView);
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
            new SchoolLoopObservableLogin().loginToSchoolLoop(login);
        }
        return schoolLoopScreen;
    }

    void submitLogin(View v) {
        // Hide keyboard
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
            new SchoolLoopObservableLogin().loginToSchoolLoop(newLogin);
        }
    }

    void transitionViews(View one, View two) {
        // Attempt Login
        one.setVisibility(View.GONE);
        one.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_out));
        two.setVisibility(View.VISIBLE);
        two.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in));
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

    private void noConnectionScreen() {
        ((RelativeLayout) schoolLoopScreen.findViewById(R.id.school_loop_loading)).setVisibility(View.GONE);
        TextView errorText = (TextView) schoolLoopScreen.findViewById(R.id.school_loop_error);
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(getString(R.string.cdm_no_connection));
    }

    void updateCredentials(String username, String password, long time) {
        SharedPreferences.Editor sharedPrefEditor = mSharedPreferences.edit();
        if (username != null)
            sharedPrefEditor.putString(Preferences.SCHOOL_LOOP_USERNAME, username);
        if (password != null)
            sharedPrefEditor.putString(Preferences.SCHOOL_LOOP_PASSWORD, password);
        if (time >= 0)
            sharedPrefEditor.putLong(Preferences.SCHOOL_LOOP_TIME, time);
        sharedPrefEditor.commit();
    }
//SchoolLoopObservableLogin Class ends here

    public static class Entry extends NavDrawerEntry {

        @Override
        public String getTitle() {
            return "School Loop";
        }

        @Override
        public EntryType getType() {
            return EntryType.FRAGMENT;
        }

        @Override
        public EntryStyle getStyle() {
            return EntryStyle.NORMAL;
        }

        @Override
        public int getIcon() {
            return 0;
        }

        @Override
        public Class<? extends ExtendedFragment> getFragmentType() {
            return SchoolLoopFragment.class;
        }

        @Override
        public void runAction(Activity activity) {
            // None
        }
    }

    class Login {
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
    } //Login Class ends here

    class SchoolLoopObservableLogin {

        @SuppressLint("NewApi")
        private void loadWebView() {
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setWebViewClient(new SchoolLoopWebViewClient());
            mWebView.loadUrl(SchoolLoopAPI.BASE_URL_SECURE + "/mobile/index");
            mWebView.getSettings().setSupportZoom(true);
            mWebView.getSettings().setBuiltInZoomControls(true);
            if (android.os.Build.VERSION.SDK_INT >= 11)
                mWebView.getSettings().setDisplayZoomControls(false);
            mWebView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
        }

        private Observable<Boolean> schoolLoopLoggedIn(final Login login) {
            return Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> booleanSubscriber) {
                    long lastActiveTime = mSharedPreferences.getLong(Preferences.SCHOOL_LOOP_TIME, 0);
                    SharedPreferences.Editor sharedPrefEditor = mSharedPreferences.edit();
                    // Calculated as still logged in. Timeout is around 15 min
                    if (lastActiveTime != 0 && (System.currentTimeMillis() - lastActiveTime) < 900000) {
                        SchoolLoopAPI.Dirty.loadLoginDataToWebView(mSharedPreferences, mWebView);
                        booleanSubscriber.onNext(true);
                    } else { // Not logged in
                        try {
                            CookieStore loginCookieStore = SchoolLoopAPI.loginToSchoolloop(new DefaultHttpClient(), login.user, login.pass, true);
                            if (loginCookieStore != null) {
                                SchoolLoopAPI.Dirty.migrateCookieStore2WebView(loginCookieStore, mWebView, sharedPrefEditor);
                                updateCredentials(newCredentials ? login.user : null, newCredentials ? login.pass : null, System.currentTimeMillis());
                                booleanSubscriber.onNext(true);

                            } else {
                                booleanSubscriber.onNext(false);
                            }
                        } catch (ClientProtocolException e) {
                            booleanSubscriber.onError(e);
                        } catch (IOException e) {
                            booleanSubscriber.onError(e);
                        } catch (Exception e) {
                            booleanSubscriber.onError(e);
                        }
                    }
                }
            });
        }

        public void loginToSchoolLoop(Login login) {

            Subscriber suscriber = new Subscriber<Boolean>() {
                @Override
                public void onNext(Boolean success) {
                    if (success != null) {
                        if (success) {
                            loadWebView();
                            LinearLayout webViewScreen = (LinearLayout) schoolLoopScreen.findViewById(R.id.school_loop_webview_screen);
                            webViewScreen.setVisibility(View.VISIBLE);
                        } else {
                            loginScreenWithErrorMessage("Bad Username or Password");
                        }
                    } else {
                        // Test Internet connection
                        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                        if (!isConnected)
                            noConnectionScreen();
                        else
                            loginScreenWithErrorMessage("Error Logging In");
                    }
                }

                @Override
                public void onCompleted() {
                    Log.d(SchoolLoopFragment.TAG, "Finished with observerable task");
                    //Since a single element observer is subscribing, code should be handled on the onNext
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(SchoolLoopFragment.TAG, e.getMessage());
                    //called if the any results are null
                }
            };

            Subscription s = schoolLoopLoggedIn(login).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(suscriber);
        }


    }
    //SchoolLoopWebview Client ends here

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

}
