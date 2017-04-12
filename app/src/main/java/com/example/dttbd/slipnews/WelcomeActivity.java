package com.example.dttbd.slipnews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.dttbd.slipnews.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WelcomeActivity extends AppCompatActivity {

    private ImageView welcomeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_welcome);

        welcomeImage = (ImageView) findViewById(R.id.welcome_image);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String welcomePic = prefs.getString("welcome_pic", null);
        if (welcomePic != null) {
            Glide.with(this).load(welcomePic).into(welcomeImage);
        } else {
            loadWelcomePic();
        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                int waitingTime = 3000; // ms
                try {
                    while(waitingTime > 0) {
                        sleep(100);
                        waitingTime -= 100; // 100ms per time
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);  // enter the main activity finally
                }
            }
        };

        thread.start();
    }

    //获取图片
    private void loadWelcomePic() {
        String requestPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String welcomePic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WelcomeActivity.this).edit();
                editor.putString("welcome_pic", welcomePic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WelcomeActivity.this).load(welcomePic).into(welcomeImage);
                    }
                });
            }
        });
    }
}
