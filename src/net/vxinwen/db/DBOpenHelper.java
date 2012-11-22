package net.vxinwen.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DBNAME = "Joey.db";
    private static final int VERSION = 2;

    public DBOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    private static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS filedownlog "
            + "(id integer primary key autoincrement, downloadname varchar(30), downloadsinger varchar(30), format varchar(30),"
            + " downloadsize INTEGER, totalsize INTEGER, downloadtrack varchar(100),versionname varchar(20))";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
    }

    private static final String DROP_SQL = "DROP TABLE IF EXISTS filedownlog";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_SQL);
        onCreate(db);
    }

}
