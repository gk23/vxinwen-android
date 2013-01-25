package net.vxinwen.db;

import java.util.logging.Logger;

import net.vxinwen.R;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 不同的context，需要不同的SQLiteDatabase实例吗？如果不是，可以修改SQLiteDatabase成单例。
 * 
 * SQLiteDatabase只与context相关，SQLiteDatabase内部会check，
 * 如果同一类context则会返回已有SQLiteDatabase实例。
 * 
 * 用法： 1. SQLiteDatabase db = new DBOpenHelper(context).getWritableDatabase();
 * 2. 用完需要close
 * 
 * @author gk23<aoaogk@gmail.com>
 * 
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DBNAME = "vxinwen.db";
    private static int VERSION = 1;

    private Context context;

    private static final String CREATE_CATEGORY_SQL = "CREATE TABLE IF NOT EXISTS category "
            + "(id integer primary key autoincrement, name nvarchar(30),description nvarchar(256))";
    private static final String CREATE_NEWS_SQL = "CREATE TABLE IF NOT EXISTS news "
            + "(id integer primary key autoincrement, category varchar(100), url varchar(256),image_address varchar(256), title nvarchar(256), content text,summary nvarchar(281),publish_time timestamp)";
    private static final String INIT_CATEGORY_SQL_TEMPLATE = "INSERT INTO category (name,description) {values}";
    private static final String DROP_CATEGORY_SQL = "DROP TABLE IF EXISTS category";
    private static final String DROP_NEWS_SQL = "DROP TABLE IF EXISTS news";

    public DBOpenHelper(Context context) {
        super(context, DBNAME, null, getVersion(context));
        this.context = context;
    }

    private static int getVersion(Context context) {
        int ver = Integer.parseInt(context.getResources().getString(R.string.sql_version));
        VERSION = ver;
        return ver;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String initSql = getInitCategorySql();
        Log.d(this.getClass().getName(), "In onCreate method, The init sql is [" + initSql + "]");
        try {
            db.execSQL(CREATE_CATEGORY_SQL);
            db.execSQL(CREATE_NEWS_SQL);
            db.execSQL(initSql);
        } catch (SQLException e) {
            Log.d(this.getClass().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(this.getClass().getName(), "Comming in onupgrade method, the drop sql is ["
                + DROP_CATEGORY_SQL + "]");
        try {
            db.execSQL(DROP_CATEGORY_SQL);
            db.execSQL(DROP_NEWS_SQL);
            onCreate(db);
        } catch (SQLException e) {
            Log.d(this.getClass().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 拼接语句为 insert into category (name,description) select '要闻','要闻' union all
     * select '体育','体育咨询' union all select 'test','test'…
     * 
     * @return
     */
    private String getInitCategorySql() {
        String[] cat_titles = this.context.getResources().getStringArray(R.array.category_names);
        String[] cat_descs = this.context.getResources().getStringArray(R.array.category_descs);
        String template = " UNION ALL SELECT '{name}','{desc}'";
        String init = "SELECT '" + cat_titles[0] + "','" + cat_descs[0] + "'";
        StringBuilder values = new StringBuilder(init);
        for (int i = 1; i < cat_titles.length; i++) {
            values.append(template.replace("{name}", cat_titles[i]).replace("{desc}", cat_descs[i]));
        }
        return INIT_CATEGORY_SQL_TEMPLATE.replace("{values}", values);
    }

    public static void close(Cursor cursor, SQLiteDatabase db) {
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
    }
}
