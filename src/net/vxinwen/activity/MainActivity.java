package net.vxinwen.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.vxinwen.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.mobeta.android.dslv.DragSortListView;

public class MainActivity extends ListActivity {
    private SimpleAdapter simpleAdapter;

    private String[] cat_titles;
    private String[] cat_descs;
    private List<Map<String,Object>> data;

    private DragSortListView.DropListener onDrop =
        new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                Map<String,Object> item=(Map<String,Object>)simpleAdapter.getItem(from);
                data.remove(from);
                data.add(to,item);
                simpleAdapter.notifyDataSetChanged();
            }
        };

    private DragSortListView.RemoveListener onRemove = 
        new DragSortListView.RemoveListener() {
            @Override
            public void remove(int which) {
            	data.remove(which);
            	simpleAdapter.notifyDataSetChanged();
            }
        };

    private DragSortListView.DragScrollProfile ssProfile =
        new DragSortListView.DragScrollProfile() {
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

    private OnItemClickListener onItemClick = 
    	new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MainActivity.this,NewsSummaryActivity.class);
				Map<String,Object> item = data.get(position);
				
				intent.putExtra("title", (String)item.get("title"));
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
        
        List<Map<String,Object>> data =  loadData();
        String[] from = new String[]{"title","desc","img"};
        int[] to = new int[]{R.id.cat_name,R.id.cat_desc,R.id.img};
        simpleAdapter = new SimpleAdapter(this, data, R.layout.list_item_handle_right, from, to);
        setListAdapter(simpleAdapter);
    }

	private List<Map<String,Object>>  loadData() {
		data = new ArrayList<Map<String,Object>>();
		cat_titles = getResources().getStringArray(R.array.category_names);
		cat_descs = getResources().getStringArray(R.array.category_descs);
		Map<String,Object> map ;
		for(int i=0;i<cat_descs.length;i++){
			map = new HashMap<String,Object>();
			map.put("title", cat_titles[i]);
			map.put("desc", cat_descs[i]);
			map.put("img",R.drawable.icon);
			data.add(map);
		}
		return data;
	}
}
