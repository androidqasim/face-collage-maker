package dauroi.photoeditor.database;

import java.io.IOException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.crash.FirebaseCrash;

import dauroi.photoeditor.config.ALog;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DatabaseManager.DB_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ALog.e(TAG, "onCreate()-db.getVersion=" + db.getVersion());
        try {
            db.beginTransaction();
            String[] assetSqlScripts = {"create_structure_and_default_data.sql"};
            for (int i = 0; i < assetSqlScripts.length; i++) {
                DatabaseManager.runSqlScript(mContext, db, assetSqlScripts[i]);
            }
            db.setTransactionSuccessful();
            ALog.i(TAG, "IN onCreate() runAssetSqlScript() CALLED!");
        } catch (Exception ioe) {
            ioe.printStackTrace();
            FirebaseCrash.report(ioe);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ALog.i(TAG, "IN onUpgrade() oldVersion=" + oldVersion + ", newVersion=" + newVersion);
        try {
            db.beginTransaction();
            DatabaseManager.runSqlScript(mContext, db, "upgrade_db.sql");
            db.setTransactionSuccessful();
            ALog.i(TAG, "IN onUpgrade() runAssetSqlScript() CALLED!");
        } catch (Exception ioe) {
            ioe.printStackTrace();
            FirebaseCrash.report(ioe);
        } finally {
            db.endTransaction();
        }
    }
}
