package com.nmd.utility.common;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.nmd.utility.DebugLog;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;

public abstract class JsonCallback<T> implements Callback<T> {
    abstract public void onSuccess(Call<T> call, JSONObject jsonObject);

    abstract public void onFailed(Call<T> call, ErrorR error);

    @SuppressWarnings("WeakerAccess")
    @CallSuper
    public void onCancelled(Call<T> call) {
        logCancelledCall(call);
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull retrofit2.Response<T> response) {
        log(call);
        if (call.isCanceled()) {
            onCancelled(call);
            return;
        }

        DebugLog.logn("status code: "+response.code());
        if (response.isSuccessful()) {
            onSuccess(call, getBody(response));
        } else {
            onFailed(call, new ErrorR(response.errorBody(), response.code(), null));
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        log(call);
        if (call.isCanceled()) {
            onCancelled(call);
            return;
        };

        onFailed(call, new ErrorR(null, 0, t));
    }

    private void logCancelledCall(Call<T> call) {
        DebugLog.loge(call.request().method() + " " + call.request().url() + " cancelled");
    }

    private JSONObject getBody(@NonNull retrofit2.Response<T> response) {
        if (response.body() != null) {
            try {
                String gson = new Gson().toJson(response.body());
                if (!gson.trim().isEmpty()) {
                    if (gson.substring(0, 1).equals("{")) {
                        DebugLog.logn(gson);
                        return new JSONObject(gson);
                    } else if (gson.substring(0, 1).equals("[")) {
                        JSONObject results = new JSONObject();
                        results.put("content", new JSONArray(gson));
                        DebugLog.logn(results);
                        return results;
                    } else {
                        JSONObject content = new JSONObject();
                        content.put("content", gson);
                        DebugLog.logn(content);
                        return content;
                    }
                } else {
                    DebugLog.loge(response.body());
                    return null;
                }
            } catch (Exception e) {
                DebugLog.loge(e);
            }
        }
        return null;
    }

    private void log(Call<T> call) {
        DebugLog.logn(call.request().method() + " : "+call.request().url().toString());
        if (call.request().method().toLowerCase().equals("post")) {
            DebugLog.logn("[Request Body] "+bodyToString(call.request().body()));
        }
    }

    String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final Exception e) {
            return "did not work";
        }
    }
}
