package net.vxinwen.activity;

import net.vxinwen.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Show all news list of one tag or one category, like news list of "sports"
 * tag.
 * 
 * @author gk23<aoaogk@gmail.com>
 * 
 */
public class ShowBodyNewsActivity extends Activity implements OnTouchListener {
    
    private GestureDetector detector ;
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        super.dispatchTouchEvent(ev);   
        return detector.onTouchEvent(ev); 
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector= new GestureDetector(this,new MySimpleGesture());
        Intent intent = this.getIntent();
        String title = intent.getStringExtra("title");
        String source = intent.getStringExtra("source");
        String publishTime = intent.getStringExtra("publishTime");
        String body=intent.getStringExtra("body");
        setContentView(R.layout.show_body_news);  
        
        TextView titleView = (TextView)this.findViewById(R.id.newsTitle1);
        Log.d(ShowBodyNewsActivity.class.getName(),"the titleView is "+titleView);
        titleView.setText(title);
        TextView soiurceView = (TextView)this.findViewById(R.id.newsSource1);
        soiurceView.setText(source+" "+publishTime);
        WebView bodyView = (WebView)this.findViewById(R.id.newsBody1);
        Log.d(ShowBodyNewsActivity.class.getName(),"the body view is "+bodyView);
        bodyView.loadData(formatBodyToHtml(body),"text/html; charset=UTF-8",null);
    }
    
    private String formatBodyToHtml(String body){
        return "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
        +"<head>"
        +"<meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\" />"
        +"<style type=\"text/css\">"
        +"body{line-height:120%}"
        +"</style>"
        +"</head>"
        +"<body>"
        +body.replaceAll("##", "'")
        +"</body>"
        +"</html>";
    }

//    @Override
//    public boolean onDoubleTap(MotionEvent e) {
//        Log.d(ShowBodyNewsActivity.class.getName(),"coming in onDoubleTap method.");
//        finish();
//        return true;
//    }
//
//    @Override
//    public boolean onDoubleTapEvent(MotionEvent e) {
//        Log.d(ShowBodyNewsActivity.class.getName(),"coming in onDoubleTapEvent method.");
//        //finish();
//        return false;
//    }
//
//    @Override
//    public boolean onSingleTapConfirmed(MotionEvent e) {
//        System.out.println("single tap confirmed.");
//        return false;
//    }

    
    @Override
    public boolean onTouch(View arg0, MotionEvent ev) {
        return detector.onTouchEvent(ev);
    }
    
    private class MySimpleGesture extends SimpleOnGestureListener{
        
        public boolean onDoubleTap(MotionEvent ev){
            Log.d(ShowBodyNewsActivity.class.getName(),"coming in onDoubleTap method.");
            finish();
            return false;
        }
        
//        public boolean onDoubleTapEvent(MotionEvent ev){
//            Log.d(ShowBodyNewsActivity.class.getName(),"coming in onDoubleTapEvent method.");
//            return super.onDoubleTapEvent(ev);
//        }
    }
}
