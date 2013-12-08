package com.jess.ticket.ui.adapter;

import com.jess.ticket.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {

    private Context mContext;

    private int[] mMenuRes;

    public MenuAdapter(Context context, int[] menuRes) {
        mContext = context;
        mMenuRes = menuRes;
    }

    @Override
    public int getCount() {
        return mMenuRes == null ? 0 : mMenuRes.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.menuTv.setText(mMenuRes[position]);

        return convertView;
    }

    static final class ViewHolder {
        TextView menuTv;

        public ViewHolder(View root) {
            menuTv = (TextView) root.findViewById(R.id.menu_tv);
        }
    }

}
