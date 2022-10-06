package com.nmd.utility.view;

import static androidx.core.view.ViewCompat.animate;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

public abstract class ViewClickAnimation implements View.OnClickListener {

    private long mLastClickedTime;

    public abstract void onClickAnimationListener(View view);

    @Override
    public void onClick(View v) {
        long currentTime = SystemClock.elapsedRealtime();
        long diff = currentTime - mLastClickedTime;
        mLastClickedTime = currentTime;
        if (diff > 500) {
            startScaleViewAnimation(v, () -> onClickAnimationListener(v));
        } else {
            Log.e("SafeClicked", "Reject multi click on a same view in a short time");
        }
    }

    public interface ViewAnimationListener {
        void onComplete();
    }

    void startScaleViewAnimation(View view, ViewAnimationListener callback) {
        animate(view).scaleX((float) 0.9).scaleY((float) 0.9).alpha(0.5f).setDuration(150);
        new Handler().postDelayed(() -> animate(view).scaleX(1).scaleY(1).alpha(1).setDuration(150), 150);
        new Handler().postDelayed(() -> {
            if (callback != null) callback.onComplete();
        }, (long) 150 * 2);
    }
}
