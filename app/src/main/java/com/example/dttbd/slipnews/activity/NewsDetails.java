package com.example.dttbd.slipnews.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dttbd.slipnews.R;
import com.example.dttbd.slipnews.db.MyOpenHelper;

/**
 * Created by Dttbd
 */

public class NewsDetails extends AppCompatActivity {
    private ImageView headPic;
    private CollapsingToolbarLayout headName;
    private Toolbar toolbar;
    private FloatingActionButton favoriteFab;
    private MyOpenHelper helper;//打开数据库连接的一个类
    private Cursor cursor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        helper = new MyOpenHelper(this);
        Intent intent = getIntent();
        String Body = intent.getStringExtra("NewsDetail");
        final String Title = intent.getStringExtra("Title");
        final String Image = intent.getStringExtra("Image");
        final String NewsId = intent.getStringExtra("NewsId");
        initView();

        Glide.with(this).load(Image).into(headPic);
        headName.setTitle(Title);
        WebView XQ = (WebView)findViewById(R.id.newsWebView);
        XQ.loadDataWithBaseURL(null,convertZhihuContent(Body), "text/html" , "utf-8", null);

        toolbar = (Toolbar) findViewById(R.id.content_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        favoriteFab = (FloatingActionButton) findViewById(R.id.news_comment_flb);
        if (find(NewsId)){
            favoriteFab.setImageResource(R.drawable.ic_favorite_black_24dp);
        }else{
            favoriteFab.setImageResource(R.drawable.ic_favorite_white_24dp);
        }
        favoriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!find(NewsId)){
                    favoriteFab.setImageResource(R.drawable.ic_favorite_black_24dp);
                    SQLiteDatabase db = helper.getReadableDatabase();//打开本地数据库连接
                    db.execSQL("delete from NewsFocus where NewsFocusId=?", new Object[]{NewsId});//删除相同id的数据
                    ContentValues values = new ContentValues();
                    values.put("NewsFocusId",NewsId);
                    values.put("NewsFocusImageUrl",Image);
                    values.put("NewsFocusTitle",Title);
                    db.insert("NewsFocus", null, values);//插入关注的新闻数据
                    db.close();
                    Toast.makeText(NewsDetails.this,"已关注",Toast.LENGTH_SHORT).show();}
                else {
                    favoriteFab.setImageResource(R.drawable.ic_favorite_white_24dp);
                    SQLiteDatabase db = helper.getReadableDatabase();
                    db.execSQL("delete from NewsFocus where NewsFocusId=?", new Object[]{NewsId});
                    db.close();
                    Toast.makeText(NewsDetails.this,"取消关注",Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private void initView() {
        headPic = (ImageView)findViewById(R.id.content_title_image);
        headName =(CollapsingToolbarLayout)findViewById(R.id.content_collapsing_toolbar);
    }

    private String convertZhihuContent(String preResult) {

        preResult = preResult.replace("<div class=\"img-place-holder\">", "");
        preResult = preResult.replace("<div class=\"headline\">", "");

        // 在api中，css的地址是以一个数组的形式给出，这里需要设置
        // api中还有js的部分，这里不再解析js
        // 不再选择加载网络css，而是加载本地assets文件夹中的css
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/zhihu_daily.css\" type=\"text/css\">";

        String theme = "<body className=\"\" onload=\"onLoaded()\">";

        return new StringBuilder()
                .append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n")
                .append("<head>\n")
                .append("\t<meta charset=\"utf-8\" />")
                .append(css)
                .append("\n</head>\n")
                .append(theme)
                .append(preResult)
                .append("</body></html>").toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //查找新闻ID
    public boolean find(String NewsFocusId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from NewsFocus where NewsFocusId=?",
                new String[] { NewsFocusId });
        boolean result = cursor.moveToNext();
        cursor.close();
        db.close();
        return result;
    }
}
