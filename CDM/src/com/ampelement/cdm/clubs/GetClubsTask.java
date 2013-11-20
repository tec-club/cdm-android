package com.ampelement.cdm.clubs;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.ampelement.cdm.Preferences;
import com.ampelement.cdm.utils.Utils;

public class GetClubsTask extends AsyncTask<Void, Void, ClubData[]> {
	int mCachedClubDataVersion;
	SharedPreferences mSharedPref;
	OnUpdateComplete mOnUpdateComplete;
	private boolean mIsFinished = false;

	public interface OnUpdateComplete {
		/**
		 * Triggers when the {@link GetClubsTask} finishes
		 * {@link GetClubsTask#doInBackground(Void...)}.
		 * 
		 * @param clubData
		 *            will equal null if the data retrieved is not newer than
		 *            the cached data. Otherwise, will be an array of ClubData
		 *            with the new info.
		 */
		public void onComplete(ClubData[] clubData);
	}

	public GetClubsTask(int cachedClubDataVersion, SharedPreferences sharedPref, OnUpdateComplete onUpdateComplete) {
		super();
		mCachedClubDataVersion = cachedClubDataVersion;
		mSharedPref = sharedPref;
		mOnUpdateComplete = onUpdateComplete;
	}

	@Override
	protected ClubData[] doInBackground(Void... voids) {
		HttpURLConnection urlConnection = null;
		try {
			// Get latest club data
			URL url = new URL("http://alexwendland.com/cdm/clubs");
			urlConnection = (HttpURLConnection) url.openConnection();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			String response = Utils.convertStreamToString(in, "UTF-8");

			// Handle club data
			JSONObject json = new JSONObject(response);
			int cachedClubDataVersion = mSharedPref.getInt(Preferences.CLUB_CACHED_DATA_VERSION, -1);
			int jsonClubDataVersion = json.getInt("version");
			if (jsonClubDataVersion > cachedClubDataVersion) {
				SharedPreferences.Editor edit = mSharedPref.edit();
				edit.putInt(Preferences.CLUB_CACHED_DATA_VERSION, jsonClubDataVersion);
				edit.putString(Preferences.CLUB_CACHED_DATA, response);
				edit.commit();
				return parseClubData(json);
			}

		} catch (MalformedURLException e) {
			Log.e(ClubsFragment.TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(ClubsFragment.TAG, e.getMessage());
		} catch (JSONException e) {
			Log.e(ClubsFragment.TAG, e.getMessage());
		} finally {
			urlConnection.disconnect();
		}
		return null;
	}

	@Override
	protected void onPostExecute(ClubData[] clubData) {
		if (mOnUpdateComplete != null)
			mOnUpdateComplete.onComplete(clubData);
		mIsFinished = true;
	}

	public boolean isFinished() {
		return mIsFinished;
	}

	public static ClubData[] parseClubData(String json) {
		try {
			return parseClubData(new JSONObject(json));
		} catch (Exception e) {
			return null;
		}
	}

	public static ClubData[] parseClubData(JSONObject j) {
		ClubData[] clubs = null;

		try {
			JSONArray jsonClubsArr = j.getJSONArray("clubs");
			clubs = new ClubData[jsonClubsArr.length()];
			for (int i = 0; i < jsonClubsArr.length(); i++) {
				try {
					ClubData club = new ClubData();
					JSONObject jClub = jsonClubsArr.getJSONObject(i);

					club.name = jClub.getString("name");
					club.description = jClub.getString("desc");
					club.logoURL = jClub.getString("logo");
					JSONArray meetingTimes = jClub.getJSONArray("meetingTimes");
					club.meetingTimes = new String[meetingTimes.length()];
					for (int k = 0; k < meetingTimes.length(); k++) {
						club.meetingTimes[k] = meetingTimes.getString(k);
					}

					clubs[i] = club;
				} catch (Exception e) {
					Log.e(ClubsFragment.TAG, e.getMessage());
				}
			}

		} catch (JSONException e) {
			Log.e(ClubsFragment.TAG, e.getMessage());
			return null;
		}

		return clubs;
	}
}
