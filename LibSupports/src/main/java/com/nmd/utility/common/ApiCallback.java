package com.nmd.utility.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nmd.utility.DebugLog;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;

public abstract class ApiCallback<T> implements Callback<T> {
    abstract public void onResponse(Call<T> call, boolean isSuccess, int statusCode, String responseText, @Nullable String requestText, String requestUrl, Throwable t, boolean isCancelled);

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull retrofit2.Response<T> response) {
        log(call);
        String responseText = "";
        if (response.body() != null) {
            responseText = response.body().toString();
        } else {
            if (response.errorBody() != null) {
                try {
                    responseText = response.errorBody().string();
                } catch (Exception e) {
                    DebugLog.loge(e);
                    responseText = "";
                }
            }
        }
        onResponse(call, response.isSuccessful(), response.code(), responseText, bodyToString(call.request().body()), call.request().url().toString(), null, call.isCanceled());
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        log(call);

        onResponse(call, false, -1, "", bodyToString(call.request().body()), call.request().url().toString(), t, call.isCanceled());
    }

    private void log(Call<T> call) {
        DebugLog.logn(call.request().method() + " : "+call.request().url().toString());
        if (call.request().body() != null) {
            DebugLog.logn("[Request Body] "+bodyToString(call.request().body()));
        }
        if (call.isCanceled()) {
            DebugLog.logn("[!!! CANCELLED !!!]");
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
            return "";
        }
    }
}
