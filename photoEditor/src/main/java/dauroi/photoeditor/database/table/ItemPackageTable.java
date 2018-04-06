package dauroi.photoeditor.database.table;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import dauroi.photoeditor.model.ItemPackageInfo;

/**
 * Created by Wisekey on 3/18/2015.
 */
public class ItemPackageTable extends BaseTable {
    public static final String CROP_TYPE = "crop";
    public static final String FILTER_TYPE = "filter";
    public static final String SHADE_TYPE = "shade";
    public static final String FRAME_TYPE = "frame";
    public static final String BACKGROUND_TYPE = "background";
    public static final String STICKER_TYPE = "sticker";
    // Database structure
    public static final String TABLE_NAME = "ItemPackage";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_THUMBNAIL = "thumbnail";
    public static final String COLUMN_SELECTED_THUMBNAIL = "selected_thumbnail";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_FOLDER = "folder";
    public static final String COLUMN_TEXT_ID = "id_str";
    // Database creation sql statement
    private static final String SQL_DATABASE_CREATE = "create table " + TABLE_NAME + "(" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " text," + COLUMN_THUMBNAIL + " text,"
            + COLUMN_SELECTED_THUMBNAIL + " text,"
            + COLUMN_TYPE + " text," + COLUMN_TEXT_ID + " text," + COLUMN_FOLDER + " text," + COLUMN_LAST_MODIFIED
            + " text," + COLUMN_STATUS + " text" + ");";

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(SQL_DATABASE_CREATE);
    }

    public static void upgradeTable(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public ItemPackageTable(Context context) {
        super(context);
    }

    public long insert(ItemPackageInfo info) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, info.getTitle());
        values.put(COLUMN_THUMBNAIL, info.getThumbnail());
        values.put(COLUMN_SELECTED_THUMBNAIL, info.getSelectedThumbnail());
        values.put(COLUMN_TYPE, info.getType());
        values.put(COLUMN_FOLDER, info.getFolder());
        values.put(COLUMN_TEXT_ID, info.getIdString());
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

    public int update(ItemPackageInfo info) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, info.getTitle());
        values.put(COLUMN_THUMBNAIL, info.getThumbnail());
        values.put(COLUMN_SELECTED_THUMBNAIL, info.getSelectedThumbnail());
        values.put(COLUMN_TYPE, info.getType());
        values.put(COLUMN_LAST_MODIFIED, getCurrentDateTime());
        values.put(COLUMN_FOLDER, info.getFolder());
        values.put(COLUMN_TEXT_ID, info.getIdString());
        if (info.getStatus() == null || info.getStatus().length() < 1) {
            info.setStatus(STATUS_ACTIVE);
        }

        values.put(COLUMN_STATUS, info.getStatus());

        return getDatabase().update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(info.getId())});
    }

    public boolean hasItem(String id, boolean active) {
        String selectString = "SELECT " + COLUMN_ID + " FROM " + TABLE_NAME + " WHERE " + COLUMN_TEXT_ID + " =?";
        if (active) {
            selectString = selectString.concat(" AND ").concat(COLUMN_STATUS).concat(" =?");
        }
        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        Cursor cursor = null;
        if (active) {
            cursor = getDatabase().rawQuery(selectString, new String[]{id, STATUS_ACTIVE});
        } else {
            cursor = getDatabase().rawQuery(selectString, new String[]{id});
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

    public ItemPackageInfo getRowWithName(String name) {
        String[] selColumns = null;
        String selection = COLUMN_STATUS + " = ? AND UPPER(" + COLUMN_NAME + ") = UPPER(?)";
        String[] selectionArgs = {STATUS_ACTIVE, name};

        String groupBy = null;
        String having = null;
        String orderBy = null;

        Cursor filesCursor = getDatabase().query(TABLE_NAME, selColumns, selection, selectionArgs, groupBy, having,
                orderBy);
        if (filesCursor == null)
            return null;

        List<ItemPackageInfo> files = toList(filesCursor);
        filesCursor.close();
        if (files.size() > 0) {
            return files.get(0);
        } else {
            return null;
        }
    }

    public ItemPackageInfo getRowWithStoreId(String idStr) {
        String[] selColumns = null;
        String selection =  COLUMN_TEXT_ID + " = ?";
        String[] selectionArgs = {idStr};

        String groupBy = null;
        String having = null;
        String orderBy = null;

        Cursor filesCursor = getDatabase().query(TABLE_NAME, selColumns, selection, selectionArgs, groupBy, having,
                orderBy);
        if (filesCursor == null)
            return null;

        List<ItemPackageInfo> files = toList(filesCursor);
        filesCursor.close();
        if (files.size() > 0) {
            return files.get(0);
        } else {
            return null;
        }
    }

    public List<ItemPackageInfo> getAllRows() {
        String[] selColumns = null;
        String selection = COLUMN_STATUS + " = ? ";
        String[] selectionArgs = {STATUS_ACTIVE};

        String groupBy = null;
        String having = null;
        String orderBy = null;

        Cursor filesCursor = getDatabase().query(TABLE_NAME, selColumns, selection, selectionArgs, groupBy, having,
                orderBy);
        if (filesCursor == null)
            return null;

        List<ItemPackageInfo> files = toList(filesCursor);
        filesCursor.close();
        return files;
    }

    public List<ItemPackageInfo> getRows(String type) {
        String[] selColumns = null;
        String selection = COLUMN_STATUS + " = ? AND " + COLUMN_TYPE + " = ?";
        String[] selectionArgs = {STATUS_ACTIVE, type};

        String groupBy = null;
        String having = null;
        String orderBy = COLUMN_LAST_MODIFIED.concat(" DESC");

        Cursor filesCursor = getDatabase().query(TABLE_NAME, selColumns, selection, selectionArgs, groupBy, having,
                orderBy);
        if (filesCursor == null)
            return null;

        List<ItemPackageInfo> files = toList(filesCursor);
        filesCursor.close();
        return files;
    }

    public int changeStatus(String idStr, String status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LAST_MODIFIED, getCurrentDateTime());
        values.put(COLUMN_STATUS, status);
        return getDatabase().update(TABLE_NAME, values, COLUMN_TEXT_ID + " = ?", new String[]{idStr});
    }

    public int markDeleted(String idStr) {
        return changeStatus(idStr, STATUS_DELETED);
    }

    public int changeStatus(long id, String status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LAST_MODIFIED, getCurrentDateTime());
        values.put(COLUMN_STATUS, status);
        return getDatabase().update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int markDeleted(long id) {
        return changeStatus(id, STATUS_DELETED);
    }

    public int delete(long id) {
        return getDatabase().delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int delete(String idStr){
        return getDatabase().delete(TABLE_NAME, COLUMN_TEXT_ID + "=?", new String[]{idStr});
    }

    private ItemPackageInfo cursorToItemPackage(Cursor itemsCursor) {
        ItemPackageInfo item = new ItemPackageInfo();
        item.setId(itemsCursor.getLong(itemsCursor.getColumnIndex(COLUMN_ID)));
        item.setLastModified(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_LAST_MODIFIED)));
        item.setStatus(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_STATUS)));
        item.setTitle(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_NAME)));
        item.setThumbnail(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_THUMBNAIL)));
        item.setSelectedThumbnail(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_SELECTED_THUMBNAIL)));
        item.setType(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_TYPE)));
        item.setFolder(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_FOLDER)));
        item.setIdString(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_TEXT_ID)));
        return item;
    }

    private List<ItemPackageInfo> toList(Cursor itemsCursor) {
        List<ItemPackageInfo> itemList = new ArrayList<ItemPackageInfo>();
        if (itemsCursor.moveToFirst()) {
            do {
                ItemPackageInfo item = cursorToItemPackage(itemsCursor);
                itemList.add(item);
                itemsCursor.moveToNext();
            } while (!itemsCursor.isAfterLast());
        }
        return itemList;
    }
}
