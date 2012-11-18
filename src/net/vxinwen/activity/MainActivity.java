package net.vxinwen.activity;

import java.util.ArrayList;
import java.util.Arrays;

import net.vxinwen.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.mobeta.android.dslv.DragSortListView;

public class MainActivity extends ListActivity {
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//	}
    private ArrayAdapter<String> adapter;

    private String[] array;
    private ArrayList<String> list;

    private DragSortListView.DropListener onDrop =
        new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                String item=adapter.getItem(from);

                adapter.notifyDataSetChanged();
                adapter.remove(item);
                adapter.insert(item, to);
            }
        };

    private DragSortListView.RemoveListener onRemove = 
        new DragSortListView.RemoveListener() {
            @Override
            public void remove(int which) {
                adapter.remove(adapter.getItem(which));
            }
        };

    private DragSortListView.DragScrollProfile ssProfile =
        new DragSortListView.DragScrollProfile() {
            @Override
            public float getSpeed(float w, long t) {
                if (w > 0.8f) {
                    // Traverse all views in a millisecond
                    return ((float) adapter.getCount()) / 0.001f;
                } else {
                    return 10.0f * w;
                }
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

        array = getResources().getStringArray(R.array.countries);
        list = new ArrayList<String>(Arrays.asList(array));

        adapter = new ArrayAdapter<String>(this, R.layout.list_item_handle_right, R.id.text, list);
        setListAdapter(adapter);
    }
}
