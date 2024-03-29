package com.nmd.utility.common;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.nmd.utility.DebugLog;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;

public abstract class JsonCallback<T> implements Callback<T> {
    abstract public void onSuccess(int statusCode, JSONObject jsonObject);

    abstract public void onFailed(@NonNull Call<T> call, ErrorR error);

    @SuppressWarnings("WeakerAccess")
    @CallSuper
    public void onCancelled(Call<T> call) {
        logCancelledCall(call);
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull retrofit2.Response<T> response) {
        if (call.isCanceled()) {
            onCancelled(call);
            return;
        }
        log(call, response.code());

        if (response.isSuccessful()) {
            if (response.body() != null) {
                try {
                    String gson;
                    if (response.body() instanceof JsonElement) {
                        gson = new Gson().toJson(response.body());
                    } else if (response.body() instanceof ResponseBody) {
                        gson = ((ResponseBody) response.body()).string();
                    } else {
                        gson = response.body().toString();
                    }
                    DebugLog.logi(gson);
                    if (gson.length() > 1) {
                        if (gson.substring(0, 1).equals("{")) {
                            onSuccess(response.code(), new JSONObject(gson));
                        } else if (gson.substring(0, 1).equals("[")) {
                            JSONObject results = new JSONObject();
                            results.put("content", new JSONArray(gson));
                            onSuccess(response.code(), results);
                        } else {
                            onFailed(call, new ErrorR("", response.code(), null));
                        }
                    } else {
                        onFailed(call, new ErrorR("", response.code(), null));
                    }
                } catch (Exception e) {
                    DebugLog.loge(e);
                    onFailed(call, new ErrorR("", response.code(), e));
                }
            } else {
                onSuccess(response.code(), null);
            }
            return;
        }

        onFailed(call, new ErrorR(response.errorBody(), response.code(), null));
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        if (call.isCanceled()) {
            onCancelled(call);
            return;
        }
        log(call, -1);
        onFailed(call, new ErrorR("", -1, t));
    }

    private void logCancelledCall(Call<T> call) {
        DebugLog.loge(call.request().method() + " " + call.request().url() + " cancelled");
    }

    private void log(Call<T> call, int statusCode) {
        DebugLog.logi(call.request().method() + " : "+call.request().url().toString() + " [" + statusCode + "]");
        if (call.request().body() != null) {
            DebugLog.logi("[Request Body] "+bodyToString(call.request().body()));
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
