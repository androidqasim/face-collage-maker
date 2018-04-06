package dauroi.photoeditor.database.table;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import dauroi.photoeditor.api.response.StoreItem;

/**
 * Created by Wisekey on 3/18/2015.
 */
public class StoreItemTable extends BaseTable {
	// Database structure
	public static final String TABLE_NAME = "StoreItem";

	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_THUMBNAIL = "thumbnail";
	public static final String COLUMN_SELECTED_THUMBNAIL = "selected_thumbnail";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_URL = "url";
	public static final String COLUMN_SIGNATURE = "signature";
	// Database creation sql statement
	private static final String SQL_DATABASE_CREATE = "create table " + TABLE_NAME + "(" + COLUMN_ID + " TEXT, "
			+ COLUMN_NAME + " text," + COLUMN_THUMBNAIL + " text,"
			+ COLUMN_SELECTED_THUMBNAIL + " text,"
			+ COLUMN_TYPE + " text," + COLUMN_SIGNATURE
			+ " text," + COLUMN_URL + " text," + COLUMN_LAST_MODIFIED + " text," + COLUMN_STATUS + " text" + ");";

	public static void createTable(SQLiteDatabase database) {
		database.execSQL(SQL_DATABASE_CREATE);
	}

	public static void upgradeTable(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	public StoreItemTable(Context context) {
		super(context);
	}

	public long insertOrUpdate(StoreItem info) {
		StoreItem item = getRowWithStoreId(info.getIdString());
		if (item == null) {
			return insert(info);
		} else {
			return update(info);
		}
	}

	public long insert(StoreItem info) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_ID, info.getIdString());
		values.put(COLUMN_NAME, info.getTitle());
		values.put(COLUMN_THUMBNAIL, info.getThumbnail());
		values.put(COLUMN_SELECTED_THUMBNAIL, info.getSelectedThumbnail());
		values.put(COLUMN_TYPE, info.getType());
		values.put(COLUMN_URL, info.getUrl());
		values.put(COLUMN_SIGNATURE, info.getSignature());
		if (info.getLastModified() == null || info.getLastModified().length() < 1) {
			info.setLastModified(getCurrentDateTime());
		}
		values.put(COLUMN_LAST_MODIFIED, info.getLastModified());
		if (info.getStatus() == null || info.getStatus().length() < 1) {
			info.setStatus(STATUS_ACTIVE);
		}

		values.put(COLUMN_STATUS, info.getStatus());

		long id = getDatabase().insert(TABLE_NAME, null, values);
		info.setId(id);
		return id;
	}

	public int update(StoreItem info) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_ID, info.getIdString());
		values.put(COLUMN_NAME, info.getTitle());
		values.put(COLUMN_THUMBNAIL, info.getThumbnail());
		values.put(COLUMN_SELECTED_THUMBNAIL, info.getSelectedThumbnail());
		values.put(COLUMN_TYPE, info.getType());
		values.put(COLUMN_LAST_MODIFIED, getCurrentDateTime());
		values.put(COLUMN_URL, info.getUrl());
		values.put(COLUMN_SIGNATURE, info.getSignature());
		if (info.getStatus() == null || info.getStatus().length() < 1) {
			info.setStatus(STATUS_ACTIVE);
		}

		values.put(COLUMN_STATUS, info.getStatus());

		return getDatabase().update(TABLE_NAME, values, COLUMN_ID + " = ?",
				new String[] { String.valueOf(info.getId()) });
	}

	public boolean hasItem(String id, boolean active) {
		String selectString = "SELECT " + COLUMN_ID + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " =?";
		if (active) {
			selectString = selectString.concat(" AND ").concat(COLUMN_STATUS).concat(" =?");
		}
		// Add the String you are searching by here.
		// Put it in an array to avoid an unrecognized token error
		Cursor cursor = null;
		if (active) {
			cursor = getDatabase().rawQuery(selectString, new String[] { id, STATUS_ACTIVE });
		} else {
			cursor = getDatabase().rawQuery(selectString, new String[] { id });
		}

		boolean hasObject = false;
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				hasObject = true;
			}

			cursor.close();
		}

		return hasObject;
	}

	public StoreItem getRowWithName(String name) {
		String[] selColumns = null;
		String selection = COLUMN_STATUS + " = ? AND UPPER(" + COLUMN_NAME + ") = UPPER(?)";
		String[] selectionArgs = { STATUS_ACTIVE, name };

		String groupBy = null;
		String having = null;
		String orderBy = null;

		Cursor filesCursor = getDatabase().query(TABLE_NAME, selColumns, selection, selectionArgs, groupBy, having,
				orderBy);
		if (filesCursor == null)
			return null;

		List<StoreItem> files = toList(filesCursor);
		filesCursor.close();
		if (files.size() > 0) {
			return files.get(0);
		} else {
			return null;
		}
	}

	public StoreItem getRowWithStoreId(String idStr) {
		String[] selColumns = null;
		String selection = COLUMN_STATUS + " = ? AND " + COLUMN_ID + " = ?";
		String[] selectionArgs = { STATUS_ACTIVE, idStr };

		String groupBy = null;
		String having = null;
		String orderBy = null;

		Cursor filesCursor = getDatabase().query(TABLE_NAME, selColumns, selection, selectionArgs, groupBy, having,
				orderBy);
		if (filesCursor == null)
			return null;

		List<StoreItem> files = toList(filesCursor);
		filesCursor.close();
		if (files.size() > 0) {
			return files.get(0);
		} else {
			return null;
		}
	}

	public List<StoreItem> getAllRows() {
		String[] selColumns = null;
		String selection = COLUMN_STATUS + " = ? ";
		String[] selectionArgs = { STATUS_ACTIVE };

		String groupBy = null;
		String having = null;
		String orderBy = null;

		Cursor filesCursor = getDatabase().query(TABLE_NAME, selColumns, selection, selectionArgs, groupBy, having,
				orderBy);
		if (filesCursor == null)
			return null;

		List<StoreItem> files = toList(filesCursor);
		filesCursor.close();
		return files;
	}

	public List<StoreItem> getRows(String type) {
		String[] selColumns = null;
		String selection = COLUMN_STATUS + " = ? AND " + COLUMN_TYPE + " = ?";
		String[] selectionArgs = { STATUS_ACTIVE, type };

		String groupBy = null;
		String having = null;
		String orderBy = null;

		Cursor filesCursor = getDatabase().query(TABLE_NAME, selColumns, selection, selectionArgs, groupBy, having,
				orderBy);
		if (filesCursor == null)
			return null;

		List<StoreItem> files = toList(filesCursor);
		filesCursor.close();
		return files;
	}

	public int changeStatus(String id, String status) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_LAST_MODIFIED, getCurrentDateTime());
		values.put(COLUMN_STATUS, status);
		return getDatabase().update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[] { id });
	}

	public int deleteItem(String id) {
		return getDatabase().delete(TABLE_NAME, COLUMN_ID + " = '" + id + "'", null);
	}

	private StoreItem cursorToItemPackage(Cursor itemsCursor) {
		StoreItem item = new StoreItem();
		item.setIdString(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_ID)));
		item.setLastModified(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_LAST_MODIFIED)));
		item.setStatus(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_STATUS)));
		item.setTitle(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_NAME)));
		item.setThumbnail(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_THUMBNAIL)));
		item.setSelectedThumbnail(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_SELECTED_THUMBNAIL)));
		item.setType(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_TYPE)));
		item.setUrl(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_URL)));
		item.setSignature(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_SIGNATURE)));
		return item;
	}

	private List<StoreItem> toList(Cursor itemsCursor) {
		List<StoreItem> itemList = new ArrayList<StoreItem>();
		if (itemsCursor.moveToFirst()) {
			do {
				StoreItem item = cursorToItemPackage(itemsCursor);
				itemList.add(item);
				itemsCursor.moveToNext();
			} while (!itemsCursor.isAfterLast());
		}
		return itemList;
	}
}
