package com.ampelement.cdm.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * This is an example of implementing an application service that will run in
 * response to an alarm, allowing us to move long duration work out of an intent
 * receiver.
 * 
 * @see AlarmService
 * @see AlarmService_Alarm
 */
public class Update_Service extends Service {
	private static final String TAG = "UpdateService";

	@Override
	public void onCreate() {
		Thread thr = new Thread(null, mTask, "EB_Update_Service");
		thr.start();

	}

	@Override
	public void onDestroy() {
	}

	/**
	 * The function that runs in our worker thread
	 */
	Runnable mTask = new Runnable() {
		public void run() {
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
	 * This is the object that receives interactions from clients. See
	 * RemoteService for a more complete example.
	 */
	private static final IBinder mBinder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		}
	};
}
