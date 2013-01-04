package net.vxinwen.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.vxinwen.R;
import net.vxinwen.bean.Category;
import net.vxinwen.db.dao.CategoryDao;
import net.vxinwen.db.dao.NewsDao;
import android.app.ListActivity;
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
    private List<Map<String, Object>> data;

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            Map<String, Object> item = (Map<String, Object>) simpleAdapter.getItem(from);
            data.remove(from);
            data.add(to, item);
            simpleAdapter.notifyDataSetChanged();
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {
            data.remove(which);
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
            Map<String, Object> item = data.get(position);
            // 传递
            intent.putExtra("name", (String) item.get("name"));
            intent.putExtra("lastNewsId", (Long) item.get("lastNewsId"));
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

        List<Map<String, Object>> data = loadTags();
        String[] from = new String[] { "name", "description", "img" };
        int[] to = new int[] { R.id.cat_name, R.id.cat_desc, R.id.img };
        simpleAdapter = new SimpleAdapter(this, data, R.layout.list_item_handle_right, from, to);
        setListAdapter(simpleAdapter);
        int size = data.size();
        if (size > 0) {
            String[] tagNames = new String[size];
            long[] lastNewsIds = new long[size];
            for (int i = 0; i < data.size(); i++) {
                Map<String, Object> tag = data.get(i);
                tagNames[i] = (String) tag.get("name");
                lastNewsIds[i] = (Long) tag.get("lastNewsId");
            }
            SyncNewsTask task = new SyncNewsTask();
            task.execute(tagNames, lastNewsIds);
        }
    }

    /**
     * 加载用户的tag列表数据
     * 
     * @return Category的
     */
    private List<Map<String, Object>> loadTags() {
        data = new ArrayList<Map<String, Object>>();
        // 从数据库中读取
        CategoryDao cateDao = new CategoryDao();
        List<Category> cates = cateDao.getAll(this);
        Log.d(MainActivity.class.getName(), "cates size is: " + cates.size() + ", the first cate is "
                + cates.get(0).getName());
        cat_titles = new String[cates.size()];
        cat_descs = new String[cates.size()];
        Map<String, Object> map;
        for (int i = 0; i < cates.size(); i++) {
            map = new HashMap<String, Object>();
            map.put("name", cates.get(i).getName());
            map.put("description", cates.get(i).getDesc());
            // TODO 添加未读数背景，去掉此图片
            map.put("img", R.drawable.sync);
            long lastNewsId = new NewsDao().getLastNewsIdByCategory(this, cates.get(i).getId());
            map.put("lastNewsId", lastNewsId);
            data.add(map);
        }
        return data;
    }

    class SyncNewsTask extends AsyncTask<Object, Integer, int[]> {

        /**
         * 同步所有tags的数据
         * 
         * @return int[] 每个元素为某个tag同步的新闻数量
         */
        @Override
        protected int[] doInBackground(Object... params) {
            String[] tagNames = (String[]) params[0];
            long[] lastNewsIds = (long[]) params[1];

            // TODO 设置每个tag的同步数据图片为进行图片,需要闪烁的图片

            return null;
        }

        protected void onPostExecute(int[] result) {
            if (result == null)
                return;
            for (int i = 0; i < result.length; i++) {
                // TODO 设置每个tag的新闻个数，并设置图片

            }
        }
    }
}
