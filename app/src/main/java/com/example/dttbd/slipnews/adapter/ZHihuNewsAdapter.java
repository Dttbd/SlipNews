package com.example.dttbd.slipnews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dttbd.slipnews.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dttbd
 */

public class ZHihuNewsAdapter extends BaseAdapter {
    private Context mContext;
    public ZHihuNewsAdapter(Context context) {
        mContext = context;
    }
    private List<ZhihunewsDataBean> mList;
    public void setData(List<ZhihunewsDataBean> list){
        if (mList==null){
            mList = new ArrayList<ZhihunewsDataBean>();
        }else {
            mList.clear();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.news_item, null);
            holder.newsPic = (ImageView)convertView.findViewById(R.id.list_image);
            holder.newsHead = (TextView) convertView.findViewById(R.id.list_title);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        Glide.with(mContext).load(mList.get(position).images).into(holder.newsPic);
        holder.newsHead.setText(mList.get(position).title);
        return convertView;
    }
    private class ViewHolder {
        ImageView newsPic;     //新闻图片
        TextView newsHead;    //新闻头条
    }
}
