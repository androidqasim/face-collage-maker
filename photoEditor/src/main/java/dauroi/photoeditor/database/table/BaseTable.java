package dauroi.photoeditor.database.table;

import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import dauroi.photoeditor.PhotoEditorApp;
import dauroi.photoeditor.database.DatabaseManager;

public abstract class BaseTable {
	public static final String BOOL_Y = "Y";
	public static final String BOOL_N = "N";
	public static final String STATUS_DELETED = "deleted";
	public static final String STATUS_ACTIVE = "active";

	public static final String COLUMN_ID = "id";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_LAST_MODIFIED = "last_modified";

	public static String convertBooleanToText(boolean b) {
		if (b) {
			return BOOL_Y;
		} else {
			return BOOL_N;
		}
	}

	public static boolean convertTextToBoolean(String text) {
		if (BOOL_Y.equals(text)) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressLint("DefaultLocale")
	public static String generateUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().toUpperCase();
	}

	public static String genRandomIdUsingCurrentDb() {
		return genRandomId(DatabaseManager.getInstance().getDb());
	}

	public static String genRandomId(SQLiteDatabase db) {
		String randomIdSql = "SELECT hex(randomblob(8))";
		Cursor randomIdCursor = db.rawQuery(randomIdSql, null);
		if (randomIdCursor.moveToFirst()) {
			String randomId = randomIdCursor.getString(0);
			randomIdCursor.close();
			return randomId;
		} else if (randomIdCursor != null) {
			randomIdCursor.close();
		}

		throw new RuntimeException("Cannot generate a random id string from database.");
	}

	private Context mContext;

	private SQLiteDatabase mDatabase;

	public BaseTable(Context context) {
		if (context == null) {
			context = PhotoEditorApp.getAppContext();
		}

		this.mDatabase = DatabaseManager.getInstance(context).getDb();
		this.mContext = context;
	}

	/**
	 * Only use when access cloud database.
	 *
	 * @param db
	 */
	public BaseTable(SQLiteDatabase db) {
		mDatabase = db;
		mContext = null;
	}

	public void setDb(SQLiteDatabase db) {
		mDatabase = db;
	}

	public Context getContext() {
		return mContext;
	}

	public SQLiteDatabase getDatabase() {
		if (mDatabase == null || !mDatabase.isOpen()) {
			if (mContext != null) {
				mDatabase = DatabaseManager.getInstance(mContext).getDb();
			} else {
				mDatabase = DatabaseManager.getInstance().getDb();
			}

			if (mDatabase == null || !mDatabase.isOpen()) {
				if (mContext != null) {
					DatabaseManager.getInstance(mContext).openDb();
				} else {
					DatabaseManager.getInstance().openDb();
				}

				if (mContext != null) {
					mDatabase = DatabaseManager.getInstance(mContext).getDb();
				} else {
					mDatabase = DatabaseManager.getInstance().getDb();
				}
			}
		}

		return mDatabase;
	}

	public String genRandom16BytesHexString() {
		String randomIdSql = "SELECT hex(randomblob(16))";
		Cursor randomIdCursor = getDatabase().rawQuery(randomIdSql, null);
		if (randomIdCursor.moveToFirst()) {
			String randomId = randomIdCursor.getString(0);
			randomIdCursor.close();
			return randomId;
		} else if (randomIdCursor != null) {
			randomIdCursor.close();
		}
		return null;
	}

	public String genRandom24BytesHexString() {
		String randomIdSql = "SELECT hex(randomblob(24))";
		Cursor randomIdCursor = getDatabase().rawQuery(randomIdSql, null);
		if (randomIdCursor.moveToFirst()) {
			String randomId = randomIdCursor.getString(0);
			randomIdCursor.close();
			return randomId;
		} else if (randomIdCursor != null) {
			randomIdCursor.close();
		}

		throw new RuntimeException("Cannot generate a random id string from database.");
	}

	public String genRandom32BytesHexString() {
		String randomIdSql = "SELECT hex(randomblob(32))";
		Cursor randomIdCursor = getDatabase().rawQuery(randomIdSql, null);
		if (randomIdCursor.moveToFirst()) {
			String randomId = randomIdCursor.getString(0);
			randomIdCursor.close();
			return randomId;
		} else if (randomIdCursor != null) {
			randomIdCursor.close();
		}

		throw new RuntimeException("Cannot generate a random id string from database.");
	}

	public String genRandom8BytesHexString() {
		String randomIdSql = "SELECT hex(randomblob(8))";
		Cursor randomIdCursor = getDatabase().rawQuery(randomIdSql, null);
		if (randomIdCursor.moveToFirst()) {
			String randomId = randomIdCursor.getString(0);
			randomIdCursor.close();
			return randomId;
		} else if (randomIdCursor != null) {
			randomIdCursor.close();
		}
		return null;
	}

	public String genRandomId() {
		return genRandomId(getDatabase());
	}

	public String getCurrentDateTime() {
		String currentDateTimeSql = "SELECT datetime('now')";
		Cursor currentDateTimeCursor = getDatabase().rawQuery(currentDateTimeSql, null);
		if (currentDateTimeCursor.moveToFirst()) {
			String currentDateTimeStr = currentDateTimeCursor.getString(0);
			currentDateTimeCursor.close();
			return currentDateTimeStr;
		} else if (currentDateTimeCursor != null) {
			currentDateTimeCursor.close();
		}

		throw new RuntimeException("Cannot get current date and time from database.");
	}
}
