package com.zeke.kangaroo.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author：KingZ
 * date：2019/10/10
 * description：通用 RecyclerView 的适配器
 */
public abstract class CommonRecyclerAdapter<T> extends
        RecyclerView.Adapter<CommonRecyclerAdapter.ViewHolder> {
    private List<T> mData = new ArrayList<>();

    public CommonRecyclerAdapter(List<T> list) {
        if(list == null){
            throw new IllegalArgumentException("Adapter Data must not be null");
        }
        mData = list;
    }

    public CommonRecyclerAdapter(T... datas) {
        if(datas == null){
            throw new IllegalArgumentException("Adapter Data must not be null");
        }
        addAll(datas);
    }

    public int getCount() {
        return mData.size();
    }

    public List<T> getAll(){
        return mData;
    }

    public T getItem(int position) {
        if (position < 0 || position >= mData.size()) {
            return null;
        }
        return mData.get(position);
    }

    /**
     * 根据viewType返回需要填充的布局id
     * @param type 实现类又来区分Layout类型的参数
     * @return Type Code
     */
    protected abstract int getItemLayout(int type);

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(parent.getContext()).inflate(this.getItemLayout(viewType),
                null, false);
        return new CommonRecyclerAdapter.ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {}

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }


    public CommonRecyclerAdapter<T> addItem(T obj){
        this.mData.add(obj);
        return this;
    }

    public CommonRecyclerAdapter<T> addItem(T obj, int index){
        this.mData.add(index,obj);
        return this;
    }

    public CommonRecyclerAdapter<T> addAll(List<T> list){
        if(list != null){
            this.mData.addAll(list);
        }else{
            removeAll();
        }
        return this;
    }

    public CommonRecyclerAdapter<T> addAll(T... list){
        if(list != null && list.length >= 0) {
            mData.addAll(Arrays.asList(list));
        }
        return this;
    }

    public T remove(int index) {
        if (index < 0 || index >= mData.size()) {
            return null;
        }
        return mData.remove(index);
    }

    public void removeAll() {
        mData.clear();
    }

    public CommonRecyclerAdapter<T> removeAll(List<T> list) {
        if (list != null) {
            mData.removeAll(list);
        }
        return this;
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> holder = new SparseArray<>();

        public ViewHolder(View itemView) {
            super(itemView);
            new SparseArray<>();
        }

        public <V extends View> V getView(int id) {
            View view = holder.get(id);
            if(view == null) {
                view = itemView.findViewById(id);
                holder.put(id, view);
            }
            return (V) view;
        }

        //FIXME 优化onItemClick和onLongClick回调  这种为每个item添加点击监听的解决方案是浪费性能的
        public void setOnLongClickListener(View.OnLongClickListener listener) {
            itemView.setOnLongClickListener(listener);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
        }
    }
}
