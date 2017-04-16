package com.example.dttbd.slipnews.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dttbd.slipnews.R;
import com.example.dttbd.slipnews.about.aboutActivity;
import com.example.dttbd.slipnews.adapter.ZHihuNewsAdapter;
import com.example.dttbd.slipnews.adapter.ZhihunewsDataBean;
import com.example.dttbd.slipnews.db.MyOpenHelper;
import com.example.dttbd.slipnews.setting.settingActivity;
import com.example.dttbd.slipnews.util.DateFormatter;
import com.example.dttbd.slipnews.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.dttbd.slipnews.util.Api.ZHIHU_HISTORY;
import static com.example.dttbd.slipnews.util.Api.ZHIHU_NEWS;


public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ZHihuNewsAdapter zhadapter;
    private ArrayList<ZhihunewsDataBean> nesData;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Calendar calendar; // 通过Calendar获取系统时间
    private MyOpenHelper helper;//打开数据库连接的一个类
    private Cursor cursor;//游标
    private TextView noRecord;//提示历史记录
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //实例化数据库
        helper = new MyOpenHelper(this);
        //实例化ListView
        listView = (ListView) findViewById(R.id.list_view);
        //实例化提示信息
        noRecord = (TextView) findViewById(R.id.noRecord);


        //初始化主页
        initMain();

        //获取NavigationView实例
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);//将主页项默认选中
        //实例化主页抽屉布局
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //处理NavigationView点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        initMain();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_favorite:
                        //下拉刷新监听
                        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
                        swipeRefreshLayout.setEnabled(true);
                        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                displayAllFocus();
                                CheckNoFocusNews();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                        toolbar.setTitle(R.string.nav_favorite);
                        FavoriteFab();
                        displayAllFocus();
                        CheckNoFocusNews();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_history:
                        displayAllHistory();
                        CheckNoHistory();
                        toolbar.setTitle(R.string.nav_history);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        Intent settingIntent = new Intent(MainActivity.this, settingActivity.class);
                        startActivity(settingIntent);
                        break;
                    case R.id.about:
                        Intent aboutIntent = new Intent(MainActivity.this, aboutActivity.class);
                        startActivity(aboutIntent);
                        break;
                    default:
                }
                return true;
            }
        });


    }

    //初始化主页
    private void initMain() {


        //屏蔽历史和收藏界面的提示信息
        noRecord = (TextView) findViewById(R.id.noRecord);
        noRecord.setVisibility(View.GONE);

        listView.setVisibility(View.VISIBLE);

        //获取Toolbar实例，设置Toolbar为actionBar显示
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.nav_home);
        setSupportActionBar(toolbar);
        //设置HomeAsUp按钮功能为展开菜单功能
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }

        //展示主页内容
        getNewsData(ZHIHU_NEWS + "latest");
        zhadapter = new ZHihuNewsAdapter(MainActivity.this);
        listView.setAdapter(zhadapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getNewsDetails(nesData.get(position).id);
                addHistory(position);

            }
        });

        //下拉刷新监听
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNews();
            }
        });

        MainFab();
    }

    //---------------------------功能函数-------------------------------------//

    //下拉刷新函数
    private void refreshNews() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNewsData(ZHIHU_NEWS + "latest");
                        zhadapter = new ZHihuNewsAdapter(MainActivity.this);
                        listView.setAdapter(zhadapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                getNewsDetails(nesData.get(position).id);
                                addHistory(position);
                            }
                        });
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

    //根据文章ID拉取文章详细页面内容
    private void getNewsDetails(final int id) {

        //文章API格式
        final String NewsDetailsUrl = ZHIHU_NEWS + id;

        HttpUtil.sendOkHttpRequest(NewsDetailsUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Toast.makeText(MainActivity.this, "获取失败，请检查网络重试！", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //获取返回的json数据
                final String jsonData = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            //解析json数据
                            JSONObject data = new JSONObject(jsonData);
                            String body = data.optString("body");
                            String title = data.optString("title");
                            String image = data.optString("image");
                            //获取到信息并传入新闻详情页面
                            Intent intent = new Intent(MainActivity.this,NewsDetails.class);
                            intent.putExtra("NewsDetail",body);//HTML代码
                            intent.putExtra("Title",title);//新闻标题
                            intent.putExtra("Image",image);//新闻主图
                            intent.putExtra("NewsId",id+"");//新闻ID
                            startActivity(intent);//显示intent打开详情页
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    //传入新闻API，获取新闻列表
    private void getNewsData(String API) {

        HttpUtil.sendOkHttpRequest(API, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Toast.makeText(MainActivity.this, "获取失败，请检查网络重试！", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //解析json数据
                final String jsonData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data = new JSONObject(jsonData);
                            String time = data.optString("date");//新闻时间
                            JSONArray stories = data.optJSONArray("stories");//新闻信息
                            parseJsonData(stories);//解析新闻数据
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    //解析新闻数据
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

    //设置ListView适配器数据
    private void printInListView() {
        if (nesData.size()>0){
            zhadapter.setData(nesData);
        }
    }

    //显示历史记录
    private void displayAllHistory(){

        swipeRefreshLayout.setEnabled(false);
        HistoryFab();

        cursor = helper.getReadableDatabase().query("NewsHistory",new String[]{"_id","NewsId","NewsImgUrl","NewsTitle"},null,null,null,null,"NewsId,NewsImgUrl,NewsTitle desc");

        CursorAdapter historyAdapter = new CursorAdapter(this,cursor) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                @SuppressLint("InflateParams") View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.news_item,null);
                return view;
            }
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ImageView historyimg = (ImageView)view.findViewById(R.id.list_image);
                TextView historytext = (TextView)view.findViewById(R.id.list_title);
                //通过游标得到指定的项的值
                int columnIndex = cursor.getColumnIndex("NewsImgUrl");
                String  URL = cursor.getString(columnIndex);
                int columnIndex2 = cursor.getColumnIndex("NewsTitle");
                String  Title = cursor.getString(columnIndex2);
                //设置显示
                Glide.with(MainActivity.this).load(URL).into(historyimg);
                historytext.setText(Title);
            }
        };
        listView.setAdapter(historyAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //游标指向的数据
                int NewsId = Integer.parseInt(cursor.getString(cursor.getColumnIndex("NewsId")));
                getNewsDetails(NewsId);
                addHistory(position);
            }
        });
    }

    //主页面fab功能，选择日期
    private void MainFab() {
        //获取FloatingActionButton实例，选择新闻日期
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.ic_date_range_white_24dp);
        //获取日历实例
        calendar = Calendar.getInstance();
        final DateFormatter dateFormatter = new DateFormatter();//日期格式化工具
        //FloatingActionButton点击监听
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int month, int day) {
                                if ( Integer.parseInt(dateFormatter.ZhihuDailyDateFormat(year,month,day))< 20130520){
                                    Toast.makeText(MainActivity.this,"知乎日报还没出生~",Toast.LENGTH_SHORT).show();
                                }else{
                                    String url = ZHIHU_HISTORY + dateFormatter.ZhihuDailyDateFormat(year,month,day);
                                    getNewsData(url);
                                    zhadapter = new ZHihuNewsAdapter(MainActivity.this);
                                    listView.setAdapter(zhadapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            getNewsDetails(nesData.get(position).id);
                                            addHistory(position);
                                        }
                                    });}
                            }
                        }, calendar.get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));
                DatePicker picker = dialog.getDatePicker();
                Date date = new Date();//当前时间
                long maxtime = date.getTime();
                picker.setMaxDate(maxtime);
                dialog.show();

            }
        });
    }

    //历史页面fab功能，清除历史记录
    private void HistoryFab() {
        FloatingActionButton historyFab = (FloatingActionButton) findViewById(R.id.fab);
        historyFab.setVisibility(View.VISIBLE);
        historyFab.setImageResource(R.drawable.ic_clear_all_white_24dp);
        historyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = helper.getReadableDatabase();
                //delete方法返回影响的行数
                int line = db.delete("NewsHistory",null,null);
                if(line>0){
                }
                db.close();
                displayAllHistory();
                CheckNoHistory();
            }
        });
    }

    //收藏页面饭功能，隐藏
    private void FavoriteFab() {
        FloatingActionButton FavoriteFab = (FloatingActionButton) findViewById(R.id.fab);
        FavoriteFab.setVisibility(View.GONE);
    }

    private void CheckNoHistory(){

        if(fetchPlacesCount()==0){
            noRecord.setVisibility(View.VISIBLE);
        }else {
            noRecord.setVisibility(View.GONE);
        }
    }

    //获取history数据库行数
    private long fetchPlacesCount(){
        String sql = "SELECT COUNT(*) FROM " + "NewsHistory";
        SQLiteStatement statement = helper.getReadableDatabase().compileStatement(sql);
        long count = statement.simpleQueryForLong();
        return count;
    }

    //添加历史记录
    private void addHistory(int position) {
        SQLiteDatabase db = helper.getReadableDatabase();//打开本地数据库连接
        db.execSQL("delete from NewsHistory where NewsId=?", new Object[]{nesData.get(position).id+""});//删除相同id的数据
        ContentValues values = new ContentValues();
        values.put("NewsId",nesData.get(position).id+"");
        values.put("NewsImgUrl",nesData.get(position).images);
        values.put("NewsTitle",nesData.get(position).title);
        db.insert("NewsHistory", null, values);//插入新数据
        db.close();
    }

    //加载关注的新闻数据
    private void displayAllFocus() {
        listView= (ListView) findViewById(R.id.list_view);
        cursor = helper.getReadableDatabase().query("NewsFocus",new String[]{"_id","NewsFocusId","NewsFocusImageUrl","NewsFocusTitle"}
                ,null,null,null,null,"NewsFocusId,NewsFocusImageUrl,NewsFocusTitle DESC");
        CursorAdapter focusAdapter = new CursorAdapter(this,cursor) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                @SuppressLint("InflateParams") View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.news_item,null);
                return view;
            }
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ImageView historyimg = (ImageView)view.findViewById(R.id.list_image);
                TextView historytext = (TextView)view.findViewById(R.id.list_title);
                //通过游标得到指定的项的值
                int columnIndex = cursor.getColumnIndex("NewsFocusImageUrl");
                String  URL = cursor.getString(columnIndex);
                int columnIndex2 = cursor.getColumnIndex("NewsFocusTitle");
                String  Title = cursor.getString(columnIndex2);
                //设置显示
                Glide.with(MainActivity.this).load(URL).into(historyimg);
                historytext.setText(Title);
            }
        };
        listView.setAdapter(focusAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //游标指向的数据
                int NewsId = Integer.parseInt(cursor.getString(cursor.getColumnIndex("NewsFocusId")));
                getNewsDetails(NewsId);
            }
        });
    }

    //查看是否存在关注新闻
    private void CheckNoFocusNews() {

        if(fetchPlacesCount1()==0){
            noRecord.setVisibility(View.VISIBLE);
        }else {
            noRecord.setVisibility(View.GONE);
        }
    }

    //获取收藏数据库行数
    private long fetchPlacesCount1(){
        String sql = "SELECT COUNT(*) FROM " + "NewsFocus";
        SQLiteStatement statement = helper.getReadableDatabase().compileStatement(sql);
        long count = statement.simpleQueryForLong();
        return count;
    }
}
