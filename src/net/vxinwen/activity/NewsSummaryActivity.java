package net.vxinwen.activity;

import net.vxinwen.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * The page to show the summary of one news.
 * 
 * @author gk23<aoaogk@gmail.com>
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
