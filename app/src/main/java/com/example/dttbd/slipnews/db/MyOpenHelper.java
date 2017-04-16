package com.example.dttbd.slipnews.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created Dttbd
 * 建立历史数据库的一个类
 */
public class MyOpenHelper extends SQLiteOpenHelper{

    private static final int VERSION = 1;

    public MyOpenHelper(Context context) {
        super(context,"users.db",null, VERSION);
    }

//    ram：name 数据库文件名
//    factory：如果为空使用默认方式
//    version：版本号（必须上升）

    public MyOpenHelper(Context context, String name, int version) {
        super(context, name,null, version);
    }

//     当数据库文件不存在，创建数据库文件，并且第一次使用时
//     只调用一次

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("TEST","onCreate");
        String sql = "create table NewsHistory(_id integer primary key autoincrement,"+"NewsId,"+"NewsImgUrl,"+"NewsTitle)";
        String sql2 = "create table NewsFocus(_id integer primary key autoincrement,"+"NewsFocusId,"+"NewsFocusImageUrl,"+"NewsFocusTitle)";
        //创建表只需一次
        db.execSQL(sql);
        db.execSQL(sql2);
    }

    //版本更新时

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("TEST","onUpgrade:oldVersion==="+oldVersion+","+"newVersion"+newVersion);
    }
}
