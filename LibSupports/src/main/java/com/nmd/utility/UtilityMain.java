package com.nmd.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.nmd.utility.other.FileUtils;
import com.nmd.utility.other.UnCaughtException;

import java.io.File;
import java.text.SimpleDateFormat;

@SuppressLint("SimpleDateFormat")
public class UtilityMain {
	public static Context mContext;
	public static boolean isLoadContextMain = false;
	static String packageName = "";
	static String versionName = "";
	static String versionCode = "";
	public static String BUILDDAY = "";
	public static String FULLNAME_LOG = "";

	public static boolean DEBUG_D = true;
	public static boolean DEBUG_E = true;
	public static boolean DEBUG_EXCEPTION = true;
	public static boolean DEBUG_N = false;
	public static boolean DEBUG_I = false;

	public static boolean CRASH_RECORD = true;

	public static boolean isRecordLog = false;

	public static boolean SHOW_LOG_D = true;
	public static boolean SHOW_LOG_E = true;
	public static boolean SHOW_LOG_EXCEPTION = true;
	public static boolean SHOW_LOG_N = true;
	public static boolean SHOW_LOG_I = true;

	public static String TAG = "log";

	public static File logFile() {
		if (mContext == null) {
			DebugLog.logi("DebugLog", "UtilityMain.mContext == null");
			return null;
		}
		return new File(FileUtils.getAppDataDir(mContext) + File.separator + "log.log");
	}

	public static File logCrashFile() {
		if (mContext == null) {
			DebugLog.logi("DebugLog", "UtilityMain.mContext == null");
			return null;
		}
		return new File(FileUtils.getAppDataDir(mContext) + File.separator + "crash.log");
	}

	static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM_HH:mm:ss");
	static SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	static void setPackageName(String name) {
		if (name.equals("")) {
			packageName = "log-libs";
		} else {
			packageName = name;
		}

		TAG = packageName.replace(".", "-");
		isLoadContextMain = true;
	}
	
	public static class Builder {
		public Builder(Context context) {
			if (context == null) {
                throw new IllegalArgumentException("No context supplied.");
            }
			mContext = context;
			PackageManager pm = mContext.getPackageManager();
			PackageInfo pi;
			try {
				pi = pm.getPackageInfo(mContext.getPackageName(), 0);
				setPackageName(pi.packageName);
				versionCode = String.valueOf(pi.versionCode);
				versionName = pi.versionName;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public Builder setRecordLog(boolean check) {
			isRecordLog = check;
			return this;
		}
		
		public Builder setTAG(String check) {
			TAG = check;
			return this;
		}
		
		public Builder setDEBUG_D(boolean check) {
			DEBUG_D = check;
			return this;
		}
		
		public Builder setDEBUG_E(boolean check) {
			DEBUG_E = check;
			return this;
		}
		
		public Builder setDEBUG_N(boolean check) {
			DEBUG_N = check;
			return this;
		}
		
		public Builder setDEBUG_I(boolean check) {
			DEBUG_I = check;
			return this;
		}
		
		public Builder setCRASH_RECORD(boolean check) {
			CRASH_RECORD = check;
			return this;
		}
		
		public Builder setSHOW_LOG_D(boolean check) {
			SHOW_LOG_D = check;
			return this;
		}
		
		public Builder setSHOW_LOG_E(boolean check) {
			SHOW_LOG_E = check;
			return this;
		}
		
		public Builder setSHOW_LOG_EXCEPTION(boolean check) {
			SHOW_LOG_EXCEPTION = check;
			return this;
		}
		
		public Builder setSHOW_LOG_N(boolean check) {
			SHOW_LOG_N = check;
			return this;
		}
		
		public Builder setSHOW_LOG_I(boolean check) {
			SHOW_LOG_I = check;
			return this;
		}
		
		public Builder setBUILDDAY(String check) {
			BUILDDAY = check;
			return this;
		}
		
		public Builder setFULLNAME_LOG(String check) {
			FULLNAME_LOG = check;
			return this;
		}
		
		private final UtilityMain main = new UtilityMain();
		public UtilityMain build() {
            return main;
        }
	}
	
	public void start() {
		if (mContext == null) {
			DebugLog.loge("mContext null!?");
			return;
		}
		if (CRASH_RECORD)
			Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(mContext));
	}
	
	public void start(boolean isDebuggable) {
		if (mContext == null) {
			DebugLog.loge("mContext null!?");
			return;
		}
		if (!isDebuggable) {
			isRecordLog = true;
			DEBUG_D = false;
			DEBUG_N = false;
			DEBUG_I = false;
			DEBUG_E = false;
			DEBUG_EXCEPTION = true;
			CRASH_RECORD = true;
		}
		
		if (CRASH_RECORD)
			Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(mContext));
	}
	
	public static void start(Context ctx) {
		if (mContext == null) {
			DebugLog.loge("mContext null!?");
			return;
		}
		mContext = ctx;
		new Builder(mContext).build();
		
		if (CRASH_RECORD)
			Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(ctx));
	}

	public static void checkLog(long length) {
		File log = logFile();
		if (log == null) return;
		if (log.exists()) {
			DebugLog.loge("file log:" + String.valueOf(log.length() / 1000000) + "MB");
			if (log.length() > length) {
				DebugLog.loge("Delete file log!");
				try {
					log.delete();
				} catch (Exception e) {
					DebugLog.loge(e);
				}
			}
		}
		File logCrash = logCrashFile();
		if (logCrash == null) return;
		if (logCrash.exists()) {
			DebugLog.loge("file error:" + String.valueOf(logCrash.length() / 1000000) + "MB");
			if (logCrash.length() > length) {
				DebugLog.loge("Delete file error!");
				try {
					logCrash.delete();
				} catch (Exception e) {
					DebugLog.loge(e);
				}
			}
		}
	}
}
