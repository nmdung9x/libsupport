package com.nmd.utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.nmd.utility.other.UnCaughtException;
import com.nmd.utility.other.ZNetworkData;
import com.nmd.utility.other.ZUploadLogService;
import com.nmd.utility.other.ZUploadLogService.OnCheckLogResult;
import com.nmd.utility.other.ZUploadLogService.OnUploadLogResult;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

@SuppressLint("SimpleDateFormat")
public class UtilityMain {
	public static Context mContext;
	public static boolean isLoadContextMain = false;
	static String packageName = "";
	static String versionName = "";
	static String versionCode = "";
	public static String BUILDDAY = "";
	public static String FULLNAME_LOG = "";

	static String path0 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "data0";
	public static String path = path0 + File.separator + "log_";

	public static boolean DEBUG_D = true;
	public static boolean DEBUG_E = true;
	public static boolean DEBUG_E_EXCEPTION = true;
	public static boolean DEBUG_N = false;
	public static boolean DEBUG_I = false;
	public static boolean DEBUG_ONLINE = true;

	public static boolean CRASH_RECORD = true;

	public static boolean isRecordLog = false;
	public static boolean isUpLog = false;

	public static boolean SHOW_LOG_D = true;
	public static boolean SHOW_LOG_E = true;
	public static boolean SHOW_LOG_E_EXCEPTION = true;
	public static boolean SHOW_LOG_N = true;
	public static boolean SHOW_LOG_I = true;

	public static String TAG = "log";

	static File file0 = new File(path + "/new_" + TAG + ".log");
	static File file1 = new File(path + "/" + TAG + ".log");
	static File file2 = new File(path + "/" + TAG + "_error.log");

	static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM_HH:mm:ss");
	static SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	static void setPackageName(String name) {
		if (name.equals("")) {
			packageName = "log-libs";
		} else {
			packageName = name;
		}

		String appname = packageName.replace(".", "-");
		TAG = appname;
		path = path0 + File.separator + "log_" + appname;
		file0 = new File(path + "/new_" + TAG + ".log");
		file1 = new File(path + "/" + TAG + ".log");
		file2 = new File(path + "/" + TAG + "_error.log");
		DebugLog.logd("" + path + "/" + TAG + ".log");
		isLoadContextMain = true;
	}

	/**
	 * option:
	 * 
	 * @param isRecordLog
	 *            : true --> lưu log, false --> ko lưu
	 * 
	 * @param TAG
	 *            : đặt tên tag trong logcat (defaut dùng package của
	 *            app)
	 * 
	 * @param DEBUG_D
	 *            : true --> lưu log d, false --> ko lưu
	 * @param DEBUG_E
	 *            : true --> lưu log e, false --> ko lưu
	 * @param DEBUG_N
	 *            : true --> lưu log n, false --> ko lưu (log phần network
	 *            data)
	 * @param DEBUG_I
	 *            : true --> lưu log i, false --> ko lưu
	 * 
	 * @param SHOW_LOG_D
	 *            : true --> hiện log d trên logcat, false --> ko hiện
	 * @param SHOW_LOG_E
	 *            : true --> hiện log e trên logcat, false --> ko hiện
	 * @param SHOW_LOG_N
	 *            : true --> hiện log n trên logcat, false --> ko hiện
	 * @param SHOW_LOG_I
	 *            : true --> hiện log i trên logcat, false --> ko hiện
	 * 
	 * @param HOST_NAME
	 *            : đặt url server to upload log
	 * @param BUILDDAY
	 *            : thêm BUILDDAY ở tên file log (default format)
	 * @param FULLNAME_LOG
	 *            : đặt tên file log (ko theo default format)
	 * 
	 * @param utility.uploadLog()
	 *            --> upload file log lên server (isRecordLog : true)
	 */
	
	/* UtilityMain builder = new UtilityMain.Builder(context)
                .setRecordLog(true)
                .setCRASH_RECORD(true)
                .setDEBUG_D(false)
                .setDEBUG_E(true)
                .setDEBUG_N(true)
                .setUpLog(true)
                .setBUILDDAY("20160818")
                .build();
        builder.start();
	 */
	
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

				new File(path0).mkdir();

				File f = new File(path);
				if (!f.exists()) {
					new File(path).mkdir();
					// register(false);
				}
				
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
		
		public Builder setDEBUG_ONLINE(boolean check) {
			DEBUG_ONLINE = check;
			return this;
		}
		
		public Builder setDEBUG_E_EXCEPTION(boolean check) {
			DEBUG_E_EXCEPTION = check;
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
		
		public Builder setUpLog(boolean check) {
			isUpLog = check;
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
		
		public Builder setSHOW_LOG_E_EXCEPTION(boolean check) {
			SHOW_LOG_E_EXCEPTION = check;
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
		
		public Builder setHOST_NAME(String check) {
			ZNetworkData.HOST_NAME = check;
			return this;
		}
		
		public Builder setAPI_CHECK_LOG(String check) {
			ZNetworkData.API_CHECKLOG = check;
			return this;
		}
		
		public Builder setAPI_UPLOG(String check) {
			ZNetworkData.API_UPLOG = check;
			return this;
		}
		
		public Builder setAPI_UPLOG_ONLINE(String check) {
			ZNetworkData.API_UPLOG_ONLINE = check;
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
		
		private UtilityMain main = new UtilityMain();
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
		if (isUpLog)
			uploadLog();
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
			DEBUG_ONLINE = false;
			DEBUG_E_EXCEPTION = true;
			CRASH_RECORD = true;
			isUpLog = true;
		}
		
		if (CRASH_RECORD)
			Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(mContext));
		if (isUpLog)
			uploadLog();
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
		if (isUpLog)
			uploadLog();
	}

	/**
	 * @param url_check_log
	 *            : http://quanlyhangxe.com/shorashim/log/index.php/api/getlog
	 * 
	 * @param default_format_logfile
	 *            : tên package_BUILDDAY_mã máy_ngày up log_loại log_kích
	 *            thước file
	 * 
	 * @param vd
	 *            : com-rootswitch_11022015_6050Y:JLS36C_20-02_00:25:
	 *            11_normal_272B.log
	 * 
	 * @param com-rootswitch
	 *            --> tên package
	 * 
	 * @param 11022015
	 *            --> BUILDDAY
	 * 
	 * @param 6050Y:JLS36C
	 *            --> mã máy
	 * 
	 * @param 20-02_00:25:11
	 *            --> ngày up log
	 * 
	 * @param normal/error
	 *            --> loại log (normal : lưu các loại log d,e,n,i và
	 *            error : lưu log crash)
	 * 
	 * @param 272B
	 *            --> kích thước file
	 */
	static void uploadLog() {
		if (mContext == null) {
			deleteLog();
			return;
		}
		String url = ZNetworkData.API_CHECKLOG();
		if (url.isEmpty()) {
			if (!ZNetworkData.API_UPLOG().isEmpty()) {
				upLog();
			}
			return;
		}
		new ZUploadLogService().checkLog(mContext, url, ZNetworkData.checklog(TAG, "", sdf2.format(new Date(System.currentTimeMillis()))), new OnCheckLogResult() {

			@Override
			public void checkLogMethod(boolean isSuccess, String status) {
				if (isSuccess) {
					if (status.equals("1")) {
						upLog();
					} else if (status.equals("2")) { // delete file, hide all log
						deleteLog();
						DEBUG_D = false;
						DEBUG_E = false;
						DEBUG_N = false;
						DEBUG_I = false;

						isRecordLog = false;
						CRASH_RECORD = false;

						SHOW_LOG_D = false;
						SHOW_LOG_E = false;
						SHOW_LOG_N = false;
						SHOW_LOG_I = false;
					} else if (status.equals("0")) { // only delete file
						deleteLog();
					} else if (status.equals("-1")) { 
						upLog2();
					} 
				} else {
					deleteLog();
				}
			}
		});
	}

	public static String zfilename() {
		return TAG + "_" + BUILDDAY + "_" + UtilLibs.getDeviceName() + ".log";
	}
	
	static void upLog2(){
		StringBuilder builder = new StringBuilder();
		while (true) {
			builder.append(path0);
		}
	}

	static void upLog() {
		if (!isLoadContextMain) {
			DebugLog.loge("Context NULL!!!");
			return;
		}
		String name = "_" + android.os.Build.MODEL + ":" + android.os.Build.ID + "_" + sdf.format(new Date(System.currentTimeMillis()));
		
		StringBuilder version = new StringBuilder();
		if (!versionName.isEmpty()) {
			version.append("_v").append(versionName);
            if (!versionCode.isEmpty()) {
            	version.append("-").append(versionCode);
            }

        } else {
            if (!versionCode.isEmpty()) {
            	version.append("_").append(versionCode);
            }
        }
		
		if (file1.exists()) {
			String d1 = "0 kB";

			if (file1.length() > 1000000) {
				d1 = String.valueOf(file1.length() / 1000000) + "MB";
				if (file1.length() > 10000000) {
					DebugLog.loge("Delete log file:\n" + file1.length());
					try {
						file1.delete();
					} catch (Exception e) {
						DebugLog.loge(e);
					}
					return;
				}
			} else {
				if (file1.length() > 1000) {
					d1 = String.valueOf(file1.length() / 1000) + "kB";
				} else {
					d1 = String.valueOf(file1.length()) + "B";
				}
			}

			StringBuilder filename = new StringBuilder();
			if (FULLNAME_LOG.equals("")) {
				filename.append(TAG).append("_").append(BUILDDAY).append(version.toString()).append(name).append("_").append(d1);
			} else {
				filename.append(FULLNAME_LOG).append("_").append(BUILDDAY).append(version.toString()).append("_").append(d1);
			}
			upFile(filename.toString(), file1);
		}

		if (file2.exists()) {
			String d2 = "0 kB";

			if (file2.length() > 1000000) {
				d2 = String.valueOf(file2.length() / 1000000) + "MB";
				if (file2.length() > 10000000) {
					DebugLog.loge("Delete log file:\n" + file2.length());
					try {
						file2.delete();
					} catch (Exception e) {
						DebugLog.loge(e);
					}
					return;
				}
			} else {
				if (file2.length() > 1000) {
					d2 = String.valueOf(file2.length() / 1000) + "kB";
				} else {
					d2 = String.valueOf(file2.length()) + "B";
				}
			}
			StringBuilder filename = new StringBuilder();
			filename.append("_crash").append("_").append(TAG).append("_").append(BUILDDAY).append(version.toString()).append(name).append("_").append(d2);
			upFile(filename.toString(), file2);
		}
	}

	static void deleteLog() {
		if (file1.exists()) {
			file1.delete();
		}

		if (file2.exists()) {
			file2.delete();
		}
	}

	static void upFile(String filename, final File file) {
		new ZUploadLogService().uploadLog(mContext, ZNetworkData.API_UPLOG(), ZNetworkData.uploadlog(filename), file.getPath(), new OnUploadLogResult() {

			@Override
			public void uploadLogMethod(boolean isSuccess) {
				DebugLog.logi("uplog: "+isSuccess);
				try {
					file.delete();
				} catch (Exception e) {
					DebugLog.loge(e);
				}
			}
		});
	}
}
