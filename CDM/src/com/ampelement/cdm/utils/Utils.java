package com.ampelement.cdm.utils;

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
	 */
	public static String combine(String[] arr, String c) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			sb.append(arr[i]).append(c);
		}
		sb.delete(sb.length() - c.length(), sb.length());
		return sb.toString();
	}

}
