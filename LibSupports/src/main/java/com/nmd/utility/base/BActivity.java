package com.nmd.utility.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;

import com.nmd.utility.DebugLog;
import com.nmd.utility.viewbinder.LayoutBinder;
import com.nmd.utility.viewbinder.ViewBinder;
import com.nmd.utility.viewbinder.ViewBindingCreator;

public abstract class BActivity<T extends ViewBinding> extends AppCompatActivity {
    public Context context;
    public Toolbar toolbar;
    public ActionBar actionbar;
    public T v;

    public abstract void onCreated();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        v = ViewBindingCreator.create(getClass(), getLayoutInflater(), null);
        if (v == null) {
            Integer layout = LayoutBinder.getViewLayout(this);
            if (layout != null) {
                try {
                    setContentView(layout);
                } catch (Exception e) {
                    DebugLog.loge(e);
                    return;
                }
                created();
            }
        } else {
            setContentView(v.getRoot());
            created();
        }

    }

    void created() {
        context = BActivity.this;
        ViewBinder.bind(this);
        onCreated();
    }

    public String getStringFromBundle(String key) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String tmp = bundle.getString(key);
            if (tmp == null) tmp = "";
            return tmp;
        }
        return "";
    }

    public void startActivity(Class<?> cls, boolean finish) {
        startActivity(new Intent(context, cls));
        if (finish) finish();
    }

    public void setupNavigationView() {
//        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setTitle("");
//            actionbar.setDisplayHomeAsUpEnabled(true);
//            actionbar.setHomeAsUpIndicator(R.drawable.icon_menu);
        }
    }

    public boolean isLandscape() {
        int orientation = getResources().getConfiguration().orientation;
        DebugLog.loge(orientation == Configuration.ORIENTATION_LANDSCAPE ? "YES" : "NO");
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

}
