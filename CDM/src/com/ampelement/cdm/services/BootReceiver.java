package com.ampelement.cdm.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		scheduleServices(context);
	}

	public static void scheduleServices(Context context) {
		long firstTime = SystemClock.elapsedRealtime() + (5 * 60 * 1000);

		PendingIntent alarmSenderAssignmentPoller = PendingIntent.getService(context, 0, new Intent(context, Update_Service.class), 0);
		// Schedule the alarm!
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		long assignmentUpdateInterval = 30 * 60 * 1000;
		am.setRepeating(AlarmManager.ELAPSED_REALTIME, firstTime, assignmentUpdateInterval, alarmSenderAssignmentPoller);
		Log.i("Boot", "Update_Service Started with intervals at " + String.valueOf(assignmentUpdateInterval));
	}
}
