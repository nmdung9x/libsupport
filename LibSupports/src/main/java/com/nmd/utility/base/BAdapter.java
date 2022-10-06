package com.nmd.utility.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nmd.utility.viewbinder.LayoutBinder;

import java.util.ArrayList;


public abstract class BAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context c;
    ArrayList<T> l;

    public BAdapter(Context context, ArrayList<T> list) {
        c = context;
        l = list;
    }

    @Override
    public int getItemCount() {
        return l.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Integer layout = LayoutBinder.getViewLayout(this);
        if (layout == null) return getViewHolder(null);
        return getViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(LayoutBinder.getViewLayout(this), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mViewHolder, final int position) {
        initViewItem(c, mViewHolder, l.get(position), position);
    }

    public abstract RecyclerView.ViewHolder getViewHolder(View viewInflater);
    public abstract void initViewItem(Context c, RecyclerView.ViewHolder v, T t, int position);
}
