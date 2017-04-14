package com.example.dttbd.slipnews.util;

/**
 * Created by Dttbd
 */

public class Api {

    //知乎日报API
    public static final String ZHIHU_NEWS = "http://news-at.zhihu.com/api/4/news/";

    //知乎日报历史文章API
    // 知乎日报始于2013 年 5 月 19 日，所以如果数字小于20130520，就获取不到记录
    public static final String ZHIHU_HISTORY = "http://news.at.zhihu.com/api/4/news/before/";

    //知乎首页壁纸json API
    public static final String ZHIHU_IMAGE = "http://news-at.zhihu.com/api/7/prefetch-launch-images/1080*1920";
}
