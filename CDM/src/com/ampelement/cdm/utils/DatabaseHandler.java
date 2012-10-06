package com.ampelement.cdm.utils;

import java.util.ArrayList;
import java.util.List;

import com.ampelement.cdm.objects.Media;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

public class DatabaseHandler extends SQLiteOpenHelper {
	// Table name variables
	private static final String TABLE_MEDIA = "media";
	// Column variables for Assignment table
	public static final String MEDIA_KEY_ID = "id";
	public static final String MEDIA_KEY_NAME = "name";
	public static final String MEDIA_KEY_DESCRIPTION = "description";
	public static final String MEDIA_KEY_SHOW_DATE = "show_date";
	public static final String MEDIA_KEY_CATEGORY = "category";
	public static final String MEDIA_KEY_ADDED_DATE = "added_date";
	public static final String MEDIA_KEY_LENGTH = "length";
	public static final String MEDIA_KEY_VIEW_STATUS = "view_status";
	public static final String MEDIA_KEY_LINK = "link";
	public static final String MEDIA_KEY_PATH = "path";
	public static final String MEDIA_KEY_THUMBNAIL_PATH = "thumbnailPath";
	public static final String MEDIA_KEY_TYPE = "type";
	public static final String MEDIA_KEY_FROM_WHERE = "fromWhere";
	public static final String MEDIA_KEY_OTHER_DATA = "otherData";
	// MEDIA query string
	private static final String[] MEDIA_QUERY = { MEDIA_KEY_ID, MEDIA_KEY_NAME, MEDIA_KEY_DESCRIPTION, MEDIA_KEY_SHOW_DATE, MEDIA_KEY_CATEGORY, MEDIA_KEY_ADDED_DATE, MEDIA_KEY_LENGTH, MEDIA_KEY_VIEW_STATUS, MEDIA_KEY_LINK, MEDIA_KEY_PATH, MEDIA_KEY_THUMBNAIL_PATH, MEDIA_KEY_TYPE, MEDIA_KEY_FROM_WHERE, MEDIA_KEY_OTHER_DATA };
	
	// Database Version
	public static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "mediaDataBase.db";

	// SharedPreferences for storing DataBase Version
	private SharedPreferences sharedPreferences;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_MEDIA_TABLE = "CREATE TABLE " + TABLE_MEDIA + "( " + MEDIA_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MEDIA_KEY_NAME + " TEXT, " + MEDIA_KEY_DESCRIPTION + " TEXT, " + MEDIA_KEY_SHOW_DATE + " REAL, " + MEDIA_KEY_CATEGORY + " TEXT, " + MEDIA_KEY_ADDED_DATE + " REAL, " + MEDIA_KEY_LENGTH + " REAL, " + MEDIA_KEY_VIEW_STATUS + " TEXT, " + MEDIA_KEY_LINK + " TEXT, " + MEDIA_KEY_PATH + " TEXT, " + MEDIA_KEY_THUMBNAIL_PATH + " TEXT, " + MEDIA_KEY_TYPE + " TEXT, " + MEDIA_KEY_FROM_WHERE + " TEXT, " + MEDIA_KEY_OTHER_DATA + " TEXT" + ");";
		db.execSQL(CREATE_MEDIA_TABLE);
		sharedPreferences.edit().putInt("db_version", DATABASE_VERSION).commit();
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA);

		// Create tables again
		onCreate(db);
		// TODO onUpgrade for easy transitions
	}

	public void addMedia(Media media) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = Media.buildContentValues(media);
		// Inserting Row
		long returnValue = db.insert(TABLE_MEDIA, null, values);
		db.close(); // Closing database connection
	}

	public Media getMediaByID(int id) {
		SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.query(TABLE_MEDIA, MEDIA_QUERY, MEDIA_KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Media media = new Media(cursor);
		// return contact
		return media;
	}

	public List<Media> getMediasByMatch(String _whereMEDIA_KEY, String _match) {
		List<Media> mediaList = new ArrayList<Media>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_MEDIA + " WHERE " + _whereMEDIA_KEY + "='" + _match + "'";

		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Media media = new Media(cursor);
				// Adding contact to list
				mediaList.add(media);
			} while (cursor.moveToNext());
		}

		// return contact list
		return mediaList;
	}

	public List<Media> getMediasBy2Matches(String _whereMEDIA_KEY1, String _match1, String _whereMEDIA_KEY2, String _match2) {
		List<Media> mediaList = new ArrayList<Media>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_MEDIA + " WHERE " + _whereMEDIA_KEY1 + "='" + _match1 + "' AND " + _whereMEDIA_KEY2 + "='" + _match2 + "'";

		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Media media = new Media(cursor);
				// Adding contact to list
				mediaList.add(media);
			} while (cursor.moveToNext());
		}

		// return contact list
		return mediaList;
	}

	public List<Media> getAllMedias() {
		List<Media> mediaList = new ArrayList<Media>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_MEDIA;

		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Media media = new Media(cursor);
				// Adding contact to list
				mediaList.add(media);
			} while (cursor.moveToNext());
		}

		// return contact list
		return mediaList;
	}

	public List<Media> getAllMediasOrderedByColumn(String _orderColumn) {
		List<Media> mediaList = new ArrayList<Media>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_MEDIA + " ORDER BY " + _orderColumn;

		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Media media = new Media(cursor);
				// Adding contact to list
				mediaList.add(media);
			} while (cursor.moveToNext());
		}

		// return contact list
		return mediaList;
	}

	public List<Media> getMediasByMatchAndOrderedByColumn(String _whereMEDIA_KEY, String _match, String _orderColumn) {
		List<Media> mediaList = new ArrayList<Media>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_MEDIA + " WHERE " + _whereMEDIA_KEY + "='" + _match + "' ORDER BY " + _orderColumn;

		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Media media = new Media(cursor);
				// Adding contact to list
				mediaList.add(media);
			} while (cursor.moveToNext());
		}

		// return contact list
		return mediaList;
	}

	public int getMediasCount() {
		String countQuery = "SELECT  * FROM " + TABLE_MEDIA;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}

	public int updateMedia(Media media) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = Media.buildContentValues(media);
		// updating row
		return db.update(TABLE_MEDIA, values, MEDIA_KEY_ID + " = ?", new String[] { String.valueOf(media.id) });
	}

	public void deleteMedia(Media media) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_MEDIA, MEDIA_KEY_ID + " = ?", new String[] { String.valueOf(media.id) });
		db.close();
	}
}
