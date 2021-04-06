package com.nmd.utility.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nmd.utility.DebugLog;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;

public abstract class ApiCallback<T> implements Callback<T> {
    abstract public void onResponse(Call<T> call, boolean isSuccess, int statusCode, T response, @Nullable String requestText, String requestUrl, ErrorR error, boolean isCancelled);

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull retrofit2.Response<T> responseTmp) {
        log(call, responseTmp.code());
        onResponse(call, responseTmp.isSuccessful(), responseTmp.code(), responseTmp.body(), bodyToString(call.request().body()), call.request().url().toString(), new ErrorR(responseTmp.errorBody(), responseTmp.code(), null), call.isCanceled());
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        log(call, -1);

        onResponse(call, false, -1, null, bodyToString(call.request().body()), call.request().url().toString(), new ErrorR("", -1, t), call.isCanceled());
    }

    private void log(Call<T> call, int statusCode) {
        DebugLog.logn(call.request().method() + " : "+call.request().url().toString() + " [" + statusCode + "]");
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
