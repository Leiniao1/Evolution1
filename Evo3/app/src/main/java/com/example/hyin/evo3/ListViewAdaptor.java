package com.example.hyin.evo3;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by hyin on 8/1/2015.
 */

public class ListViewAdaptor extends ArrayAdapter<Row> {

    Context context;
    int layoutResourceId;
    Row data[] = null;

    public ListViewAdaptor(Context context, int layoutResourceId, Row[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RowHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RowHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtDescription = (TextView)row.findViewById(R.id.txtDescription);

            row.setTag(holder);
        }
        else
        {
            holder = (RowHolder)row.getTag();
        }

        Row aRow = data[position];
        holder.txtTitle.setText(aRow.title);
        holder.imgIcon.setImageResource(aRow.icon);
        holder.txtDescription.setText(aRow.description);

        return row;
    }


    static class RowHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtDescription;
    }
}
