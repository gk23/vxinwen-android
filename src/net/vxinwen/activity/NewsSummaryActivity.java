package net.vxinwen.activity;

import java.sql.Timestamp;
import java.util.List;

import net.vxinwen.R;
import net.vxinwen.bean.News;
import net.vxinwen.db.dao.NewsDao;
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
    /**
     * 如果当前时间比数据库中最新数据大于INTERVAL_HOURS，则从服务器中更新数据
     */
    private static int INTERVAL_HOURS = 6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.flipper = new ViewFlipper(this);
        this.detector = new GestureDetector(this);
        // 按页显示所有内容
        List<News> newses = getNews();
        Log.d(NewsSummaryActivity.class.getName(), "the newses size is ["
                + (newses == null ? 0 : newses.size()) + "]");
        View layout;
        for (News news : newses) {
            layout = getNewsLayout(news);
            flipper.addView(layout);
        }
        setContentView(flipper);
    }

    private View getNewsLayout(News news) {
        // 实例化布局
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.news_summary, null);
        Log.d(this.getLocalClassName(), "layout of newsDetailLayout is [" + layout + "]");
        // set title
        TextView newsTitle = (TextView) layout.findViewById(R.id.newsTitle);
        Log.d(this.getLocalClassName(), "newsTitle is [" + newsTitle + "]");
        Drawable bg = getResources().getDrawable(R.drawable.bg_title_1);
        Log.d(this.getLocalClassName(), "theopacity of bg picture is [" + bg.getOpacity() + "]");
        newsTitle.setBackgroundDrawable(bg);
        newsTitle.setText(news.getTitle());

        // set source
        TextView source = (TextView) layout.findViewById(R.id.newsSource);
        source.setTextColor(Color.GRAY);
        int summaryWordCount = news.getSummary().length();
        String sourceText = "新浪 " + summaryWordCount + "字 " + news.getPublishTime();
        source.setText(sourceText);

        // set summary
        TextView summary = (TextView) layout.findViewById(R.id.newsSummary);
        summary.setText(news.getSummary());
        return layout;
    }

    /**
     * 获得当前类别最新的news列表
     * 
     * @return 新闻list，如果没有则list.size()==0，list不会为null
     */
    private List<News> getNews() {
        String category = this.getIntent().getStringExtra("category");
        Log.d(NewsSummaryActivity.class.getName(), "[" + category + "]");
        // 1.首先从数据库中找最新的新闻，
        // 2.如果最新新闻与当前时间相差超过6小时，则需要同步信息标识，不能查看信息
        long s = System.currentTimeMillis();
        NewsDao dao = new NewsDao();
        // 数据按照publish_time倒序
        List<News> newses = dao.getByCategory(this, category);
        Log.d(NewsSummaryActivity.class.getName(), "the news size of [" + category + "] is "
                + newses.size());
        long e = System.currentTimeMillis();
        Log.d(NewsSummaryActivity.class.getName(), "[" + category
                + "] Getting news from Sqlite costs " + (e - s) + "ms");

        // 需要从服务器中更新数据,TODO 需要在数据库存入时，判断主要字段如publish_time, summary, title等是否为空
        boolean isUpdate = false;
        long lastNewsId = -1L;
        if (newses == null || newses.size() == 0) {
            isUpdate =true;
        }
//        else{
//            Timestamp time = newses.get(0).getPublishTime();
//            boolean isExceed = isExceedSixHours(time.getTime());
//            Log.d(NewsSummaryActivity.class.getName(), "[" + category + "], the last publish_time is "+time+". Exceeding 6h is "+isExceed);
//            if(isExceed){
//                // TODO 以后应该是最新的pushblish_time中，id最大的那个；
//                lastNewsId = dao.getLastNewsIdByCategory(this, category);
//                isUpdate = true;
//            }
//        }
        if(isUpdate){
            // 1. 从服务器中获取最新新闻
            Log.d(NewsSummaryActivity.class.getName(), "[" + category
                    + "] Fetching news from server.");
            SyncNewsService syncNewsService = new SyncNewsService();
            s = System.currentTimeMillis();
            newses = syncNewsService.getNews(new long[] { lastNewsId }, new String[] { category })
                    .get(category);
            e = System.currentTimeMillis();
            Log.d(NewsSummaryActivity.class.getName(), "[" + category
                    + "] Fetching news from server costs " + (e - s) + "ms");
            // 2. 如果有新的，存入数据库
            // 3. 从数据库中读出，显示。这样不会造成显示顺序混乱。
            
        }
        return newses;
    }

    private static boolean isExceedSixHours(long lastNewsTime) {
        long now = System.currentTimeMillis();
        return (now - lastNewsTime) / 3600 * 1000 > INTERVAL_HOURS;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.detector.onTouchEvent(event);
    }

    /**
     * TODO 1. 首页从左向右拨，激活同步最新信息（从互联网上获得，存入数据库，同时显示） 2.
     * 尾页从右向左拨，查看更多（从数据库中读取下30条，如果没有则到服务器中获取，存入数据库同时显示）
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        if (e1.getX() - e2.getX() > 120) {
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.layout.left_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.layout.left_out));
            this.flipper.showNext();
            result = true;
        } else if (e1.getX() - e2.getX() < -120) {
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.layout.right_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.layout.right_out));
            this.flipper.showPrevious();
            result = true;
        }
        return result;
    }

    /**
     * 长按后，弹出分享功能菜单
     */
    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
}
