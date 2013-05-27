package net.vxinwen.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.vxinwen.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ShowImageActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image);
        Intent intent = this.getIntent();
        String image_url = intent.getStringExtra("image_url");
        // set image-view 
        ImageView imageView = (ImageView)findViewById(R.id.oriImage);
        URL picUrl;
        try {
            picUrl = new URL(image_url);
            Bitmap pngBM = BitmapFactory.decodeStream(picUrl.openStream());   
            //  设置大小
            imageView.setImageBitmap(pngBM); 
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        finish();
        return true;
    }
}
