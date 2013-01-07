package net.vxinwen.db.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import net.vxinwen.bean.News;
import net.vxinwen.db.DBOpenHelper;
import net.vxinwen.util.TimestampUtil;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NewsDao {
    private final static String TABLE = "news";
    /**
     * 待插入的字段和顺序，顺序如果修改，则toNewsArray方法也需要相应修改
     */
    private final static String[] COLUMNS_INSERTED = new String[] { "title", "summary",
            "image_address", "url", "publish_time", "category" };
    private final static int COLUMN_INSERTED_COUNT = COLUMNS_INSERTED.length;

    public List<News> getByCategory(Context context, String category) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new DBOpenHelper(context).getWritableDatabase();
            cursor = db.query(TABLE, null, "category=?", new String[] { category }, null, null,
                    null);
            List<News> list = new ArrayList<News>();
            News news = null;
            if (cursor.moveToFirst()) {
                do {
                    news = new News();
                    news.setId(cursor.getLong(cursor.getColumnIndex("id")));
                    news.setContent(cursor.getString(cursor.getColumnIndex("content")));
                    news.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    news.setSummary(cursor.getString(cursor.getColumnIndex("summary")));
                    news.setImageAddress(cursor.getString(cursor.getColumnIndex("image_address")));
                    news.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                    Timestamp publishTime = TimestampUtil.stringToTimeStamp(cursor.getString(cursor
                            .getColumnIndex("publish_time")));
                    news.setPublishTime(publishTime);
                    news.setCategory(category);
                    list.add(news);
                } while (cursor.moveToNext());
            }
            return list;
        } finally {
            DBOpenHelper.close(cursor, db);
        }
    }

    /**
     * 批量插入，sqlite不支持insert into category (name,description) values
     * ('',''),('','')的格式，只支持 insert into category (name,description) select
     * '要闻','要闻' union all select '体育','体育咨询' union all select 'test','test'…
     * 
     * @param context
     * @param newses
     * @return
     */
    public boolean insertBatch(Context context, List<News> newses) {
        // 实现插入，有多线程插入News的现象
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db.beginTransaction();
            db = new DBOpenHelper(context).getWritableDatabase();
            String sql = getInsertBatchSql(TABLE, COLUMNS_INSERTED, toNewsArray(newses));
            db.execSQL(sql);
            db.endTransaction();
            return true;
        } catch (SQLException e) {
            Log.e(NewsDao.class.getName(), e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBOpenHelper.close(cursor, db);
        }
    }

    /**
     * 
     * @param newses
     * @return
     */
    private String[][] toNewsArray(List<News> newses) {
        int size = 0;
        if (newses == null || (size = newses.size()) == 0)
            return null;
        String[][] newsArray = new String[size][];
        News news = null;
        for (int i = 0; i < size; i++) {
            newsArray[i] = new String[COLUMN_INSERTED_COUNT];
            news = newses.get(i);
            newsArray[i][0] = news.getTitle();
            newsArray[i][1] = news.getSummary();
            newsArray[i][2] = news.getImageAddress();
            newsArray[i][3] = news.getUrl();
            newsArray[i][4] = TimestampUtil.timeStampToString(news.getPublishTime());
            newsArray[i][5] = news.getCategory();
        }
        return newsArray;
    }

    /**
     * 拼接成：insert into category (name,description) select '要闻','要闻' union all
     * select '体育','体育咨询' union all select 'test','test'…
     * 
     * 要求每个字段的类型都为字符串或timestamp类型，目前没有处理Integer类型，所有值都加了单引号
     * 
     * @param tableName
     * @param columns
     *            要插入的字段数组
     * @param values
     *            二维数组，values[3][2],表示插入3条，每条插入2个字段；values[0]=(
     *            '要闻','要闻'),values[1]=('体育','体育咨询')...
     * @return 拼接好的sql语句。
     */
    private String getInsertBatchSql(String tableName, String[] columns, String[][] values) {
        if (tableName == null || tableName.trim().length() == 0)
            return null;
        if (columns.length == 0)
            return null;
        if (values == null || values.length == 0)
            return null;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null || values[i].length != columns.length)
                return null;
        }
        String template = "INSERT INTO {tableName} ({columns}) {values}";
        String res = template.replace("{tableName}", tableName);

        // 替换Column字段
        String cols = columns[0];
        for (int i = 1; i < columns.length; i++) {
            cols += "," + columns[i];
        }
        res = res.replace("{columns}", cols);

        // 替换VALUES字段
        StringBuilder valuePart = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            valuePart.append(" UNION ALL SELECT '").append(values[i][0]).append("'");
            for (int j = 1; j < columns.length; j++) {
                valuePart.append(",'").append(values[i][j]).append("'");
            }
        }
        String valueString = valuePart.toString().replaceFirst("UNION ALL", "");
        res.replace("{values}", valueString);
        // // 替换column字段
        // String cols = columns[0];
        // for (int i = 1; i < columns.length; i++) {
        // cols += "," + columns[i];
        // }
        // res = res.replace("{columns}", cols);
        //
        // // 替换values
        // String initVals = "SELECT '"+values[0][0];
        //
        // for(int i=1;i<columns.length;i++){
        // initVals+=values[0][i];
        // }
        //
        // String template = " UNION ALL SELECT '{name}','{desc}'";
        // String init = "SELECT '"+cat_titles[0]+"','"+cat_descs[0]+"'";
        return null;
    }

    /**
     * 获得
     * 
     * @param context
     * @param cateId
     * @return
     */
    public long getLastNewsIdByCategory(Context context, long cateId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new DBOpenHelper(context).getWritableDatabase();
            cursor = db.rawQuery(
                    "select id from news where category_id=? order by id desc limit 1",
                    new String[] { cateId + "" });
            long id = -1;
            if (cursor.moveToFirst()) {
                id = cursor.getLong(cursor.getColumnIndex("id"));
            }
            return id;
        } finally {
            DBOpenHelper.close(cursor, db);
        }
    }

}
