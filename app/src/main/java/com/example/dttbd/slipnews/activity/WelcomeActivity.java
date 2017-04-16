package com.example.dttbd.slipnews.activity;

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
import com.example.dttbd.slipnews.R;
import com.example.dttbd.slipnews.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.dttbd.slipnews.util.Api.ZHIHU_IMAGE;

public class WelcomeActivity extends AppCompatActivity {

    private ImageView welcomeImage;//获取首页图片实例

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
            loadWelcomePic();
            Glide.with(this).load(welcomePic).into(welcomeImage);
        } else {
            loadWelcomePic();
        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                int waitingTime = 3000; //壁纸hold3秒
                try {
                    while(waitingTime > 0) {
                        sleep(100);
                        waitingTime -= 100;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);  //进入MainActivity
                }
            }
        };

        thread.start();
    }

    //获取知乎首页壁纸
    private void loadWelcomePic() {

        //知乎首页壁纸json API
        final String imgUrl = ZHIHU_IMAGE;

        HttpUtil.sendOkHttpRequest(imgUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String welcomePic = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data1 = new JSONObject(welcomePic);
                            JSONArray Creatives = data1.optJSONArray("creatives");
                            JSONObject data2 = Creatives.getJSONObject(0);
                            String url = data2.getString("url");
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WelcomeActivity.this).edit();
                            editor.putString("welcome_pic", url);
                            editor.apply();
                            Glide.with(WelcomeActivity.this).load(url).into(welcomeImage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

}
