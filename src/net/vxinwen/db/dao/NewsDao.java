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
     * 每次返回新闻摘要的条数
     */
    private static String MAX_RESULT_COUNT="30"; 
    /**
     * 待插入的字段和顺序，顺序如果修改，则toNewsArray方法也需要相应修改
     * TODO body格式有换行，单引号，逗号等导致insert操作失败，需要先预处理。
     */
    private final static String[] COLUMNS_INSERTED = new String[] {"id","title","summary",
            "image_address", "url", "publish_time", "category","source","body"};
    private final static int COLUMN_INSERTED_COUNT = COLUMNS_INSERTED.length;
    
    /**
     * 
     * 
     * @param context
     * @param category
     * @return 可能为null或size为0
     */
    public List<News> getByCategory(Context context, String category) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new DBOpenHelper(context).getWritableDatabase();
            String sql = "select * from news where category=? order by publish_time desc limit "+MAX_RESULT_COUNT;
            cursor = db.rawQuery(sql, new String[] { category });
//            cursor = db.query(TABLE, null, "category=?", new String[] { category }, null, null,
//                    "publish_time desc",MAX_RESULT_COUNT);
            List<News> list = new ArrayList<News>();
            News news = null;
            if (cursor.moveToFirst()) {
                do {
                    news = new News();
                    news.setId(cursor.getLong(cursor.getColumnIndex("id")));
                    news.setBody(cursor.getString(cursor.getColumnIndex("body")));
                    news.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    news.setSummary(cursor.getString(cursor.getColumnIndex("summary")));
                    news.setImage(cursor.getString(cursor.getColumnIndex("image_address")));
                    news.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                    news.setSource(cursor.getString(cursor.getColumnIndex("source")));
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
        if (newses == null || newses.size() == 0)
            return false;
        Log.d(NewsDao.class.getName(), "Coming in method insertBatch. Category is "
                + newses.get(0).getCategory());
        // 实现插入，有多线程插入News的现象
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new DBOpenHelper(context).getWritableDatabase();
            db.beginTransaction();
            String sql = getInsertBatchSql(TABLE, COLUMNS_INSERTED, toNewsArray(newses));
            db.execSQL(sql);
            db.setTransactionSuccessful(); 
            db.endTransaction();
            Log.d(NewsDao.class.getName(), "insertBatch. Category is "
                    + newses.get(0).getCategory());
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
    private Object[][] toNewsArray(List<News> newses) {
        int size = 0;
        if (newses == null || (size = newses.size()) == 0)
            return null;
        Object[][] newsArray = new Object[size][];
        News news = null;
        for (int i = 0; i < size; i++) {
            newsArray[i] = new Object[COLUMN_INSERTED_COUNT];
            news = newses.get(i);
            newsArray[i][0] = (Long)news.getId();
            newsArray[i][1] = news.getTitle();
            newsArray[i][2] = news.getSummary();
            newsArray[i][3] = news.getImage();
            newsArray[i][4] = news.getUrl();
            newsArray[i][5] = TimestampUtil.timeStampToString(news.getPublishTime());
            newsArray[i][6] = news.getCategory();
            newsArray[i][7] = news.getSource();
            String body = news.getBody().replaceAll("'", "##");
            newsArray[i][8] = body;
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
    private String getInsertBatchSql(String tableName, String[] columns, Object[][] values) {
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
            //  每条数据第一个是id的值，是整数，不用单引号，其他都为字符串
            valuePart.append(" UNION ALL SELECT ").append(values[i][0]);
            for (int j = 1; j < columns.length; j++) {
                valuePart.append(",'").append(values[i][j]).append("'");
            }
        }
        String valueString = valuePart.toString().replaceFirst("UNION ALL", "");
        res = res.replace("{values}", valueString);
        Log.d(NewsDao.class.getName(), "the insert sql is [ " + res + " ]");
        return res;
    }

    /**
     * 获得
     * 
     * @param context
     * @param cateId
     * @return
     */
    public long getLastNewsIdByCategory(Context context, String cate) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new DBOpenHelper(context).getWritableDatabase();
            cursor = db.rawQuery("select id from news where category=? order by id desc limit 1",
                    new String[] { cate });
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
