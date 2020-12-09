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
		n,
		w,
		e
	}
	
	public static String log(Type type, String tag, Object msg){
    	if (msg == null) msg = "";
    	if (String.valueOf(msg).trim().isEmpty()) return "";
		String message;
		boolean isException = false;

		if (msg instanceof Exception) {
			isException = true;
			Exception e = (Exception) msg;
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));

			message = errors.toString();
		} else if (msg instanceof Throwable) {
			isException = true;
			Throwable e = (Throwable) msg;
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));

			message = errors.toString();
		} else {
			message = String.valueOf(msg).trim();
		}

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

			case n:
				if (UtilityMain.SHOW_LOG_N || (isException && UtilityMain.SHOW_LOG_EXCEPTION)) {
					Log.i(tag, check + message);
				}
				if (UtilityMain.DEBUG_N || (isException && UtilityMain.SHOW_LOG_EXCEPTION)) {
					appendLog("Log---n " + check + message);
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
        return check + message;
    }
	
    public static void logv(Object obj){
        log(Type.v, UtilityMain.TAG, obj);
    }
    
    public static void logv(String tag, Object obj){
    	log(Type.v, tag, obj);
    }
	
    public static void logd(Object obj){
        log(Type.d, UtilityMain.TAG, obj);
    }
    
    public static void logd(String tag, Object obj){
    	log(Type.d, tag, obj);
    }
	
    public static void logi(Object obj){
        log(Type.i, UtilityMain.TAG, obj);
    }
    
    public static void logi(String tag, Object obj){
    	log(Type.i, tag, obj);
    }

    public static void logn(Object obj){
        log(Type.n, UtilityMain.TAG, obj);
    }
    
    public static void logn(String tag, Object obj){
    	log(Type.n, tag, obj);
    }

    public static void logw(Object obj){
        log(Type.w, UtilityMain.TAG, obj);
    }
    
    public static void logw(String tag, Object obj){
    	log(Type.w, tag, obj);
    }
	
    public static void loge(Object obj){
        log(Type.e, UtilityMain.TAG, obj);
    }
    
    public static void loge(String tag, Object obj){
    	log(Type.e, tag, obj);
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
