package com.nmd.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharedPreference {
	static SharedPreferences sharedPreferences;

	public static String get(Context context, Object key, String defData) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			return sharedPreferences.getString(String.valueOf(key), defData);
		} catch (Exception e) {
			DebugLog.loge(e);
			return defData;
		}
	}
	
	public static String get(Object key, String defData) {
		if (UtilityMain.mContext != null) {
			return get(UtilityMain.mContext, key, defData);
		}
		return defData;
	}

	public static void set(Context context, Object key, String data) {
		try {
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = sharedPreferences.edit();
			editor.putString(String.valueOf(key), data);
			editor.commit();
		} catch (Exception e) {
			DebugLog.loge(e);
		}
	}
	
	public static void set(Object key, String data) {
		if (UtilityMain.mContext != null) {
			set(UtilityMain.mContext, key, data);
		}
	}

	// Long
	public static Long getLong(Context context, Object key) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			return sharedPreferences.getLong(String.valueOf(key), 0);
		} catch (Exception e) {
			DebugLog.loge(e);
			return (long) 0;
		}
	}
	
	public static Long getLong(Context context, Object key, Long defData) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			return sharedPreferences.getLong(String.valueOf(key), defData);
		} catch (Exception e) {
			DebugLog.loge(e);
			return defData;
		}
	}

	public static void setLong(Context context, Object key, Long data) {
		try {
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = sharedPreferences.edit();
			editor.putLong(String.valueOf(key), data);
			editor.commit();
		} catch (Exception e) {
			DebugLog.loge(e);
		}
	}

	// Boolean
	public static Boolean getBoolean(Context context, Object key, Boolean defData) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			return sharedPreferences.getBoolean(String.valueOf(key), defData);
		} catch (Exception e) {
			DebugLog.loge(e);
			return defData;
		}
	}

	public static void setBoolean(Context context, Object key, Boolean data) {
		try {
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = sharedPreferences.edit();
			editor.putBoolean(String.valueOf(key), data);
			editor.commit();
		} catch (Exception e) {
			DebugLog.loge(e);
		}
	}

	// Integer
	public static Integer getInt(Context context, Object key) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			return sharedPreferences.getInt(String.valueOf(key), 0);
		} catch (Exception e) {
			DebugLog.loge(e);
			return 0;
		}
	}
	
	public static Integer getInt(Context context, Object key, Integer defData) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			return sharedPreferences.getInt(String.valueOf(key), defData);
		} catch (Exception e) {
			DebugLog.loge(e);
			return defData;
		}
	}

	public static void setInt(Context context, Object key, Integer data) {
		try {
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = sharedPreferences.edit();
			editor.putInt(String.valueOf(key), data);
			editor.commit();
		} catch (Exception e) {
			DebugLog.loge(e);
		}
	}

	public static String getE(Context context, Object key, String defData, byte[] pass_key_encrypt) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			return UtilLibs.AESdecrypt(pass_key_encrypt, sharedPreferences.getString(String.valueOf(key), defData));
		} catch (Exception e) {
			DebugLog.loge(e);
			return defData;
		}
	}

	public static void setE(Context context, Object key, String data, byte[] pass_key_encrypt) {
		try {
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = sharedPreferences.edit();
			editor.putString(String.valueOf(key), UtilLibs.AESencrypt(pass_key_encrypt, data));
			editor.commit();
		} catch (Exception e) {
			DebugLog.loge(e);
		}
	}

	public static String getL(Context context, Object key, String defData) {
		String data = "";

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			data = sharedPreferences.getString(String.valueOf(key), defData);
		} catch (Exception e) {
			DebugLog.loge(e);
			data = defData;
		}

		DebugLog.logi("load data with key: " + key + "\n" + data);
		return data;
	}

	public static void setL(Context context, Object key, String data) {
		DebugLog.logi("save data with key: " + key + "\n" + data);
		try {
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = sharedPreferences.edit();
			editor.putString(String.valueOf(key), data);
			editor.commit();
		} catch (Exception e) {
			DebugLog.loge(e);
		}
	}
}
