package com.ampelement.cdm.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.util.ByteArrayBuffer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ampelement.cdm.CDMActivity;
import com.ampelement.cdm.utils.DatabaseHandler;
import com.ampelement.cdm.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;

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

			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

				try {
					URL u = new URL("http://cdmampelement.blogspot.com/feeds/posts/default?alt=rss");

					try {
						Document doc = builder.parse(u.openStream());
						NodeList items = doc.getElementsByTagName("item");
						for (int i = 0; i < items.getLength(); i++) {
							try {
								String title = "";
								String imageSrc = "";
								String description = "";
								String thumbnailSrc = "";
								Element element = (Element) items.item(i);
								NodeList titleNodes = element.getElementsByTagName("title");
								if (titleNodes.getLength() > 0) {
									Element titleElement = (Element) titleNodes.item(0);
									if (titleElement.getChildNodes().getLength() > 0) {
										title = titleElement.getTextContent();

										if (title.contains("%cancel")) {
											cancelNotification(title.split("%cancel")[0]);
										} else {
											NodeList descriptionNodeList = element.getElementsByTagName("description");
											if (descriptionNodeList.getLength() > 0) {
												Element descriptionNode = (Element) descriptionNodeList.item(0);
												String descString = descriptionNode.getTextContent();
												String more = Jsoup.clean(descString, Whitelist.simpleText());
												showNotification(title, more);
											}
										}
									}
								}

							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	private void showNotification(String title, String more) {

		Notification notification = new Notification(R.drawable.ic_launcher_64, more, System.currentTimeMillis());
		// PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new
		// Intent(), 0);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, CDMActivity.class), 0);
		notification.setLatestEventInfo(this, title, more, contentIntent);
		mNM.notify(R.string.app_name + notificationID + title.hashCode(), notification);
		Log.i(TAG, "Displaying Notification: \"" + title + "\" with \"" + more + "\"");
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

	public static void DownloadFromUrl(String imageURL, String fileName, Context context) throws IOException {
		URL url = new URL(imageURL);
		File file = new File(fileName);

		long startTime = System.currentTimeMillis();
		Log.d(TAG, "download begining");
		Log.d(TAG, "download url:" + url);
		Log.d(TAG, "downloaded file name:" + fileName);
		/* Open a connection to that URL. */
		URLConnection ucon = url.openConnection();

		/*
		 * Define InputStreams to read from the URLConnection.
		 */
		InputStream is = ucon.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);

		/*
		 * Read bytes to the Buffer until there is nothing more to read(-1).
		 */
		ByteArrayBuffer baf = new ByteArrayBuffer(50);
		int current = 0;
		while ((current = bis.read()) != -1) {
			baf.append((byte) current);
		}

		/* Convert the Bytes read to a String. */
		FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
		fos.write(baf.toByteArray());
		fos.close();
		Log.d(TAG, "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
	}
}
