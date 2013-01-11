package net.vxinwen.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.vxinwen.R;
import net.vxinwen.bean.Category;
import net.vxinwen.bean.News;
import net.vxinwen.db.dao.CategoryDao;
import net.vxinwen.db.dao.NewsDao;
import net.vxinwen.service.SyncNewsService;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

import com.mobeta.android.dslv.DragSortListView;

/**
 * 新闻tag列表页
 * 
 * @author gk23<aoaogk@gmail.com>
 * 
 */
public class MainActivity extends ListActivity {
    private SimpleAdapter simpleAdapter;

    private String[] cat_titles;
    private String[] cat_descs;
    private List<Map<String, Object>> tags;

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            Map<String, Object> item = (Map<String, Object>) simpleAdapter.getItem(from);
            tags.remove(from);
            tags.add(to, item);
            simpleAdapter.notifyDataSetChanged();
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {
            tags.remove(which);
            simpleAdapter.notifyDataSetChanged();
        }
    };

    private DragSortListView.DragScrollProfile ssProfile = new DragSortListView.DragScrollProfile() {
        @Override
        public float getSpeed(float w, long t) {
            if (w > 0.8f) {
                // Traverse all views in a millisecond
                return ((float) simpleAdapter.getCount()) / 0.001f;
            } else {
                return 10.0f * w;
            }
        }
    };

    private OnItemClickListener onItemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, NewsSummaryActivity.class);
            Map<String, Object> item = tags.get(position);
            // 传递
            intent.putExtra("category", (String) item.get("name"));
            MainActivity.this.startActivity(intent);
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        DragSortListView lv = (DragSortListView) getListView();

        lv.setDropListener(onDrop);
        lv.setRemoveListener(onRemove);
        lv.setDragScrollProfile(ssProfile);
        lv.setOnItemClickListener(onItemClick);

        // 从数据库中获得tags内容，赋值给tags变量
        loadTags();
        String[] from = new String[] { "name", "description", "syncCount" };
        int[] to = new int[] { R.id.cat_name, R.id.cat_desc, R.id.syncCount };
        // String[] from = new String[] { "name", "description"};
        // int[] to = new int[] { R.id.cat_name, R.id.cat_desc};
        simpleAdapter = new SimpleAdapter(this, tags, R.layout.list_item_handle_right, from, to);
        setListAdapter(simpleAdapter);
        int size = tags.size();
        if (size > 0) {
            for (int i = 0; i < tags.size(); i++) {
                Map<String, Object> tag = tags.get(i);
                // 每个tag需要单独同步，这样会有一个同步数量动态变化的效果
                SyncNewsTask task = new SyncNewsTask(this, i);
                task.execute((String) tag.get("name"), (Long) tag.get("lastNewsId"));
            }
        }
    }

    /**
     * 加载用户的tag列表数据,同时查出当前每个类别的news的最大ID，用于列表同步用。
     * 
     * @return Category的
     */
    private List<Map<String, Object>> loadTags() {
        tags = new ArrayList<Map<String, Object>>();
        // 从数据库中读取
        CategoryDao cateDao = new CategoryDao();
        List<Category> cates = cateDao.getAll(this);
        Log.d(MainActivity.class.getName(), "cates size is: " + cates.size()
                + ", the first cate is " + cates.get(0).getName());
        cat_titles = new String[cates.size()];
        cat_descs = new String[cates.size()];
        Map<String, Object> map;

        for (int i = 0; i < cates.size(); i++) {
            map = new HashMap<String, Object>();
            map.put("name", cates.get(i).getName());
            map.put("description", cates.get(i).getDesc());
            map.put("syncCount", getString(R.string.sync_msg));
            long lastNewsId = new NewsDao().getLastNewsIdByCategory(this, cates.get(i).getName());
            map.put("lastNewsId", lastNewsId);
            tags.add(map);
        }
        return tags;
    }

    class SyncNewsTask extends AsyncTask<Object, Integer, Integer> {
        private Context context;
        private int position;

        /**
         * @param Context
         *            用于数据库操作时关联的context，默认为本activity
         * @param position
         *            当前tag在tags列表中的位置，用于动态更新对应的同步数据的数量
         * 
         * @param context
         * @param position
         */
        SyncNewsTask(Context context, int position) {
            this.context = context;
            this.position = position;
        }

        /**
         * 同步指定tag的数据
         * 
         * @param params
         *            两个参数，第一个是tagName，第二个是lastNewsId
         * 
         * @return int 某个tag同步的新闻数量,没有更新或者更新失败会返回0
         */
        @Override
        protected Integer doInBackground(Object... params) {
            String tagName = (String) params[0];
            long lastNewsId = (Long) params[1];
            Log.d(SyncNewsTask.class.getName(), "["+tagName+"] Coming in method [doInBackground], the lastId is ["+lastNewsId+"]");
            long s  = System.currentTimeMillis();
            SyncNewsService service = new SyncNewsService();
            Map<String, List<News>> newsMap = service.getNews(new long[] { lastNewsId },
                    new String[] { tagName });
            // 存入数据库
            NewsDao newsDao = new NewsDao();
            // 返回的只有一个tag对应的List<News>
            List<News> newses = newsMap.get(tagName);
            long e = System.currentTimeMillis();
            Log.d(SyncNewsTask.class.getName(), "["+tagName+"] Fetching news from server costs "+(e-s)+"ms, newses size is "+newses.size());
            s  = System.currentTimeMillis();
            boolean isInserted = newsDao.insertBatch(context, newses);
            e = System.currentTimeMillis();
            Log.d(SyncNewsTask.class.getName(), "["+tagName+"] Inserting news into DB costs "+(e-s)+"ms, isInserted is "+isInserted);
            if (isInserted) {
                return newses.size();
            } else {
                return 0;
            }
        }

        protected void onPostExecute(Integer tagSyncCount) {
            Log.d(SyncNewsTask.class.getName(),
                    "coming in method [onPostExecute] with result which size is " + tagSyncCount);
            DragSortListView lv = (DragSortListView) getListView();

            ((HashMap) lv.getItemAtPosition(position)).put("syncCount",
                    String.valueOf(tagSyncCount));
            Log.d(SyncNewsTask.class.getName(), "the sync count is " + tagSyncCount);
            // 通知adapter有数据修改
            simpleAdapter.notifyDataSetChanged();
        }
    }
}
