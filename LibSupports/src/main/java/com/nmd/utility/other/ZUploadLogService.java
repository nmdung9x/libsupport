package com.nmd.utility.other;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nmd.utility.DebugLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ZUploadLogService {

	public interface OnUploadLogResult {
		void uploadLogMethod(boolean isSuccess);
	}

	static RequestQueue mRequestQueue_uploadLog = null;
	private static String request_check_time_uploadLog = "";

	public void uploadLog(final Context context, final String url, final ArrayList<Data> data, final String file, final OnUploadLogResult onUploadLogResult) {
		if (context == null) {
			DebugLog.loge("context == null");
			onUploadLogResult.uploadLogMethod(false);
			return;
		}
		if (url.isEmpty()) {
			DebugLog.loge("url emplty");
			return;
		}
		mRequestQueue_uploadLog = Volley.newRequestQueue(context);

		ArrayList<Data> files = new ArrayList<Data>();
		files.add(new Data("file", file));

		byte[] multipartBody = Data.multipartBody(data, files);

		if (multipartBody == null) {
			DebugLog.loge("multipartBody == null");
			onUploadLogResult.uploadLogMethod(false);
			return;
		}

		MultipartRequest multipartRequest = new MultipartRequest(url, null, Data.mimeType, multipartBody, new Response.Listener<NetworkResponse>() {
			@Override
			public void onResponse(NetworkResponse response) {
				try {
					String result = new String(response.data, "UTF-8");
					DebugLog.loge("upload log response:\n" + result);
					JSONObject js = new JSONObject(result);

					int code = js.getInt("code");
					if (code == 1) {
						onUploadLogResult.uploadLogMethod(true);
					} else {
						onUploadLogResult.uploadLogMethod(false);
					}

				} catch (Exception e) {
					DebugLog.loge(e);
					onUploadLogResult.uploadLogMethod(false);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				DebugLog.loge(error.toString());
				onUploadLogResult.uploadLogMethod(false);
			}
		});

		multipartRequest.setShouldCache(false);
		if (request_check_time_uploadLog.equals("")) {
			request_check_time_uploadLog = "" + System.currentTimeMillis();
			multipartRequest.setTag(request_check_time_uploadLog);
			mRequestQueue_uploadLog.add(multipartRequest);
		} else {
			mRequestQueue_uploadLog.cancelAll(request_check_time_uploadLog);
			request_check_time_uploadLog = "" + System.currentTimeMillis();
			multipartRequest.setTag(request_check_time_uploadLog);
			mRequestQueue_uploadLog.add(multipartRequest);
		}
	}

	static RequestQueue mRequestQueue_uploadLogV2 = null;
	private static String request_check_time_uploadLogV2 = "";

	public void uploadLogV2(final Context context, final String url, final ArrayList<Data> data, final OnUploadLogResult onUploadLogResult) {
		if (context == null) {
			DebugLog.loge("context == null");
			onUploadLogResult.uploadLogMethod(false);
			return;
		}
		if (url.isEmpty()) {
			DebugLog.loge("url emplty");
			return;
		}
		mRequestQueue_uploadLogV2 = Volley.newRequestQueue(context);

		StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject result = new JSONObject(response);
					int code = result.getInt("code");
					if (code == 1) {
						onUploadLogResult.uploadLogMethod(true);
					} else {
						onUploadLogResult.uploadLogMethod(false);
					}
				} catch (Exception e) {
					DebugLog.loge(e);
					onUploadLogResult.uploadLogMethod(false);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				DebugLog.loge(error);
				onUploadLogResult.uploadLogMethod(false);
			}
		}) {
			@Override
			protected Map<String, String> getParams() {
				return Data.parseToHashMap(data);
			}
		};

		stringRequest.setShouldCache(false);
		if (request_check_time_uploadLogV2.equals("")) {
			request_check_time_uploadLogV2 = "" + System.currentTimeMillis();
			stringRequest.setTag(request_check_time_uploadLogV2);
			mRequestQueue_uploadLogV2.add(stringRequest);
		} else {
			mRequestQueue_uploadLogV2.cancelAll(request_check_time_uploadLogV2);
			request_check_time_uploadLogV2 = "" + System.currentTimeMillis();
			stringRequest.setTag(request_check_time_uploadLogV2);
			mRequestQueue_uploadLogV2.add(stringRequest);
		}
	}

	public interface OnCheckLogResult {
		void checkLogMethod(boolean isSuccess, String status);
	}

	static RequestQueue mRequestQueue_checkLog = null;
	private static String request_check_time_checkLog = "";

	public void checkLog(Context context, final String url, final ArrayList<Data> data, final OnCheckLogResult onCheckLogResult) {
		if (context == null) {
			DebugLog.loge("context == null");
			onCheckLogResult.checkLogMethod(true, "0");
			return;
		}
		if (url.isEmpty()) {
			DebugLog.loge("url emplty");
			return;
		}
		mRequestQueue_checkLog = Volley.newRequestQueue(context);

		StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject result = new JSONObject(response);
					int code = result.getInt("code");
					if (code == 1) {
						if (result.has("result")) {
							JSONObject jsonResult = result.getJSONObject("result");
							if (jsonResult.has("status")) {
								String status = jsonResult.getString("status");
								onCheckLogResult.checkLogMethod(true, status);
							} else {
								onCheckLogResult.checkLogMethod(true, "0");
							}
						} else {
							onCheckLogResult.checkLogMethod(false, "0");
						}

					} else {
						onCheckLogResult.checkLogMethod(false, "0");
					}
				} catch (Exception e) {
					DebugLog.loge(e);
					onCheckLogResult.checkLogMethod(false, "0");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				DebugLog.loge(error);
				onCheckLogResult.checkLogMethod(false, "0");
			}
		}) {
			@Override
			protected Map<String, String> getParams() {
				return Data.parseToHashMap(data);
			}
		};

		stringRequest.setShouldCache(false);
		if (request_check_time_checkLog.equals("")) {
			request_check_time_checkLog = "" + System.currentTimeMillis();
			stringRequest.setTag(request_check_time_checkLog);
			mRequestQueue_checkLog.add(stringRequest);
		} else {
			mRequestQueue_checkLog.cancelAll(request_check_time_checkLog);
			request_check_time_checkLog = "" + System.currentTimeMillis();
			stringRequest.setTag(request_check_time_checkLog);
			mRequestQueue_checkLog.add(stringRequest);
		}

	}
}
