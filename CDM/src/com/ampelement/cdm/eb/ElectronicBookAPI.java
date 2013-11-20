package com.ampelement.cdm.eb;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.BufferOverflowException;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ElectronicBookAPI {

	public static final int ERROR = -10;
	public static final int ERROR_NETWORK = -11;

	static final String API_BASE = "http://ampelement.com/cdm/eb/";
	static final String API_EB_VERSION = API_BASE + "eb_list_version.php";
	static final String API_EB_FILE = API_BASE + "eb_list.php";

	static final String PREF_KEY_EB_VERSION = "eb_version";

	Context mContext;
	DefaultHttpClient mHttpClient = new DefaultHttpClient();
	SharedPreferences mSharedPref;
	SharedPreferences.Editor mSharedPrefEditor;

	public ElectronicBookAPI(Context context) {
		mContext = context;
		mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		mSharedPrefEditor = mSharedPref.edit();
	}

	public boolean updateEBData() {
		int curInstalledVersion = mSharedPref.getInt(PREF_KEY_EB_VERSION, 0);
		int latestVersion = getLatestVersion();
		if (latestVersion > ERROR) {
			if (latestVersion > curInstalledVersion) {

			}
		}
		return false;
	}

	public int getLatestVersion() {
		HttpGet ebVerRequest = new HttpGet(API_EB_VERSION);
		try {
			HttpResponse ebVerResponse = mHttpClient.execute(ebVerRequest);
			String ebVer = EntityUtils.toString(ebVerResponse.getEntity());
			if (ebVer != null && ebVer.matches("\\d+")) {
				return Integer.parseInt(ebVer);
			}
		} catch (ClientProtocolException e) {
			return ERROR;
		} catch (IOException e) {
			return ERROR;
		}
		return ERROR;
	}

	public void downloadLatestVersion(int versionCode) {
		gzipUrlToFile(API_EB_FILE + "?v=" + c2s(versionCode), ElectronicBookDB.EB_FILE_PATH);
	}

	public static boolean gzipUrlToFile(String gzipURL, String outFilePath) {
		try {
			URL url = new URL(gzipURL);
			URLConnection connection = url.openConnection();
			InputStream stream = connection.getInputStream();
			stream = new GZIPInputStream(stream);
			InputSource is = new InputSource(stream);
			InputStream input = new BufferedInputStream(is.getByteStream());
			OutputStream output = new FileOutputStream(outFilePath);
			byte data[] = new byte[2097152];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;
				output.write(data, 0, count);
			}
			output.flush();
			output.close();
			input.close();
			return true;
		} catch (BufferOverflowException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	static String c2s(int vc) {
		String vcs = String.valueOf(vc);
		if (vcs.length() < 2)
			vcs = "0" + vcs;
		return vcs;
	}

}
