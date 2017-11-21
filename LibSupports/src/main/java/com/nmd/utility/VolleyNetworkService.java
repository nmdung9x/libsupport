package com.nmd.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nmd.utility.other.Data;
import com.nmd.utility.other.MultipartRequest;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

public class VolleyNetworkService {
	private RequestQueue mRequestQueue = null;
	private final String TAG = "VolleyNetworkService";

	public <T> void addToRequestQueue(Context context, Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue(context).add(req);
	}

	public RequestQueue getRequestQueue(Context context) {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(context);
		}
		return mRequestQueue;
	}

	/*
	 * TODO Method POST
	 */
	public void getResponseFromRequestPost(Context context, final String url, String TAG_STRING_REQUEST, final ArrayList<Data> arrData, boolean cancelAllRequest,
			final AsyncTask<Object, Object, Object[]> handlerRequest) {
		StringRequest stringRequest = new StringRequest(Method.POST, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// Call asynctask process response data
				handlerRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				DebugLog.loge("VolleyError:\n" + error);
				error.printStackTrace();
				// Call asynctask process response data
				handlerRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(error));
			}
		}) {
			@Override
			protected Response<String> parseNetworkResponse(NetworkResponse response) {
				try {
					String utf8String = new String(response.data, "UTF-8");
					return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
				} catch (Exception e) {
				}
				return null;
			}

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// Add data to request
				HashMap<String, String> params = new HashMap<String, String>();
				DebugLog.logn("Url: " + url);
				for (int i = 0; i < arrData.size(); i++) {
					Data data = arrData.get(i);
					params.put(data.getKey(), data.getValue());
					DebugLog.logn(data.getKey() + ": " + data.getValue());
				}
				return params;
			}
		};

		// Set timeout
		int socketTimeout = 10 * 1000;
		RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		stringRequest.setRetryPolicy(policy);
		// Disable cache
		stringRequest.setShouldCache(false);
		// Cancel all request with TAG
		if (cancelAllRequest) {
			getRequestQueue(context).cancelAll(TAG_STRING_REQUEST);
		}
		// Adding request to request queue
		addToRequestQueue(context, stringRequest, TAG_STRING_REQUEST);
	}

	/*
	 * TODO Method POST Multipart
	 */
	public void getResponseFromRequestPostMultipart(Context context, String url, String TAG_STRING_REQUEST, final ArrayList<Data> arrData, String keyOfFile, String pathFile, boolean cancelAllRequest,
			final AsyncTask<Object, Object, Object[]> handlerRequest) {

		ArrayList<Data> files = new ArrayList<Data>();
		files.add(new Data(keyOfFile, pathFile));

		DebugLog.logn("Url: " + url);
		byte[] multipartBody = Data.multipartBody(arrData, files);

		MultipartRequest multipartRequest = new MultipartRequest(url, null, Data.mimeType, multipartBody, new Response.Listener<NetworkResponse>() {
			@Override
			public void onResponse(NetworkResponse response) {
				try {
					String result = new String(response.data, "UTF-8");
					// Call asynctask process response data
					handlerRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);
				} catch (Exception e) {
					DebugLog.loge(e);
					handlerRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, e.toString());
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				DebugLog.loge("VolleyError:\n" + error);
				error.printStackTrace();
				// Call asynctask process response data
				handlerRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(error));
			}
		});

		// Set timeout
		int socketTimeout = 30 * 1000;
		RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		multipartRequest.setRetryPolicy(policy);
		// Disable cache
		multipartRequest.setShouldCache(false);
		// Cancel all request with TAG
		if (cancelAllRequest) {
			getRequestQueue(context).cancelAll(TAG_STRING_REQUEST);
		}
		// Adding request to request queue
		addToRequestQueue(context, multipartRequest, TAG_STRING_REQUEST);
	}

	/*
	 * TODO Method POST MultiFile
	 */
	public void getResponseFromRequestPostMultiFile(Context context, String url, String TAG_STRING_REQUEST, final ArrayList<Data> arrData, ArrayList<String> arrKeyOfFile,
			ArrayList<String> arrPathFile, boolean cancelAllRequest, final AsyncTask<Object, Object, Object[]> handlerRequest) {

		ArrayList<Data> files = new ArrayList<Data>();
		int fileCount = arrKeyOfFile.size();
		if (arrKeyOfFile.size() > arrPathFile.size()) {
			fileCount = arrPathFile.size();
		}
		for (int i = 0; i < fileCount; i++) {
			try {
				files.add(new Data(arrKeyOfFile.get(i), arrPathFile.get(i)));
			} catch (Exception e) {
			}
		}

		byte[] multipartBody = Data.multipartBody(arrData, files);

		MultipartRequest multipartRequest = new MultipartRequest(url, null, Data.mimeType, multipartBody, new Response.Listener<NetworkResponse>() {
			@Override
			public void onResponse(NetworkResponse response) {
				try {
					String result = new String(response.data, "UTF-8");
					// Call asynctask process response data
					handlerRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);
				} catch (Exception e) {
					DebugLog.loge(e);
					handlerRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, e.toString());
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				DebugLog.loge("VolleyError:\n" + error);
				error.printStackTrace();
				// Call asynctask process response data
				handlerRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(error));
			}
		});

		// Set timeout
		int socketTimeout = 60 * 1000;
		RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		multipartRequest.setRetryPolicy(policy);
		// Disable cache
		multipartRequest.setShouldCache(false);
		// Cancel all request with TAG
		if (cancelAllRequest) {
			getRequestQueue(context).cancelAll(TAG_STRING_REQUEST);
		}
		// Adding request to request queue
		addToRequestQueue(context, multipartRequest, TAG_STRING_REQUEST);
	}

	/*
	 * TODO Method GET
	 */
	public void getResponseFromRequestGet(Context context, final String url, String TAG_STRING_REQUEST, final ArrayList<Data> arrData, boolean cancelAllRequest,
			final AsyncTask<Object, Object, Object[]> handlerRequest) {
		StringRequest stringRequest = new StringRequest(Method.GET, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// Call asynctask process response data
				handlerRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				DebugLog.loge("VolleyError:\n" + error);
				error.printStackTrace();
				handlerRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(error));
			}
		}) {
			@Override
			protected Response<String> parseNetworkResponse(NetworkResponse response) {
				try {
					String utf8String = new String(response.data, "UTF-8");
					return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
				} catch (Exception e) {
				}
				return null;
			}

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// Add data to request
				HashMap<String, String> params = new HashMap<String, String>();
				DebugLog.logn("Url: " + url);
				for (int i = 0; i < arrData.size(); i++) {
					Data data = arrData.get(i);
					params.put(data.getKey(), data.getValue());
					DebugLog.logn(data.getKey() + ": " + data.getValue());
				}
				return params;
			}
		};

		// Set timeout
		int socketTimeout = 10 * 1000;
		RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		stringRequest.setRetryPolicy(policy);
		// Disable cache
		stringRequest.setShouldCache(false);
		// Cancel all request with TAG
		if (cancelAllRequest) {
			getRequestQueue(context).cancelAll(TAG_STRING_REQUEST);
		}
		// Adding request to request queue
		addToRequestQueue(context, stringRequest, TAG_STRING_REQUEST);
	}

	/*
	 * TODO Method POST request headers
	 */
	public void getResponseFromRequestPostHeader(Context context, final String url, String TAG_STRING_REQUEST, final ArrayList<Data> arrData, boolean cancelAllRequest,
			final ArrayList<Data> arrHeaders, final AsyncTask<Object, Object, Object[]> handlerRequest) {
		StringRequest stringRequest = new StringRequest(Method.POST, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// Call asynctask process response data
				handlerRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				DebugLog.loge("VolleyError:\n" + error);
				error.printStackTrace();
				handlerRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(error));
			}
		}) {
			@Override
			protected Response<String> parseNetworkResponse(NetworkResponse response) {
				try {
					String utf8String = new String(response.data, "UTF-8");
					return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
				} catch (Exception e) {
				}
				return null;
			}

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// Add data to request
				HashMap<String, String> params = new HashMap<String, String>();
				DebugLog.logn("Url: " + url);
				for (int i = 0; i < arrData.size(); i++) {
					Data data = arrData.get(i);
					params.put(data.getKey(), data.getValue());
					DebugLog.logn(data.getKey() + ": " + data.getValue());
				}
				return params;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				// Passing some request headers
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Content-Type", "application/json");
				for (int i = 0; i < arrHeaders.size(); i++) {
					headers.put(arrHeaders.get(i).getKey(), arrHeaders.get(i).getValue());
				}
				return headers;
			}
		};

		// Set timeout
		int socketTimeout = 20 * 1000;
		RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		stringRequest.setRetryPolicy(policy);
		// Disable cache
		stringRequest.setShouldCache(false);
		// Cancel all request with TAG
		if (cancelAllRequest) {
			getRequestQueue(context).cancelAll(TAG_STRING_REQUEST);
		}
		// Adding request to request queue
		addToRequestQueue(context, stringRequest, TAG_STRING_REQUEST);
	}

}
