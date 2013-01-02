package net.vxinwen.activity;

import java.util.List;
import java.util.Map;

import net.vxinwen.R;
import net.vxinwen.bean.News;
import net.vxinwen.service.SyncNewsService;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * The page to show the summary of one news.
 * 
 * @author gk23<aoaogk@gmail.com>
 * 
 */
public class NewsSummaryActivity extends Activity implements OnGestureListener {
    private ViewFlipper flipper;
    private GestureDetector detector;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.flipper = new ViewFlipper(this);
        this.detector = new GestureDetector(this);
        // 按页显示所有内容
        List<News> newses = getNews();
        Log.d(NewsSummaryActivity.class.getName(), "the newses size is ["+newses.size()+"]");
        View layout;
        for(News news:newses){
            layout = getNewsLayout(news);
            flipper.addView(layout);
        }
        setContentView(flipper);
    }
    
    
    
    private View getNewsLayout(News news) {
        // 实例化布局
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);  
        View layout = inflater.inflate(R.layout.news_summary,null);
        Log.d(this.getLocalClassName(), "layout of newsDetailLayout is ["+layout+"]");
        // set title
        TextView newsTitle = (TextView)layout.findViewById(R.id.newsTitle);
        Log.d(this.getLocalClassName(), "newsTitle is ["+newsTitle+"]");
        Drawable bg = getResources().getDrawable(R.drawable.bg_title_1);
        Log.d(this.getLocalClassName(), "theopacity of bg picture is ["+bg.getOpacity()+"]");
        newsTitle.setBackgroundDrawable(bg);
        newsTitle.setText(news.getTitle());
        
        // set source 
        TextView source =  (TextView)layout.findViewById(R.id.newsSource);
        source.setTextSize(10);
        source.setTextColor(Color.GRAY);
        int summaryWordCount = news.getSummary().length();
        String sourceText = "新浪 "+summaryWordCount+"字 "+news.getPublishTime();
        source.setText(sourceText);
        
        // set summary
        TextView summary =  (TextView)layout.findViewById(R.id.newsSummary);
        summary.setText(news.getSummary());
        return layout;
    }

    /**
     * 获得当前类别最新的news列表
     * 
     * @return 新闻list，如果没有则list.size()==0，list不会为null
     */
    private List<News> getNews(){
        String name = this.getIntent().getStringExtra("name");
        long lastNewsId = this.getIntent().getLongExtra("lastNewsId", -1L);
        Log.d(NewsSummaryActivity.class.getName(), "the name is ["+name+"], the lastNewsId is ["+lastNewsId+"]");
        // 如果-1则同步新闻
        SyncNewsService syncNewsService = new SyncNewsService();
        Map<String,List<News>> newses =  syncNewsService.getNews(new long[] { lastNewsId }, new String[] { name });
        
        return newses.get(name);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.detector.onTouchEvent(event);
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        if (e1.getX() - e2.getX() > 120) {
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.layout.left_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.layout.left_out));
            this.flipper.showNext();
            result= true;
        } else if (e1.getX() - e2.getX() < -120) {
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.layout.right_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.layout.right_out));
            this.flipper.showPrevious();
            result= true;
        }
        return result;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }
}
