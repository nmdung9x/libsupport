package com.nmd.utility.other;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.nmd.utility.DebugLog;
import com.nmd.utility.NetworkService;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ZUploadLogService {

	public interface OnUploadLogResult {
		void uploadLogMethod(boolean isSuccess);
	}

	public void uploadLog(final Context context, final String url, final ArrayList<Data> data, final File file, final OnUploadLogResult onUploadLogResult) {
		if (context == null) {
			DebugLog.loge("context == null");
			onUploadLogResult.uploadLogMethod(false);
			return;
		}
		if (url.isEmpty()) {
			DebugLog.loge("url emplty");
			return;
		}

		HashMap<String, File> files = new HashMap<String, File>();
		files.put("file", file);

		new NetworkService(context).post(url, parseToHashMap(data), files, null, new NetworkService.OnGetResult() {
			@Override
			public void result(String response) {
				try {
					DebugLog.loge("upload log response:\n" + response);
					JSONObject js = new JSONObject(response);

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

			@Override
			public void networkResponse(NetworkResponse networkResponse) {
				DebugLog.loge(networkResponse);
				onUploadLogResult.uploadLogMethod(false);
			}

			@Override
			public void volleyError(VolleyError error) {
				DebugLog.loge(error);
				onUploadLogResult.uploadLogMethod(false);
			}

			@Override
			public void error(Exception exception) {
				DebugLog.loge(exception);
				onUploadLogResult.uploadLogMethod(false);
			}
		});
	}

	public interface OnCheckLogResult {
		void checkLogMethod(boolean isSuccess, String status);
	}

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

		new NetworkService(context).post(url, parseToHashMap(data), null, new NetworkService.OnGetResult() {
			@Override
			public void result(String response) {
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

			@Override
			public void networkResponse(NetworkResponse networkResponse) {
				DebugLog.loge(networkResponse);
				onCheckLogResult.checkLogMethod(false, "0");
			}

			@Override
			public void volleyError(VolleyError error) {
				DebugLog.loge(error);
				onCheckLogResult.checkLogMethod(false, "0");
			}

			@Override
			public void error(Exception exception) {
				DebugLog.loge(exception);
				onCheckLogResult.checkLogMethod(false, "0");
			}
		});
	}

	HashMap<String, String> parseToHashMap(ArrayList<Data> data){
		HashMap<String, String>  params = new HashMap<String, String>();
		for (Data obj : data) {
			params.put(obj.getKey(), obj.getValue());
		}
		return params;
	}
}
