package com.asb.cdm.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class Utils {

	/**
	 * Read an InputStream to a String
	 * 
	 * @param is
	 *            InputStream to read from
	 * @param enc
	 *            Character Encoding to use, eg "UTF-8"
	 * @return A String of the InputStream data
	 */
	public static String convertStreamToString(java.io.InputStream is, String enc) {
		java.util.Scanner s = new java.util.Scanner(is, enc).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	/**
	 * Combines a String array using a provided delimiter.
	 * 
	 * @param arr
	 *            String[] to combine
	 * @param c
	 *            Delimiter to use
	 * @return a single String
     *
     * For example if String arr[]= {"5", "4", "3"}
     * then a call to combine(arr, "+") would return
     * a string "5+4+3"
     *
     */
    public static String combine(String[] arr, String c) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			sb.append(arr[i]).append(c);
		}
		sb.delete(sb.length() - c.length(), sb.length());
		return sb.toString();
	}

	/**
	 * Open an InputStream of the supplied URL and read the response into a
     * String object. Used for HTTP GET Requests, since further socket interactions
     * are restricted.
     *
     * Can be used to return a string of an html page to be parsed later on
     *
     * @param url
	 *            URL to fetch
	 * @return String response from the URL
	 */
	public static String getURL(URL url) {
		StringBuilder response = new StringBuilder();

		InputStream is = null;
		BufferedReader br = null;
		try {
			is = url.openStream();
			br = new BufferedReader(new InputStreamReader(is));

			String line;
			while ((line = br.readLine()) != null)
				response.append(line);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (is != null)
					is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return response.toString();
	}

}
