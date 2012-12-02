package net.vxinwen.activity;

import net.vxinwen.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Show one news summary.
 * 
 * @author Administrator
 *
 */
public class NewsSummaryActivity extends Activity {
	private TextView textView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_summary);
        
        textView = (TextView)findViewById(R.id.summary);
        String title =this.getIntent().getStringExtra("title");
        
        textView.setText("这应该是"+title+"的内容");
    }
}
