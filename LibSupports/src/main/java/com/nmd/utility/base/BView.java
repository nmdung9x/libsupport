package com.nmd.utility.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.viewbinding.ViewBinding;

import com.nmd.utility.DebugLog;
import com.nmd.utility.viewbinder.LayoutBinder;
import com.nmd.utility.viewbinder.ViewBinder;
import com.nmd.utility.viewbinder.ViewBindingCreator;

public abstract class BView<T extends ViewBinding> extends FrameLayout {
    public Context context;
    public View viewRoot;
    public T v;

    public BView(Context context) {
        super(context);
        this.context = context;
        initRView();
    }

    public BView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initRView();
    }

    public BView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initRView();
    }

    private void initRView() {
        v = ViewBindingCreator.create(getClass(), LayoutInflater.from(context), null);
        if (v == null) {
            try {
                Integer layout = LayoutBinder.getViewLayout(this);
                if (layout != null) viewRoot = LayoutInflater.from(context).inflate(layout, null);
                else DebugLog.loge("layout = null");
            } catch (Exception e) {
                DebugLog.loge(e);
            }
        } else {
            viewRoot = v.getRoot();
        }

        if (viewRoot != null) {
            addView(viewRoot);
            ViewBinder.bind(this, viewRoot);
        } else return;

        setOnClickListener(v -> DebugLog.loge("_"));
        initView();
    }

    public abstract void initView();
}
