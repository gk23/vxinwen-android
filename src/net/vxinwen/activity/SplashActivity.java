package net.vxinwen.activity;

import net.vxinwen.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * the splash page, the first page when the app is running.
 * 
 * In this activity, some pre-running tasks can be loadded
 * 
 * @author gk23<aoaogk@gmail.com>
 *
 */
public class SplashActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
			    // TODO 添加一些异步加载的程序，加载一些初始化内容，提高加载速度
				Intent intent = new Intent(SplashActivity.this,MainActivity.class);
				SplashActivity.this.startActivity(intent);
				SplashActivity.this.finish();
			}
        }, 5000);
    }
    
}