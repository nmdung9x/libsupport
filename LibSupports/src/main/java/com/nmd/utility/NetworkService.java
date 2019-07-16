package com.nmd.utility;

import android.content.Context;
import android.content.ContextWrapper;

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
import com.nmd.utility.other.MultipartRequest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nmd9x on 10/12/17.
 */

public class NetworkService extends ContextWrapper {
    Context context = null;
    private RequestQueue mRequestQueue = null;

    public NetworkService(Context ctx) {
        super(ctx);
        context = ctx;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public interface OnGetResult {
        void result(String response);
        void networkResponse(NetworkResponse networkResponse);
        void volleyError(VolleyError error);
        void error(Exception exception);
    }

    public void cancelRequest(String tagRequest) {
        mRequestQueue.cancelAll(tagRequest);
    }

    public void get(final String url, final HashMap<String, String> headers, final OnGetResult callback) {
        get(url, headers, "", false, callback);
    }

    public void get(final String url, final HashMap<String, String> headers, String tagRequest, boolean singleRequest, final OnGetResult callback) {
        DebugLog.logn("url:\n"+url);
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
                callback.volleyError(error);
            }
        }) {

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                callback.networkResponse(response);
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

    public void post(String url, final String body, final HashMap<String, String> headers, final OnGetResult callback) {
        post(url, body, headers, "", false, callback);
    }

    public void post(String url, final String body, final HashMap<String, String> headers, String tagRequest, boolean singleRequest, final OnGetResult callback) {
        DebugLog.logn("url:\n"+url);
        DebugLog.logn("body:\n"+body);
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
                callback.volleyError(error);
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                callback.networkResponse(response);
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
                callback.volleyError(error);
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                callback.networkResponse(response);
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
                String strResponse = "";
                try {
                    if (response != null) {
                        strResponse = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        DebugLog.loge(strResponse);
                    } else {
                        DebugLog.loge("response == null");
                    }
                } catch (Exception e) {
                    DebugLog.loge(e);
                    callback.error(e);
                }
                callback.networkResponse(response);
                callback.result(strResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.loge(error);
                callback.volleyError(error);
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

    public byte[] multipartBody(HashMap<String, String> params, HashMap<String, File> files){
        byte[] multipartBody = null;

        boundary = "apiclient-" + System.currentTimeMillis();
        mimeType = "multipart/form-data;boundary=" + boundary;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            if (params.size() > 0) {
                for (HashMap.Entry<String,String> entry : params.entrySet()) {
                    buildTextPart(dos, entry.getKey(), entry.getValue());
                }
            }
            if (files.size() > 0) {
                for (HashMap.Entry<String,File> entry : files.entrySet()) {
                    String path = entry.getValue().getPath();
                    DebugLog.loge("path:\n" +path);
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
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\""+parameterName+"\"; filename=\"" + fileName + "\"" + lineEnd);
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
}