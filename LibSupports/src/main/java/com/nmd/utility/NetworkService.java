package com.nmd.utility;

import android.content.Context;
import android.content.ContextWrapper;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.nmd.utility.common.ErrorR;
import com.nmd.utility.common.JsonCallback;
import com.nmd.utility.other.MultipartRequest;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by nmd9x on 10/12/17.
 */

public class NetworkService extends ContextWrapper {
    Context context;
    private RequestQueue mRequestQueue;
    private static final int TIME_OUT = 30000;

    public static Retrofit retrofit(String baseUrl) {
        return retrofit(baseUrl, new JSONObject());
    }

    public static Retrofit retrofit(String baseUrl, @NonNull final JSONObject header) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//                .registerTypeAdapter(Date.class, new GsonUtcDateAdapter())
                .create();
        return retrofit(baseUrl, header, gson, TIME_OUT);
    }

    public static Retrofit retrofit(String baseUrl, @NonNull final JSONObject header, Gson gson, int timeOut) {
        if (baseUrl.isEmpty()) {
            DebugLog.loge("baseUrl empty");
            return null;
        }
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
                .readTimeout(timeOut, TimeUnit.MILLISECONDS)
                .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Request.Builder builder = chain.request().newBuilder();

                        String userAgent = System.getProperty("http.agent");
                        if (userAgent != null) {
                            builder.addHeader("User-Agent", userAgent);
                        }

                        Iterator<String> keys = header.keys();
                        while(keys.hasNext()) {
                            String key = keys.next();
                            builder.addHeader(key, UtilLibs.getStringInJsonObj(header, key));
                        }
                        return chain.proceed(builder.build());
                    }
                });

        return new Retrofit.Builder()
                .baseUrl(baseUrl.concat("/"))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClientBuilder.build())
                .build();
    }

    public interface RetrofitCall {
        @GET
        Call<JsonElement> get(@Url String url);

        @POST
        Call<JsonElement> post(@Url String url, @Body RequestBody body);
    }

    public interface OnResponse {
        void result(int statusCode, JSONObject responseJson, String errorString);
    }

    public static void get(String url, final OnResponse callback) {
        get(url, new JSONObject(), callback);
    }

    public static void get(String url, JSONObject header, final OnResponse callback) {
        String base_url = UtilLibs.getDomainName(url);
        if (base_url.isEmpty()) return;
        RetrofitCall call = retrofit(base_url, header).create(RetrofitCall.class);
        get(call, url.substring(base_url.length()), callback);
    }

    public static void get(RetrofitCall call, String url, final OnResponse callback) {
        call.get(url).enqueue(new JsonCallback<JsonElement>() {
            @Override
            public void onSuccess(int statusCode, JSONObject jsonObject) {
                if (callback != null) {
                    callback.result(statusCode, jsonObject, "");
                }
            }

            @Override
            public void onFailed(@NonNull Call<JsonElement> call, ErrorR error) {
                if (callback != null) {
                    JSONObject jsonObject = null;
                    if (!error.getContent().isEmpty()) {
                        try {
                            jsonObject = new JSONObject(error.getContent());
                        } catch (Exception e) {
                            DebugLog.logv(e);
                            jsonObject = null;
                        }
                    }
                    callback.result(error.getCode(), jsonObject, error.getContent());
                }
            }
        });
    }

    public static void post(String url, JSONObject params, final OnResponse callback) {
        post(url, new JSONObject(), params, callback);
    }

    public static void post(String url, JSONObject header, JSONObject params, final OnResponse callback) {
        String base_url = UtilLibs.getDomainName(url);
        if (base_url.isEmpty()) return;
        RetrofitCall call = retrofit(base_url, header).create(RetrofitCall.class);
        post(call, url.substring(base_url.length()), params, callback);
    }

    public static void post(RetrofitCall call, String url, JSONObject params, final OnResponse callback) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), params.toString());
        call.post(url, body).enqueue(new JsonCallback<JsonElement>() {
            @Override
            public void onSuccess(int statusCode, JSONObject jsonObject) {
                if (callback != null) {
                    callback.result(statusCode, jsonObject, "");
                }
            }

            @Override
            public void onFailed(@NonNull Call<JsonElement> call, ErrorR error) {
                if (callback != null) {
                    JSONObject jsonObject = null;
                    if (!error.getContent().isEmpty()) {
                        try {
                            jsonObject = new JSONObject(error.getContent());
                        } catch (Exception e) {
                            DebugLog.logv(e);
                            jsonObject = null;
                        }
                    }
                    callback.result(error.getCode(), jsonObject, error.getContent());
                }
            }
        });
    }

    public NetworkService(Context ctx) {
        super(ctx);
        context = ctx;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public interface OnGetResult {
        void result(String response);
        void statusCode(int code);
        void error(Exception exception);
    }

    public void cancelRequest(String tagRequest) {
        mRequestQueue.cancelAll(tagRequest);
    }

    public void get(final String url, final JSONObject headers, final OnGetResult callback) {
        get(url, parseToHashMap(headers), "", false, callback);
    }

    public void get(final String url, final HashMap<String, String> headers, final OnGetResult callback) {
        get(url, headers, "", false, callback);
    }

    public void get(final String url, final HashMap<String, String> headers, String tagRequest, boolean singleRequest, final OnGetResult callback) {
        DebugLog.logn("url:\n" + url);
        if (url.isEmpty()) {
            DebugLog.loge("url empty");
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DebugLog.logn(response);
                callback.result(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.loge(error);
                callback.error(error);
                int statusCode = -1;
                try {
                    statusCode = error.networkResponse.statusCode;
                } catch (Exception e) {
                    DebugLog.loge(e);
                }
                if (statusCode > 0) callback.statusCode(statusCode);
                try {
                    NetworkResponse response = error.networkResponse;
                    if (response != null) {
                        String strResponse = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        DebugLog.loge(strResponse);
                        callback.result(strResponse);
                    } else {
                        DebugLog.loge("response == null");
                    }
                } catch (Exception e) {
                    DebugLog.loge(e);
                }
            }
        }) {

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int statusCode = -1;
                try {
                    statusCode = response.statusCode;
                } catch (Exception e) {
                    DebugLog.loge(e);
                }
                if (statusCode > 0) callback.statusCode(statusCode);
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers == null) {
                    return new HashMap<>();
                }
                return headers;
            }
        };

        stringRequest.setShouldCache(false);
        if (singleRequest) {
            if (!tagRequest.trim().isEmpty()) {
                mRequestQueue.cancelAll(tagRequest);
            }
        }
        if (!tagRequest.trim().isEmpty()) stringRequest.setTag(tagRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }

    public void post(String url, final String body, final JSONObject headers, final OnGetResult callback) {
        post(url, body, parseToHashMap(headers), "", false, callback);
    }

    public void post(String url, final String body, final HashMap<String, String> headers, final OnGetResult callback) {
        post(url, body, headers, "", false, callback);
    }

    public void post(String url, final String body, final HashMap<String, String> headers, String tagRequest, boolean singleRequest, final OnGetResult callback) {
        DebugLog.logn("url:\n" + url);
        DebugLog.logn("body:\n" + body);
        if (url.isEmpty()) {
            DebugLog.loge("url empty");
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DebugLog.logn(response);
                callback.result(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.loge(error);
                callback.error(error);
                try {
                    NetworkResponse response = error.networkResponse;
                    if (response != null) {
                        String strResponse = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        DebugLog.loge(strResponse);
                        callback.result(strResponse);
                    } else {
                        DebugLog.loge("response == null");
                    }
                } catch (Exception e) {
                    DebugLog.loge(e);
                }
            }
        }) {

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int statusCode = -1;
                try {
                    statusCode = response.statusCode;
                } catch (Exception e) {
                    DebugLog.loge(e);
                }
                if (statusCode > 0) callback.statusCode(statusCode);
                return super.parseNetworkResponse(response);
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    if (body.isEmpty()) return null;
                    return body.getBytes("utf-8");
                } catch (Exception e) {
                    DebugLog.loge(e);
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers == null) {
                    return new HashMap<>();
                }
                return headers;
            }
        };

        stringRequest.setShouldCache(false);
        if (singleRequest) {
            if (!tagRequest.trim().isEmpty()) mRequestQueue.cancelAll(tagRequest);
        }
        if (!tagRequest.trim().isEmpty()) stringRequest.setTag(tagRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }

    public void post(String url, final JSONObject params, final JSONObject headers, final OnGetResult callback) {
        post(url, parseToHashMap(params), parseToHashMap(headers), callback);
    }

    public void post(String url, final HashMap<String, String> params, final HashMap<String, String> headers, final OnGetResult callback) {
        post(url, params, headers, "", false, callback);
    }

    public void post(String url, final HashMap<String, String> params, final HashMap<String, String> headers, String tagRequest, boolean singleRequest, final OnGetResult callback) {
        if (url.isEmpty()) {
            DebugLog.loge("url empty");
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DebugLog.logn(response);
                callback.result(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.loge(error);
                callback.error(error);
                try {
                    NetworkResponse response = error.networkResponse;
                    if (response != null) {
                        String strResponse = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        DebugLog.loge(strResponse);
                        callback.result(strResponse);
                    } else {
                        DebugLog.loge("response == null");
                    }
                } catch (Exception e) {
                    DebugLog.loge(e);
                }
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int statusCode = -1;
                try {
                    statusCode = response.statusCode;
                } catch (Exception e) {
                    DebugLog.loge(e);
                }
                if (statusCode > 0) callback.statusCode(statusCode);
                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String, String> getParams() {
                if (params == null) {
                    return new HashMap<>();
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers == null) {
                    return new HashMap<>();
                }
                return headers;
            }
        };

        stringRequest.setShouldCache(false);
        if (singleRequest) {
            if (!tagRequest.trim().isEmpty()) mRequestQueue.cancelAll(tagRequest);
        }
        if (!tagRequest.trim().isEmpty()) stringRequest.setTag(tagRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }

    public void post(String url, final HashMap<String, String> params, final HashMap<String, File> files, final HashMap<String, String> headers, final OnGetResult callback) {
        post(url, params, files, headers, "", false, callback);
    }

    public void post(String url, final HashMap<String, String> params, final HashMap<String, File> files, final HashMap<String, String> headers, String tagRequest, boolean singleRequest, final OnGetResult callback) {
        if (url.isEmpty()) {
            DebugLog.loge("url empty");
            return;
        }
        byte[] multipartBody = multipartBody(params, files);

        if (multipartBody == null) {
            DebugLog.loge("multipartBody == null");
            callback.result("multipartBody == null");
            return;
        }

        MultipartRequest multipartRequest = new MultipartRequest(url, null, mimeType, multipartBody, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                int statusCode = -1;
                try {
                    statusCode = response.statusCode;
                } catch (Exception e) {
                    DebugLog.loge(e);
                }
                if (statusCode > 0) callback.statusCode(statusCode);
                try {
                    String result = new String(response.data, "UTF-8");
                    callback.result(result);
                } catch (Exception e) {
                    DebugLog.loge(e);
                    callback.error(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.loge(error);
                callback.error(error);
                try {
                    NetworkResponse response = error.networkResponse;
                    if (response != null) {
                        String strResponse = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        DebugLog.loge(strResponse);
                        callback.result(strResponse);
                    } else {
                        DebugLog.loge("response == null");
                    }
                } catch (Exception e) {
                    DebugLog.loge(e);
                }
            }
        });

        multipartRequest.setShouldCache(false);
        if (singleRequest) {
            if (!tagRequest.trim().isEmpty()) mRequestQueue.cancelAll(tagRequest);
        }
        if (!tagRequest.trim().isEmpty()) multipartRequest.setTag(tagRequest);
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(multipartRequest);
    }

    public byte[] multipartBody(HashMap<String, String> params, HashMap<String, File> files) {
        byte[] multipartBody = null;

        boundary = "apiclient-" + System.currentTimeMillis();
        mimeType = "multipart/form-data;boundary=" + boundary;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            if (params.size() > 0) {
                for (HashMap.Entry<String, String> entry : params.entrySet()) {
                    buildTextPart(dos, entry.getKey(), entry.getValue());
                }
            }
            if (files.size() > 0) {
                for (HashMap.Entry<String, File> entry : files.entrySet()) {
                    String path = entry.getValue().getPath();
                    DebugLog.loge("path:\n" + path);
                    buildPart(dos, entry.getKey(), UtilLibs.getFileNameAndExtension(path), readFile(entry.getValue()));
                }
            }
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            multipartBody = bos.toByteArray();
        } catch (Exception e) {
            DebugLog.loge(e);
        }

        return multipartBody;
    }

    static byte[] readFile(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];

        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (Exception e) {
            DebugLog.loge(e);
        }
        return bytes;
    }

    final String twoHyphens = "--";
    final String lineEnd = "\r\n";
    String boundary = "apiclient-" + System.currentTimeMillis();
    String mimeType = "multipart/form-data;boundary=" + boundary;

    void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws Exception {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.write(String.valueOf(parameterValue + lineEnd).getBytes("UTF-8"));
    }

    void buildPart(DataOutputStream dataOutputStream, String parameterName, String fileName, byte[] fileData) throws Exception {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"; filename=\"" + fileName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // read file and write it into form...
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }

    public static HashMap<String, String> parseToHashMap(JSONObject data){
        HashMap<String, String>  params = new HashMap<String, String>();
        Iterator<String> keys = data.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            params.put(key, UtilLibs.getStringInJsonObj(data, key));
        }
        return params;
    }
}