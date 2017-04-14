package com.example.dttbd.slipnews;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dttbd.slipnews.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private ListView newsListView;
    private ZHihuNewsAdapter zhadapter;
    private ArrayList<ZhihunewsDataBean> nesData;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取Toolbar实例，设置Toolbar为actionBar显示
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //设置导航按钮为展开菜单功能
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }

        //获取CollapsingToolbarLayout实例
//        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
//                findViewById(R.id.collapsing_toolbar);
//        //设置CollapsingToolbarLayout不显示标题，则标题会再toolbar上显示
//        collapsingToolbarLayout.setTitleEnabled(false);

        //获取NavigationView实例
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);//将主页项默认选中
        //处理NavigationView点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_favorite:
                        Toast.makeText(MainActivity.this, "fav", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_history:
                        Toast.makeText(MainActivity.this, "his", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settings:
                        Toast.makeText(MainActivity.this, "set", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.about:
                        Toast.makeText(MainActivity.this, "about", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                }
                return true;
            }
        });


        //获取FloatingActionButton实例
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //FloatingActionButton点击监听
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "FAB", Toast.LENGTH_SHORT).show();
            }
        });

        //实现滑动导航
        //获取TabLayout实例
//        TabLayout tabLayout= (TabLayout)findViewById(R.id.tab_layout);
//        tabLayout.addTab(tabLayout.newTab().setText(getResources().getText(R.string.title_activity_zhihuDaily)));
//        tabLayout.addTab(tabLayout.newTab().setText(getResources().getText(R.string.title_activity_guokr_read)));
//        tabLayout.addTab(tabLayout.newTab().setText(getResources().getText(R.string.title_activity_douban)));

        //获取ListView实例
        newsListView= (ListView) findViewById(R.id.list_view);
        getNewsData();
        zhadapter = new ZHihuNewsAdapter(this);
        newsListView.setAdapter(zhadapter);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getNewsDetails(nesData.get(position).id);
            }
        });

        //下拉刷新监听
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNews();
            }
        });

    }

    //下拉刷新函数
    private void refreshNews() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNewsData();
                        zhadapter = new ZHihuNewsAdapter(MainActivity.this);
                        newsListView.setAdapter(zhadapter);
                        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                getNewsDetails(nesData.get(position).id);
                            }
                        });
                        zhadapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }


    //加载toolbar布局菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
    //监听toolbar点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.brightness:
                Toast.makeText(this, "You clickes brightness", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    private void getNewsDetails(int id) {
        final String NewsDetailsUrl = "http://news-at.zhihu.com/api/4/news/"+id;
        HttpUtil.sendOkHttpRequest(NewsDetailsUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            Toast.makeText(ZHihuNews.this,jsonData,Toast.LENGTH_LONG).show();
                            JSONObject data = new JSONObject(jsonData);
                            String body = data.optString("body");
                            String title = data.optString("title");
                            String image = data.optString("image");
                            Intent intent = new Intent(MainActivity.this,NewsDetails.class);
                            intent.putExtra("NewsDetail",body);
                            intent.putExtra("Title",title);
                            intent.putExtra("Image",image);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void getNewsData() {
        final String NewsUrl = "http://news-at.zhihu.com/api/4/news/latest";
        HttpUtil.sendOkHttpRequest(NewsUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data = new JSONObject(jsonData);
                            String time = data.optString("date");//新闻时间
                            JSONArray stories = data.optJSONArray("stories");
                            Log.d("stories",String.valueOf(stories));
                            parseJsonData(stories);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void parseJsonData(JSONArray stories) throws JSONException {
        if (nesData == null) {
            nesData = new ArrayList<ZhihunewsDataBean>();
        } else {
            nesData.clear();
        }
        for (int i = 0;i<stories.length();i++){
            ZhihunewsDataBean  datebean = new ZhihunewsDataBean();
            JSONObject data = stories.getJSONObject(i);
            JSONArray img = data.optJSONArray("images");
            datebean.images = img.getString(0);
            datebean.id = data.optInt("id");
            datebean.title = data.optString("title");
            datebean.type = data.optString("type");
            nesData.add(datebean);
        }
        printInListView();
    }

    private void printInListView() {
        if (nesData.size()>0){
            zhadapter.setData(nesData);
        }
    }


}
