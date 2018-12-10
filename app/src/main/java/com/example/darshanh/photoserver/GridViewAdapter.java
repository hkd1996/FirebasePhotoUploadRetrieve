package com.example.darshanh.photoserver;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private List<Upload> uploads;

    public GridViewAdapter(Context context, List<Upload> uploads) {
        this.context = context;
        this.uploads = uploads;
    }

    @Override
    public int getCount() {
        return uploads.size();
    }

    @Override
    public Object getItem(int i) {
        return uploads.get(i);

    }



    class ViewHolder{
        ImageView imageHolder;

        public ViewHolder(View v) {
            imageHolder=v.findViewById(R.id.imageView);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row=view;
        ViewHolder holder=null;
        if(row==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=inflater.inflate(R.layout.image_display,viewGroup,false);
            holder=new ViewHolder(row);
            row.setTag(holder);

        }
        else
        {
            holder=(ViewHolder)row.getTag();
        }
        Upload upload = uploads.get(i);
        Glide.with(context).load(upload.getUrl()).into(holder.imageHolder);

        return row;
    }
}
