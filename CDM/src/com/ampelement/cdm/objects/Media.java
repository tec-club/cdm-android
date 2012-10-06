package com.ampelement.cdm.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.ampelement.cdm.utils.DatabaseHandler;

public class Media {

	public static final String TYPE_IMAGE = "image";
	public static final String TYPE_VIDEO = "video";
	
	public static final String THUMBNAIL_NONE = "noThumbnail";
	
	public static final String FROM_TEST_BLOG = "fromTestBlog";

	public static final String OTHER_NONE = "";

	// private variables
	public int id;
	public String name;
	public String description;
	public double showDate;
	public String category;
	public double addedDate;
	public double length;
	public String viewStatus;
	public String link;
	public String path;
	public String thumbnailPath;
	public String type;
	public String fromWhere;
	public String otherData;

	public Media() {

	}

	public Media(String _name, String _description, double _showDate, String _category, double _addedDate, double _length, String _viewStatus, String _link, String _path, String _thumbnailPath, String _type, String _fromWhere, String _otherData) {
		this.name = _name;
		this.description = _description;
		this.showDate = _showDate;
		this.category = _category;
		this.addedDate = _addedDate;
		this.length = _length;
		this.viewStatus = _viewStatus;
		this.link = _link;
		this.path = _path;
		this.thumbnailPath = _thumbnailPath;
		this.type = _type;
		this.fromWhere = _fromWhere;
		this.otherData = _otherData;
	}

	public Media(int _id, String _name, String _description, double _showDate, String _category, double _addedDate, double _length, String _viewStatus, String _link, String _path, String _thumbnailPath, String _type, String _fromWhere, String _otherData) {
		this(_name, _description, _showDate, _category, _addedDate, _length, _viewStatus, _link, _path, _thumbnailPath, _type, _fromWhere, _otherData);
		this.id = _id;
	}

	public Media(Cursor cursor) {
		this(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), Double.parseDouble(cursor.getString(3)), cursor.getString(4), Double.parseDouble(cursor.getString(5)), Double.parseDouble(cursor.getString(6)), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13));
	}
	
public static ContentValues buildContentValues(Media media) {
		
		ContentValues values = new ContentValues();
		values.put(DatabaseHandler.MEDIA_KEY_NAME, media.name);
		values.put(DatabaseHandler.MEDIA_KEY_DESCRIPTION, media.description);
		values.put(DatabaseHandler.MEDIA_KEY_SHOW_DATE, media.showDate);
		values.put(DatabaseHandler.MEDIA_KEY_CATEGORY, media.category);
		values.put(DatabaseHandler.MEDIA_KEY_ADDED_DATE, media.addedDate);
		values.put(DatabaseHandler.MEDIA_KEY_LENGTH, media.length);
		values.put(DatabaseHandler.MEDIA_KEY_VIEW_STATUS, media.viewStatus);
		values.put(DatabaseHandler.MEDIA_KEY_LINK, media.link);
		values.put(DatabaseHandler.MEDIA_KEY_PATH, media.path);
		values.put(DatabaseHandler.MEDIA_KEY_THUMBNAIL_PATH, media.thumbnailPath);
		values.put(DatabaseHandler.MEDIA_KEY_TYPE, media.type);
		values.put(DatabaseHandler.MEDIA_KEY_FROM_WHERE, media.fromWhere);
		values.put(DatabaseHandler.MEDIA_KEY_OTHER_DATA, media.otherData);
		
		return values;
	}

}
