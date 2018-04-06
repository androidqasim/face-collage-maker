package dauroi.photoeditor.database.table;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import dauroi.photoeditor.model.FilterInfo;

public class FilterTable extends BaseTable {
	public static final String TABLE_NAME = "Filter";

	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_THUMBNAIL = "thumbnail";
	public static final String COLUMN_SELECTED_THUMBNAIL = "selected_thumbnail";
	public static final String COLUMN_CMD = "cmd";
	public static final String COLUMN_PACKAGE_ID = "package_id";

	// Database creation sql statement
	private static final String SQL_DATABASE_CREATE = "create table " + TABLE_NAME + "(" + COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_NAME + " text,"
			+ COLUMN_THUMBNAIL + " text,"
			+ COLUMN_SELECTED_THUMBNAIL + " text,"
			+ COLUMN_CMD + " text,"
			+ COLUMN_PACKAGE_ID + " integer,"
			+ COLUMN_LAST_MODIFIED + " text,"
			+ COLUMN_STATUS + " text"
			+ ");";

	public static void createTable(SQLiteDatabase database) {
		database.execSQL(SQL_DATABASE_CREATE);
	}

	public static void upgradeTable(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	public FilterTable(Context context) {
		super(context);
	}

	public FilterInfo get(long packageId, String name) {
		String[] selColumns = null;
		String selection = COLUMN_PACKAGE_ID + " = ? AND " + COLUMN_STATUS + " = ? AND UPPER(" + COLUMN_NAME
				+ ") = UPPER(?)";
		String[] selectionArgs = { String.valueOf(packageId), STATUS_ACTIVE, name };

		String groupBy = null;
		String having = null;
		String orderBy = null;

		Cursor filesCursor = getDatabase().query(TABLE_NAME, selColumns, selection, selectionArgs, groupBy, having,
				orderBy);
		if (filesCursor == null)
			return null;
		if (filesCursor.moveToFirst()) {
			FilterInfo result = cursorToFilterInfo(filesCursor);
			filesCursor.close();
			return result;
		} else {
			return null;
		}
	}

	public long insert(FilterInfo info) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, info.getTitle());
		values.put(COLUMN_THUMBNAIL, info.getThumbnail());
		values.put(COLUMN_SELECTED_THUMBNAIL, info.getSelectedThumbnail());
		values.put(COLUMN_CMD, info.getCmd());
		values.put(COLUMN_PACKAGE_ID, info.getPackageId());
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

	public int update(FilterInfo info) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, info.getTitle());
		values.put(COLUMN_THUMBNAIL, info.getThumbnail());
		values.put(COLUMN_SELECTED_THUMBNAIL, info.getSelectedThumbnail());
		values.put(COLUMN_CMD, info.getCmd());
		values.put(COLUMN_PACKAGE_ID, info.getPackageId());
		values.put(COLUMN_LAST_MODIFIED, getCurrentDateTime());
		if (info.getStatus() == null || info.getStatus().length() < 1) {
			info.setStatus(STATUS_ACTIVE);
		}

		values.put(COLUMN_STATUS, info.getStatus());

		return getDatabase().update(TABLE_NAME, values, COLUMN_ID + " = ?",
				new String[] { String.valueOf(info.getId()) });
	}

	public List<FilterInfo> getAllRows() {
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

		List<FilterInfo> files = toList(filesCursor);
		filesCursor.close();
		return files;
	}

	public List<FilterInfo> getAllRows(long packageId) {
		String[] selColumns = null;
		String selection = COLUMN_PACKAGE_ID + " = ? AND " + COLUMN_STATUS + " = ? ";
		String[] selectionArgs = { String.valueOf(packageId), STATUS_ACTIVE };

		String groupBy = null;
		String having = null;
		String orderBy = null;

		Cursor filesCursor = getDatabase().query(TABLE_NAME, selColumns, selection, selectionArgs, groupBy, having,
				orderBy);
		if (filesCursor == null)
			return null;

		List<FilterInfo> files = toList(filesCursor);
		filesCursor.close();
		return files;
	}

	public int changeStatus(long id, String status) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_LAST_MODIFIED, getCurrentDateTime());
		values.put(COLUMN_STATUS, status);
		return getDatabase().update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
	}

	public int markDeleted(long id) {
		return changeStatus(id, STATUS_DELETED);
	}

	public int deleteAllItemInPackage(long packageId){
		return getDatabase().delete(TABLE_NAME, COLUMN_PACKAGE_ID + " = ?", new String[]{String.valueOf(packageId)});
	}

	private FilterInfo cursorToFilterInfo(Cursor itemsCursor) {
		FilterInfo item = new FilterInfo();
		item.setId(itemsCursor.getLong(itemsCursor.getColumnIndex(COLUMN_ID)));
		item.setLastModified(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_LAST_MODIFIED)));
		item.setStatus(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_STATUS)));
		item.setTitle(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_NAME)));
		item.setThumbnail(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_THUMBNAIL)));
		item.setSelectedThumbnail(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_SELECTED_THUMBNAIL)));
		item.setCmd(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_CMD)));
		item.setPackageId(itemsCursor.getInt(itemsCursor.getColumnIndex(COLUMN_PACKAGE_ID)));
		return item;
	}

	private List<FilterInfo> toList(Cursor itemsCursor) {
		List<FilterInfo> itemList = new ArrayList<FilterInfo>();
		if (itemsCursor.moveToFirst()) {
			do {
				FilterInfo item = cursorToFilterInfo(itemsCursor);
				itemList.add(item);
				itemsCursor.moveToNext();
			} while (!itemsCursor.isAfterLast());
		}

		return itemList;
	}

}
