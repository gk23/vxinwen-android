package net.vxinwen.db;

import net.vxinwen.R;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 不同的context，需要不同的SQLiteDatabase实例吗？如果不是，可以修改SQLiteDatabase成单例。
 * 
 * SQLiteDatabase只与context相关，SQLiteDatabase内部会check， 如果同一类context则会返回已有SQLiteDatabase实例。
 * 
 * 用法： 1. SQLiteDatabase db = new DBOpenHelper(context).getWritableDatabase(); 2. 用完需要close
 * 
 * @author gk23<aoaogk@gmail.com>
 * 
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DBNAME = "vxinwen.db";
    private static final int VERSION = 1;
    private SQLiteDatabase db;

    private Context context;

    public DBOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        this.context = context;
    }

    private static final String CREATE_CATEGORY_SQL = "CREATE TABLE IF NOT EXISTS category "
            + "(id integer primary key autoincrement, name varchar(30)";
    private static final String CREATE_NEWS_SQL = "CREATE TABLE IF NOT EXISTS news "
            + "(id integer primary key autoincrement, category_id bigint, image_address varchar(128), title nvarchar(256), content text,summary nvarchar(281)";
    private static final String INIT_CATEGORY_SQL_TEMPLATE = "INSERT INTO catgory (name,desc) VALUES {values}";
    private static final String DROP_CATEGORY_SQL = "DROP TABLE IF EXISTS category";
    private static final String DROP_NEWS_SQL = "DROP TABLE IF EXISTS news";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORY_SQL);
        db.execSQL(CREATE_NEWS_SQL);
        db.execSQL(getInitCategorySql());
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_CATEGORY_SQL);
        db.execSQL(DROP_NEWS_SQL);
        onCreate(db);
    }

    private String getInitCategorySql() {
        String[] cat_titles = this.context.getResources().getStringArray(R.array.category_names);
        String[] cat_descs = this.context.getResources().getStringArray(R.array.category_descs);
        String template = "('{name}','{desc}')";
        StringBuilder values = new StringBuilder(template.replace("{name}", cat_titles[0]).replace("{desc}",
                cat_descs[0]));
        for (int i = 1; i < cat_titles.length; i++) {
            values.append(",").append(template.replace("{name}", cat_titles[i]).replace("{desc}", cat_descs[i]));
        }
        return INIT_CATEGORY_SQL_TEMPLATE.replace("{values}", values);
    }
}
