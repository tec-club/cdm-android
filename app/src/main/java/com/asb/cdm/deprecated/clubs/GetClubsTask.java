package com.asb.cdm.deprecated.clubs;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.asb.cdm.Preferences;
import com.asb.cdm.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
            URL url = new URL("http://alexwendland.com/cdm/clubs"); //TODO Update all old urls to new ones that direct towards http://cdmtecclub.com/
            //URL seems like a 404 error
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			String response = Utils.convertStreamToString(in, "UTF-8");

			// Handle club data
			JSONObject json = new JSONObject(response);
			int cachedClubDataVersion = mSharedPref.getInt(Preferences.CLUB_CACHED_DATA_VERSION, -1);
			int jsonClubDataVersion = json.getInt("version");
			if (jsonClubDataVersion > cachedClubDataVersion) {
                /*
                If newer version, then update the sharedPreferences with new CLUB_CACHED_DATA values
                 */
                SharedPreferences.Editor edit = mSharedPref.edit();
				edit.putInt(Preferences.CLUB_CACHED_DATA_VERSION, jsonClubDataVersion);
				edit.putString(Preferences.CLUB_CACHED_DATA, response);
				edit.commit();
                return parseClubData(json); //At this line, before returning, it will go to the finally-statement
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

    /**
     * Called after executing
     *
     * @param clubData The array of clubData, null value means its done
     */
    @Override
	protected void onPostExecute(ClubData[] clubData) {
		if (mOnUpdateComplete != null)
			mOnUpdateComplete.onComplete(clubData);
		mIsFinished = true;
	}

	public boolean isFinished() {
		return mIsFinished;
	}

	public void setOnUpdateCompleteListener(OnUpdateComplete onUpdateComplete) {
		mOnUpdateComplete = onUpdateComplete;
	}

	public static ClubData[] parseClubData(String json) {
		try {
			return parseClubData(new JSONObject(json));
		} catch (Exception e) {
			return null;
        }
    }

    /**Creates a JSONArray object from the param j
     * and then traverses the array and creates
     * clubData objects from JSONObjects in the JSONArray
     *
     * @param j The JSONObject that contains the string of club data from the URL
     * @return the array of ClubData objects
     */
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
					club.description = jClub.getString("description");
					/*
					 * club.logoURL = jClub.getString("logo"); JSONArray
					 * meetingTimes = jClub.getJSONArray("meetingTimes");
					 * club.meetingTimes = new String[meetingTimes.length()];
					 * for (int k = 0; k < meetingTimes.length(); k++) {
					 * club.meetingTimes[k] = meetingTimes.getString(k); }
					 */
					club.president = jClub.getString("president");
					club.advisor = jClub.getString("advisor");

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
