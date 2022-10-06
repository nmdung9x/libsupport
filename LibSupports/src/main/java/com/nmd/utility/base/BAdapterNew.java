package com.nmd.utility.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.nmd.utility.viewbinder.ViewBindingCreator;

import java.util.ArrayList;


public abstract class BAdapterNew<V extends ViewBinding, T> extends RecyclerView.Adapter<BAdapterNew.MyViewHolder> {
    Context c;
    ArrayList<T> l;

    public BAdapterNew(Context context, ArrayList<T> list) {
        c = context;
        l = list;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        V binding;
        public MyViewHolder(V binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    @Override
    public int getItemCount() {
        return l.size();
    }

    @NonNull
    @Override
    public BAdapterNew.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new MyViewHolder(ViewBindingCreator.create(getClass(), LayoutInflater.from(c), viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull BAdapterNew.MyViewHolder mViewHolder, final int position) {
        initViewItem(c, (V) mViewHolder.binding, l.get(position), position);
    }

    public abstract void initViewItem(Context c, V v, T t, int position);
}
