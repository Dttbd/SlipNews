package com.example.dttbd.slipnews;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

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
                        Toast.makeText(MainActivity.this, "home", Toast.LENGTH_SHORT).show();
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

}
