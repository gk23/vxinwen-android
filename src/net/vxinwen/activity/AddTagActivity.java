package net.vxinwen.activity;

import net.vxinwen.R;
import android.app.Activity;
import android.os.Bundle;

/**
 * 当用户点击添加时，显示此页面。可以添加一个主题
 * 
 * @author Administrator
 *
 */
public class AddTagActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tag);
    }
}
