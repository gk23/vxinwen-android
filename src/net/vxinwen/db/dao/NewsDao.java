package net.vxinwen.db.dao;

import java.util.ArrayList;
import java.util.List;

import net.vxinwen.bean.News;
import net.vxinwen.db.DBOpenHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NewsDao {
	private final static String TABLE = "news";
	
	public List<News> getByCategory(Context context,long cateId){
		SQLiteDatabase db = new DBOpenHelper(context).getWritableDatabase();
		Cursor cursor = db.query(TABLE, null, "category_id=?", new String[]{cateId+""}, null, null, null);
		List<News> list = new ArrayList<News>();
		News news=null;
		if (cursor.moveToFirst()) {
			do {
				news = new News();
				news.setId(cursor.getLong(cursor.getColumnIndex("id")));
				news.setContent(cursor.getString(cursor.getColumnIndex("content")));
				news.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				news.setSummary(cursor.getString(cursor.getColumnIndex("summary")));
				news.setImageAddress(cursor.getString(cursor.getColumnIndex("image_address")));
				list.add(news);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	public boolean insert(Context context,List<News> newses){
	    
	    return false;
	}
	
	/**
	 * 获得
	 * 
	 * @param context
	 * @param cateId
	 * @return
	 */
	public long getLastNewsIdByCategory(Context context, long cateId){
	    SQLiteDatabase db = new DBOpenHelper(context).getWritableDatabase();
        Cursor cursor=db.rawQuery("select id from news where category_id=? order by id desc limit 1", new String[]{cateId+""});
        long id =-1;
        if(cursor.moveToFirst()){
            id = cursor.getLong(cursor.getColumnIndex("id"));
        }
        return id;
	}
		
}
