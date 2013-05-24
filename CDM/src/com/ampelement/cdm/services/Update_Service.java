package com.ampelement.cdm.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.ampelement.cdm.CDMActivity;
import com.ampelement.cdm.R;

/**
 * This is an example of implementing an application service that will run in
 * response to an alarm, allowing us to move long duration work out of an intent
 * receiver.
 * 
 * @see AlarmService
 * @see AlarmService_Alarm
 */
public class Update_Service extends Service {

	NotificationManager mNM;

	private static final String TAG = "Assignment_Polling_Service";
	private static final int notificationID = 456;

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the
		// process's
		// main thread, which we don't want to block.
		Thread thr = new Thread(null, mTask, "Update_Service");
		thr.start();

	}

	@Override
	public void onDestroy() {
		// Cancel the notification -- we use the same ID that we had used to
		// start it
		// mNM.cancel(R.string.app_name);

		// Tell the user we stopped.
		/*
		 * if (toastMessageOnStop.length() > 0) { Toast.makeText(this,
		 * toastMessageOnStop, Toast.LENGTH_LONG).show(); }
		 */
	}

	/**
	 * The function that runs in our worker thread
	 */
	Runnable mTask = new Runnable() {
		public void run() {
		}
	};

	private void showNotification(String title, String more) {

		Notification notification = new Notification(R.drawable.ic_launcher_64, more, System.currentTimeMillis());
		// PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new
		// Intent(), 0);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, CDMActivity.class), 0);
		notification.setLatestEventInfo(this, title, more, contentIntent);
		mNM.notify(R.string.app_name + notificationID + title.hashCode(), notification);
	}

	private void cancelNotification(String title) {
		mNM.cancel(R.string.app_name + notificationID + title.hashCode());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
	 * This is the object that receives interactions from clients. See
	 * RemoteService for a more complete example.
	 */
	private final IBinder mBinder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		}
	};
}
