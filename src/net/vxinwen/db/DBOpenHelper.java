package net.vxinwen.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DBNAME = "vxinwen.db";
    private static final int VERSION = 1;

    public DBOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    private static final String CREATE_CATEGORY_SQL = "CREATE TABLE IF NOT EXISTS category "
            + "(id integer primary key autoincrement, name varchar(30)";
    private static final String CREATE_NEWS_SQL = "CREATE TABLE IF NOT EXISTS news "
        + "(id integer primary key autoincrement, category_id bigint, image_address varchar(128), title nvarchar(256), content text,summary nvarchar(281)";
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORY_SQL);
        db.execSQL(CREATE_NEWS_SQL);
    }

    private static final String DROP_CATEGORY_SQL = "DROP TABLE IF EXISTS category";
    private static final String DROP_NEWS_SQL = "DROP TABLE IF EXISTS news";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_CATEGORY_SQL);
        db.execSQL(DROP_NEWS_SQL);
        onCreate(db);
    }

}
