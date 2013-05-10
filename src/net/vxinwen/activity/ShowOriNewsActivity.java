package net.vxinwen.activity;

import net.vxinwen.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class ShowOriNewsActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        String url = intent.getStringExtra("url");
        String id = String.valueOf(intent.getLongExtra("id",-1L));
        // 取消标题  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
        // 进行全屏  
  
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        setContentView(R.layout.show_ori_news);  
        // 实例化WebView  
        WebView  webView = (WebView)this.findViewById(R.id.wvOriNews);  
        /** 
         * 调用loadUrl()方法进行加载内容 
         */  
        webView.loadUrl(url);  
        /** 
         * 设置WebView的属性，此时可以去执行JavaScript脚本 
         */  
        webView.getSettings().setJavaScriptEnabled(true);  
    }
}
