package com.nmd.utility;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DebugLog {
	enum Type {
		v,
		d,
		i,
		w,
		e
	}

	public static void log(Type type, String tag, Object msg){
		log(type, tag, "", msg);
	}
	
	public static void log(Type type, String tag, String msg, Object exc){
		boolean isException = false;
		String messageException = "";

		if (exc != null) {
			if (exc instanceof Exception) {
				isException = true;
				Exception e = (Exception) exc;
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));

				messageException = errors.toString();
			} else if (exc instanceof Throwable) {
				isException = true;
				Throwable e = (Throwable) exc;
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));

				messageException = errors.toString();
			} else messageException = String.valueOf(exc);
		}

		String message = msg.isEmpty() ? messageException : msg + "\n" + messageException;

    	StackTraceElement stack = Thread.currentThread().getStackTrace()[4];
        String fullClassName = stack.getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);

        if (className.contains("$")) {
            className = className.substring(0, className.lastIndexOf("$"));
        }

        String methodName = stack.getMethodName();
        int lineNumber = stack.getLineNumber();
        
        String check = "at ("+ className + ".java:" + lineNumber + ") " + "[" + methodName + "] ";

		switch (type) {
			case v:
				if (UtilityMain.SHOW_LOG_D || (isException && UtilityMain.SHOW_LOG_EXCEPTION)) {
					Log.v(tag, check + message);
				}
				if (UtilityMain.DEBUG_D || (isException && UtilityMain.SHOW_LOG_EXCEPTION)) {
					appendLog("Log---v " + check + message);
				}
				break;

			case d:
				if (UtilityMain.SHOW_LOG_D || (isException && UtilityMain.SHOW_LOG_EXCEPTION)) {
					Log.d(tag, check + message);
				}
				if (UtilityMain.DEBUG_D || (isException && UtilityMain.SHOW_LOG_EXCEPTION)) {
					appendLog("Log---d " + check + message);
				}
				break;

			case i:
				if (UtilityMain.SHOW_LOG_I || (isException && UtilityMain.SHOW_LOG_EXCEPTION)) {
					Log.i(tag, check + message);
				}
				if (UtilityMain.DEBUG_I || (isException && UtilityMain.SHOW_LOG_EXCEPTION)) {
					appendLog("Log---i " + check + message);
				}
				break;

			case w:
				if (UtilityMain.SHOW_LOG_I || (isException && UtilityMain.SHOW_LOG_EXCEPTION)) {
					Log.w(tag, check + message);
				}
				if (UtilityMain.DEBUG_I || (isException && UtilityMain.SHOW_LOG_EXCEPTION)) {
					appendLog("Log---w " + check + message);
				}
				break;

			case e:
				if (UtilityMain.SHOW_LOG_E || (isException && UtilityMain.SHOW_LOG_EXCEPTION)) {
					Log.e(tag, check + message);
				}
				if (UtilityMain.DEBUG_E || (isException && UtilityMain.SHOW_LOG_EXCEPTION)) {
					appendLog("Log---e " + check + message);
				}
				break;

			default:
				break;
		}
	}

	public static void logv(Object obj){
		log(Type.v, UtilityMain.TAG, obj);
	}

	public static void logv(Object msg, Object e){
		log(Type.v, UtilityMain.TAG, String.valueOf(msg), e);
	}

	public static void logv(String tag, Object obj){
		log(Type.v, tag, obj);
	}

	public static void logv(String tag, String msg, Object e){
		log(Type.v, tag, msg, e);
	}

	public static void logd(Object obj){
		log(Type.d, UtilityMain.TAG, obj);
	}

	public static void logd(Object msg, Object e){
		log(Type.d, UtilityMain.TAG, String.valueOf(msg), e);
	}

	public static void logd(String tag, Object obj){
		log(Type.d, tag, obj);
	}

	public static void logd(String tag, String msg, Object e){
		log(Type.d, tag, msg, e);
	}

	public static void logi(Object obj){
		log(Type.i, UtilityMain.TAG, obj);
	}

	public static void logi(Object msg, Object e){
		log(Type.i, UtilityMain.TAG, String.valueOf(msg), e);
	}

	public static void logi(String tag, Object obj){
		log(Type.i, tag, obj);
	}

	public static void logi(String tag, String msg, Object e){
		log(Type.i, tag, msg, e);
	}

	public static void loge(Object obj){
		log(Type.e, UtilityMain.TAG, obj);
	}

	public static void loge(Object msg, Object e){
		log(Type.e, UtilityMain.TAG, String.valueOf(msg), e);
	}

	public static void loge(String tag, Object obj){
		log(Type.e, tag, obj);
	}

	public static void loge(String tag, String msg, Object e){
		log(Type.e, tag, msg, e);
	}

	public static void logw(Object obj){
		log(Type.w, UtilityMain.TAG, obj);
	}

	public static void logw(Object msg, Object e){
		log(Type.w, UtilityMain.TAG, String.valueOf(msg), e);
	}

	public static void logw(String tag, Object obj){
		log(Type.w, tag, obj);
	}

	public static void logw(String tag, String msg, Object e){
		log(Type.w, tag, msg, e);
	}
    
	static void appendLog(String text) {
		if (!UtilityMain.isRecordLog) {
			return;
		}

		File logFile = UtilityMain.logFile();
		if (logFile == null) return;
		boolean isNew = false;
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
				isNew = true;
			} catch (IOException e) {
				logi("DebugLog", "Can't create log file in AppDataDir");
				return;
			}
		}
		try {
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
			if (isNew) {
				buf.append(UtilLibs.getInfoDevices()).append('\n');
			}
			buf.append(getCurrentTime() + " (" + UtilLibs.getTimeZoneInLocal() + ")" + "_" + text).append('\n');
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    @SuppressLint("SimpleDateFormat")
	private static String getCurrentTime(){
    	SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
        Date resultdate = new Date(System.currentTimeMillis());
		return sdf.format(resultdate);
    }
}
