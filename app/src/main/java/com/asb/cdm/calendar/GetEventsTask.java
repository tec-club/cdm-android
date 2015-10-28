package com.asb.cdm.calendar;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.asb.cdm.Preferences;
import com.asb.cdm.schoolloop.SchoolLoopAPI.EventFetcher;
import com.asb.cdm.schoolloop.SchoolLoopEventMap;

public class GetEventsTask extends AsyncTask<Void, String, SchoolLoopEventMap> {

    public final String TAG = this.getClass().getCanonicalName();

	SharedPreferences mSharedPref;
	OnUpdateComplete mOnUpdateComplete;
	private boolean mIsFinished = false;
	private String mPrevXMLData;

	public interface OnUpdateComplete {
		/**
		 * Triggers when the {@link GetEventsTask} finishes
		 * {@link GetEventsTask#doInBackground(Void...)}.
		 * 
		 * @param eventsMap
		 *            will equal null if the data retrieved is no different than
		 *            the cached data. Otherwise, will be a
		 *            {@link SchoolLoopEventMap} of the new data.
		 */
		public void onComplete(SchoolLoopEventMap eventsMap);
	}

	public GetEventsTask(SharedPreferences sharedPref, String previousXML, OnUpdateComplete onUpdateComplete) {
		mSharedPref = sharedPref;
		mPrevXMLData = previousXML;
		mOnUpdateComplete = onUpdateComplete;
	}

	@Override
	protected SchoolLoopEventMap doInBackground(Void... params) {
		EventFetcher eventFetcher = new EventFetcher();
        try {
            String latestXMLData = eventFetcher.fetchEventXML();
            if (mPrevXMLData != null && mPrevXMLData.equals(latestXMLData))
                return eventFetcher.loadEvents(mPrevXMLData);
            else {
                SharedPreferences.Editor edit = mSharedPref.edit();
                edit.putString(Preferences.CALENDAR_CACHED_DATA, latestXMLData);
                edit.apply();
                return eventFetcher.loadEvents(latestXMLData);
            }
        } catch (Exception e) {
            Log.d(TAG, "Parsing exception.\n" + e.getMessage());
        }
		return null;
	}

	@Override
	protected void onPostExecute(SchoolLoopEventMap result) {
		if (mOnUpdateComplete != null)
			mOnUpdateComplete.onComplete(result);
		mIsFinished = true;
	}

	public boolean isFinished() {
		return mIsFinished;
	}

	public void setOnUpdateCompleteListener(OnUpdateComplete onUpdateComplete) {
		mOnUpdateComplete = onUpdateComplete;
	}
}
