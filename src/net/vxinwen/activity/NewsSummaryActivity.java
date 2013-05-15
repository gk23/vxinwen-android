package net.vxinwen.activity;

import java.util.List;

import net.vxinwen.R;
import net.vxinwen.bean.News;
import net.vxinwen.db.dao.NewsDao;
import net.vxinwen.service.SyncNewsService;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
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
public class NewsSummaryActivity extends Activity implements OnGestureListener,OnDoubleTapListener {
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
        if (newses == null || newses.size() == 0) {
            NewsSummaryActivity.this.finish();
            return;
        }
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
        Drawable bg = getResources().getDrawable(getRandomBackGround());

        Log.d(this.getLocalClassName(), "theopacity of bg picture is [" + bg.getOpacity() + "]");
        newsTitle.setBackgroundDrawable(bg);
        newsTitle.setText(news.getTitle());

        // set source
        TextView source = (TextView) layout.findViewById(R.id.newsSource);
        source.setTextColor(Color.GRAY);
        int summaryWordCount = news.getSummary().length();
        String sourceText = news.getSource()+"  " + summaryWordCount + "字   " + news.getPublishTime();
        source.setText(sourceText);

        // set summary
        TextView summary = (TextView) layout.findViewById(R.id.newsSummary);
        //summary.setMovementMethod(ScrollingMovementMethod.getInstance());
        summary.setText(news.getSummary());
        // 存储news数据，用于标识当前显示的view是哪一个
        layout.setTag(news);
        return layout;
    }

    private int getRandomBackGround() {
        int[] bgs = new int[] { R.drawable.bg_title_x_6, R.drawable.bg_title_x_7,
                R.drawable.bg_title_x_8 };
        int index = (int) (Math.random() * bgs.length);
        return bgs[index];
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
        
        // TODO BaseDao.getService
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
            isUpdate = true;
        }
        // else{
        // Timestamp time = newses.get(0).getPublishTime();
        // boolean isExceed = isExceedSixHours(time.getTime());
        // Log.d(NewsSummaryActivity.class.getName(), "[" + category +
        // "], the last publish_time is "+time+". Exceeding 6h is "+isExceed);
        // if(isExceed){
        // // TODO 以后应该是最新的pushblish_time中，id最大的那个；
        // lastNewsId = dao.getLastNewsIdByCategory(this, category);
        // isUpdate = true;
        // }
        // }
        if (isUpdate) {
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
     * 
     * @param e1
     *            划动初始的接触点
     * @param e2
     *            划动结束的接触点
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        if (e1.getX() - e2.getX() > 120) { // 从右向左划，显示前一条
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.layout.left_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.layout.left_out));
            this.flipper.showNext();
            result = true;
        } else if (e1.getX() - e2.getX() < -120) { // 从左向右划，显示前一条
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.layout.right_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.layout.right_out));
            this.flipper.showPrevious();
            result = true;
        } else if (e1.getY() - e2.getY() > 120) { // 从下往上划，返回列表
            result = true;
        } else if (e1.getY() - e2.getY() < -120) { // 从上往下划，显示全文
            Intent intent = new Intent(NewsSummaryActivity.this, ShowOriNewsActivity.class);
            News news = (News) flipper.getCurrentView().getTag();
            intent.putExtra("id", news.getId());
            intent.putExtra("url", news.getUrl());
            NewsSummaryActivity.this.startActivity(intent);
            result = true;
        }
        return result;
    }

    /**
     * 长按后，弹出分享功能菜单
     */
    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(NewsSummaryActivity.class.getName(), "long press.");
    }
    

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        offset = offset- distanceX;  
//        //确保不滑出界  
//        if(offset>0){  
//            offset=0;  
//        }  
//       else if(offset < (getChildCount()-numColumns)*unitWidth*-1) {  
//            offset = (getChildCount()-numColumns)*unitWidth*-1;  
//        }  
//        //重绘布局  
//        requestLayout();  
  
        return false;  
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        News news = (News)flipper.getCurrentView().getTag();
        Intent intent = new Intent(NewsSummaryActivity.this,ShowBodyNewsActivity.class);
        intent.putExtra("title",news.getTitle());
        intent.putExtra("body", news.getBody());
        NewsSummaryActivity.this.startActivity(intent);
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }
}
