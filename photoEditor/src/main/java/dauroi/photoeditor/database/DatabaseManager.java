package dauroi.photoeditor.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.google.firebase.crash.FirebaseCrash;

import dauroi.photoeditor.PhotoEditorApp;
import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.utils.FileUtils;

public class DatabaseManager {
    private static final String TAG = DatabaseManager.class.getSimpleName();
    public static final float DB_SCHEMA_VERSION = 1.0f;
    private static final String SQL_COMMENT = "--";
    private static final Map<String, String[]> DB_MIGRATE_SCRIPT_NAMES_MAP = initializeDbMigrateScriptNamesMap();

    public static String DB_NAME = "photoeditor.db";
    private static DatabaseManager instance;

    public static String getDbFileFullPath() {
        if (PhotoEditorApp.getAppContext() != null) {
            return getDbFileFullPath(PhotoEditorApp.getAppContext());
        } else {
            throw new RuntimeException("Didnt' set context for DbHelper!");
        }
    }

    public static String getDbFileFullPath(Context ctx) {
        return ctx.getDatabasePath(DB_NAME).getAbsolutePath();
    }

    public static String getDbsDirPath(Context ctx) {
        String dbFilePath = ctx.getDatabasePath(DB_NAME).getAbsolutePath();
        return dbFilePath.substring(0, dbFilePath.length() - DB_NAME.length());
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager(PhotoEditorApp.getAppContext());
        }
        return instance;
    }

    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    public static DatabaseManager getInstanceAfterKilledByOS(Context context) {
        instance = new DatabaseManager(context);
        return instance;
    }

    private static Map<String, String[]> initializeDbMigrateScriptNamesMap() {
        // NOTEs:
        // WIDA's First DbSchemaVersion is "2.5"
        // Assuming that data issues Only exist in DB versions below 3.6
        Map<String, String[]> dbMigrateScriptNamesMap = new HashMap<String, String[]>();
        String fromDbSchemaVersion = "2.5";
        String[] dbMigrateScriptNamesInOrder = new String[]{"migrate_db_from_2.5_to_3.1.sql",
                "migrate_db_from_3.1_to_3.2.sql", "migrate_db_from_3.2_to_3.3.sql", "migrate_db_from_3.3_to_3.4.sql",
                "migrate_db_from_3.4_to_3.5.sql", "fix_data_issues.sql", "migrate_db_from_3.5_to_3.6.sql",
                "create_FileEncryption_Db.sql", "migrate_db_from_3.6_to_3.7.sql"};
        dbMigrateScriptNamesMap.put(fromDbSchemaVersion, dbMigrateScriptNamesInOrder);

        fromDbSchemaVersion = "3.0";
        dbMigrateScriptNamesInOrder = new String[]{"migrate_db_from_3.0_to_3.1.sql", "migrate_db_from_3.1_to_3.2.sql",
                "migrate_db_from_3.2_to_3.3.sql", "migrate_db_from_3.3_to_3.4.sql", "migrate_db_from_3.4_to_3.5.sql",
                "fix_data_issues.sql", "migrate_db_from_3.5_to_3.6.sql", "create_FileEncryption_Db.sql",
                "migrate_db_from_3.6_to_3.7.sql"};
        dbMigrateScriptNamesMap.put(fromDbSchemaVersion, dbMigrateScriptNamesInOrder);

        dbMigrateScriptNamesMap.put(fromDbSchemaVersion, dbMigrateScriptNamesInOrder);

        return dbMigrateScriptNamesMap;
    }

    public static byte[] loadBigBlob(SQLiteDatabase db, String blobTable, String blobRecordId, int blobLength,
                                     int maxReadPerQuery) {
        String query = null;
        Cursor cursor = null;
        if (blobLength <= maxReadPerQuery) {
            query = "SELECT SUBSTR(value, 1) FROM " + blobTable + " WHERE id = ?";
            cursor = db.rawQuery(query, new String[]{blobRecordId});
            cursor.moveToFirst();
            byte[] blob = cursor.getBlob(0);
            cursor.close();
            return blob;
        } else {
            int i = 0;
            ByteBuffer blobBuffer = ByteBuffer.allocate(blobLength);
            int restUnRead = blobLength;
            // Read big blob in segments , repeat until the whole has been read
            do {
                query = "SELECT SUBSTR(value, " + (i++ * maxReadPerQuery) + " , " + maxReadPerQuery + ") FROM "
                        + blobTable + " WHERE id = ?";
                cursor = db.rawQuery(query, new String[]{blobRecordId});
                cursor.moveToFirst();
                byte[] partial = cursor.getBlob(0);
                // Copy partial
                blobBuffer.put(partial);
                cursor.close();
                restUnRead -= maxReadPerQuery;
            } while (restUnRead > maxReadPerQuery);
            // Read the rest of blob
            query = "SELECT SUBSTR(value, " + (i * maxReadPerQuery) + ") FROM " + blobTable + " WHERE id = ?";
            cursor = db.rawQuery(query, new String[]{blobRecordId});
            cursor.moveToFirst();
            byte[] lastPartial = cursor.getBlob(0);
            // Copy lastPartial
            blobBuffer.put(lastPartial);
            cursor.close();
            byte[] blob = blobBuffer.array();
            return blob;
        }
    }

    public static void runSqlScript(Context context, SQLiteDatabase db, String scriptFileName) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(scriptFileName)));
        String stmt_str = null;
        String sql = "";
        String tmp;
        while ((stmt_str = br.readLine()) != null) {
            tmp = stmt_str.trim();
            if (tmp.length() <= 0 || tmp.startsWith(SQL_COMMENT)) {
                continue;
            } else {
                sql += " " + stmt_str;
            }

			/* Execute script if it ends with ; */
            if (tmp.endsWith(";")) {
                try {
                    db.execSQL(sql);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    FirebaseCrash.report(ex);
                }

				/* For next statement in the script */
                sql = "";
            }
        }

        br.close();
        br = null;
        System.gc();
    }

    private Context mCtx;

    private SQLiteDatabase mDb;

    private String mFromDbSchemaVersion;

    private File mDbFile;

    private DatabaseManager(Context context) {
        if (context == null) {
            throw new RuntimeException("PASSED Context is NULL!");
        }

        mCtx = context.getApplicationContext();
        mDbFile = context.getDatabasePath(DB_NAME);
    }

    public synchronized void closeDb() {
        if (mDb != null && mDb.isOpen()) {
            mDb.close();
            mDb = null;
        }
    }

    public synchronized void createDb() {
        ALog.d(TAG, "createDb");
        // Create a new DB
        DatabaseHelper dbHelper = new DatabaseHelper(mCtx);
        mDb = dbHelper.getWritableDatabase();
    }

    public synchronized void deleteDbFile() {
        closeDb();
        if (isDbFileExisted()) {
            mDbFile.delete();
        }
    }

    public void exportDatabases() {
        File db = new File(getDbFileFullPath());
        File[] files = db.getParentFile().listFiles();
        if (files != null) {
            final String outFolder = Environment.getExternalStorageDirectory().toString().concat("/exportedDB");
            final File outFile = new File(outFolder);
            outFile.mkdirs();
            for (File file : files) {
                File tmp = new File(outFolder, file.getName());
                try {
                    FileUtils.copyFile(file, tmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String genRandom2BytesHexString() {
        String randomIdSql = "SELECT hex(randomblob(2))";
        Cursor randomIdCursor = mDb.rawQuery(randomIdSql, null);
        if (randomIdCursor.moveToFirst()) {
            String randomId = randomIdCursor.getString(0);
            randomIdCursor.close();
            return randomId;
        } else if (randomIdCursor != null) {
            randomIdCursor.close();
        }
        return null;
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }

    public String getDbSchemaVersion() {
        return getDbSchemaVersion(mDb);
    }

    public String getDbSchemaVersion(SQLiteDatabase db) {
        String dbSchemaVersion = "1.0";
        String sql = "SELECT value FROM settings WHERE name = ?";
        String[] selectionArgs = {"db_schema_version"};
        Cursor dbSchemaVersionCursor = null;
        try {
            dbSchemaVersionCursor = db.rawQuery(sql, selectionArgs);
            if (dbSchemaVersionCursor != null && dbSchemaVersionCursor.moveToFirst()) {
                dbSchemaVersion = dbSchemaVersionCursor.getString(dbSchemaVersionCursor.getColumnIndex("value"));
                if (dbSchemaVersion != null) {
                    dbSchemaVersion = dbSchemaVersion.trim();
                }
            }
            if (dbSchemaVersionCursor != null) {
                dbSchemaVersionCursor.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            // No such table: 'settings'
            sql = "SELECT value FROM setting WHERE name = ?";
            try {
                dbSchemaVersionCursor = db.rawQuery(sql, selectionArgs);
                if (dbSchemaVersionCursor != null) {
                    dbSchemaVersionCursor.close();
                }
            } catch (Throwable t2) {
                t2.printStackTrace();
            }
        }

        return dbSchemaVersion;
    }

    public boolean isDbFileExisted() {
        File db = mCtx.getDatabasePath(DB_NAME);
        if (db.exists() && db.length() > 0) {
            return true;
        } else {
            db.delete();
            return false;
        }
    }

    public void migrateDb() throws IOException {
        if (mFromDbSchemaVersion == null) {
            mFromDbSchemaVersion = getDbSchemaVersion();
        }
        migrateDb(mFromDbSchemaVersion);
    }

    private void migrateDb(String fromDbSchemaVersion) throws IOException {
        String[] migrateSqlScriptNames = DB_MIGRATE_SCRIPT_NAMES_MAP.get(fromDbSchemaVersion);
        if (migrateSqlScriptNames != null && migrateSqlScriptNames.length > 0) {
            mDb.beginTransaction();
            for (String sqlScriptName : migrateSqlScriptNames) {
                runSqlScript(sqlScriptName);
            }
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
        }
    }

    public boolean isOpenedDb() {
        if (mDb != null && mDb.isOpen()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean needDbMigration() {
        // Get DB Schema Version from table 'setting/settings'
        mFromDbSchemaVersion = getDbSchemaVersion();
        // return !DB_SCHEMA_VERSION.equalsIgnoreCase(mFromDbSchemaVersion);
        final float dbVersion = Float.parseFloat(mFromDbSchemaVersion);
        return (dbVersion < DB_SCHEMA_VERSION);
    }

    public synchronized boolean openDb() {
        ALog.d(TAG, "openDb");
        if (mDb != null && mDb.isOpen()) {
            return true;
        }

        try {
            mDb = SQLiteDatabase.openOrCreateDatabase(mDbFile, null);
            return mDb.isOpen();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void runSqlScript(String scriptFileName) throws IOException {
        runSqlScript(mCtx, mDb, scriptFileName);
    }

    public void upgradeBackupDb(SQLiteDatabase backupDb) throws IOException {
        String backupDbSchemaVersion = getDbSchemaVersion(backupDb);
        final float backupDbVersion = Float.parseFloat(backupDbSchemaVersion);
        if (backupDbVersion < DB_SCHEMA_VERSION) {
            String[] migrateSqlScriptNames = DB_MIGRATE_SCRIPT_NAMES_MAP.get(backupDbSchemaVersion);
            if (migrateSqlScriptNames != null && migrateSqlScriptNames.length > 0) {
                backupDb.beginTransaction();
                for (String sqlScriptName : migrateSqlScriptNames) {
                    runSqlScript(mCtx, backupDb, sqlScriptName);
                }
                backupDb.setTransactionSuccessful();
                backupDb.endTransaction();
            }
        }
    }
}
