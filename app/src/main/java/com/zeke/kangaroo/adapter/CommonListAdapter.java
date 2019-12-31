package com.zeke.kangaroo.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by KingZ.
 * Data: 2016 2016/8/18
 * Discription: 通用 List 适配器
 */
public abstract class CommonListAdapter<T> extends BaseAdapter {
    protected List<T> mData;

    public CommonListAdapter(List<T> datas) {
        this.mData = datas;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        if (position < 0 || position >= mData.size()) {
            return null;
        }
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    public CommonListAdapter<T> addItem(T obj, int index) {
        this.mData.add(index, obj);
        return this;
    }

    public CommonListAdapter<T> addAll(List<T> list) {
        if (list != null) {
            this.mData.addAll(list);
        } else {
            removeAll();
        }
        notifyDataSetChanged();
        return this;
    }

    public CommonListAdapter<T> addAll(T... list) {
        if (list != null && list.length > 0) {
            this.mData.addAll(Arrays.asList(list));
        }
        notifyDataSetChanged();
        return this;
    }

    public T remove(int index) {
        if (index < 0 || index >= mData.size()) {
            return null;
        }
        return this.mData.remove(index);
    }

    public void removeAll() {
        this.mData.clear();
    }

}
